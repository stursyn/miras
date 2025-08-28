package kz.satpaev.sunkar.service;

import com.fazecast.jSerialComm.SerialPort;
import kz.satpaev.sunkar.model.dto.ImageRow;
import kz.satpaev.sunkar.model.dto.NiimbotPacket;
import kz.satpaev.sunkar.util.AppUtil;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;

import static kz.satpaev.sunkar.util.AppUtil.countPixelsForBitmapPacket;
import static kz.satpaev.sunkar.util.AppUtil.indexPixels;

public class NiimbotB1PrinterService implements AutoCloseable {
    private final SerialPort serialPort;
    public NiimbotB1PrinterService(String comPort, int rate) {
        for (SerialPort commPort : SerialPort.getCommPorts()) {
            System.out.println("CommPort: " + commPort.getSystemPortName() + " " + commPort.getDescriptivePortName());
        }

        serialPort = SerialPort.getCommPort(comPort);
        serialPort.setBaudRate(rate);

        if (!serialPort.openPort()) {
            throw new RuntimeException("Не удалось открыть порт " + comPort);
        }
    }

    @SneakyThrows
    private byte[] buildPacket(byte cmd, byte[] payload) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0x55); // header
        out.write(0x55); // header

        out.write(cmd);

        byte dataLength = (byte) payload.length;
        out.write(dataLength);

        out.write(payload);

        byte checksum = cmd;
        checksum ^= dataLength;
        for (byte b : payload) {
            checksum ^= b;
        }

        out.write(checksum);

        out.write(0xaa);//tail
        out.write(0xaa);//tail

        return out.toByteArray();
    }

    @SneakyThrows
    private NiimbotPacket sendPacketWaitResponse(byte cmd, byte[] payload) {
        byte[] packet = buildPacket(cmd, payload);
        serialPort.writeBytes(packet, packet.length);
        System.out.println("Sent: " + HexFormat.of().formatHex(packet));

        int workingTime = 0;
        boolean reading = true;
        while (reading) {
            Thread.sleep(100); // ждём, чтобы принтер успел ответить
            int available = serialPort.bytesAvailable();
            if (available > 0) {
                byte[] buf = new byte[available];
                serialPort.readBytes(buf, buf.length);
                System.out.println("Recv: " + HexFormat.of().formatHex(buf));

                return NiimbotPacket.builder()
                        .command(buf[2])
                        .size(buf[3])
                        .data(Arrays.copyOfRange(buf, 4, 4 + buf[3]))
                        .build();
            } else {
                System.out.println("Ждет ответа");
            }
            workingTime += 100;
            if (workingTime >= 1200) {
                reading = false;

                System.out.println("Timeout");
            }
        }

        throw new RuntimeException("Timeout");
    }

    private void sendPacket(byte cmd, byte[] payload) {
        byte[] packet = buildPacket(cmd, payload);
        serialPort.writeBytes(packet, packet.length);
        System.out.println("Sent: " + HexFormat.of().formatHex(packet));
    }

    // Проверка, не белый ли пиксель
    private static boolean isPixelNonWhite(BufferedImage img, int x, int y) {
        int rgb = img.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // если яркость < 250 (можно настроить), считаем черным
        return (r + g + b) / 3 < 250;
    }

    @SneakyThrows
    public void encodeImageAndSend(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        if (width % 8 != 0) {
            throw new IllegalArgumentException("Width must be multiple of 8");
        }

        var rowsData = new ArrayList<ImageRow>();

        int bytesPerRow = width / 8;
        for (int row = 0; row < height; row++) {
            var isVoid = true;
            var blackPixelCount = 0;
            byte[] rowColumnData = new byte[bytesPerRow];
            for (int colOct = 0; colOct < bytesPerRow; colOct++) {
                byte pixelsOctet = 0;
                for (int colBit = 0; colBit < 8; colBit++) {
                    int x = colOct * 8 + colBit;
                    if (isPixelNonWhite(img, x, row)) {
                        isVoid = false;
                        pixelsOctet |= (byte) (1 << (7 - colBit));
                        blackPixelCount++;
                    }
                }
                rowColumnData[colOct] = pixelsOctet;
            }
            var imageRow = ImageRow.builder()
                    .type(isVoid? ImageRow.Type.VOID: ImageRow.Type.PIXEL)
                    .rowNumber(row)
                    .repeatCount(1)
                    .columnData(rowColumnData)
                    .blackPixelCount(blackPixelCount)
                    .build();

            if (rowsData.isEmpty()) {
                rowsData.add(imageRow);
                continue;
            }

            var lastImageRow = rowsData.getLast();
            var same = lastImageRow.getType() == imageRow.getType();

            if (same && imageRow.getType() == ImageRow.Type.PIXEL) {
                same = Arrays.equals(lastImageRow.getColumnData(), imageRow.getColumnData());
            }

            if (same) {
                lastImageRow.setRepeatCount(lastImageRow.getRepeatCount() + 1);
            } else {
                rowsData.add(imageRow);
            }
        }

        rowsData.forEach(row -> {
            byte[] bytes = AppUtil.u16ToBytes(row.getRowNumber());

            if (row.getType() == ImageRow.Type.VOID) {
                sendPacket((byte) 0x84, new byte[] {bytes[0], bytes[1], (byte) row.getRepeatCount()});
                return;
            }

            if (row.getBlackPixelCount() <= 6) {
                printBitmapRowIndex(row, bytes);
                return;
            }

            printBitmapRow(row, bytes);

        });

    }

    private void printBitmapRowIndex(ImageRow row, byte[] bytes) {
        var integerPair = countPixelsForBitmapPacket(row.getColumnData(), 384);

        if (integerPair.getLeft() > 6) {
            throw new RuntimeException("Black pixel count > 6 (" + integerPair.getLeft() + ")");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(bytes[0]);
        out.write(bytes[1]);
        for (var b : integerPair.getRight()) {
            out.write(b);
        }
        out.write(row.getRepeatCount());
        try {
            out.write(indexPixels(row.getColumnData()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendPacket((byte) 0x83, out.toByteArray());
    }

    private void printBitmapRow(ImageRow row, byte[] bytes) {
        var integerPair = countPixelsForBitmapPacket(row.getColumnData(), 384);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(bytes[0]);
        out.write(bytes[1]);
        for (var b : integerPair.getRight()) {
            out.write(b);
        }
        out.write(row.getRepeatCount());
        try {
            out.write(row.getColumnData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendPacket((byte) 0x85, out.toByteArray());
    }

    public void print(BufferedImage image) throws InterruptedException {

        // 2. Устанавливаем плотность (8)
        sendPacketWaitResponse((byte) 0x21, new byte[]{(byte) 0x03});

        // 3. Тип ленты = стандарт
        sendPacketWaitResponse((byte) 0x23, new byte[]{(byte) 0x01});

        // 4. Start печати
        sendPacketWaitResponse((byte)0x01, new byte[]{(byte)0x00,(byte) 0x01, (byte)0x00,(byte) 0x00,(byte) 0x00, (byte)0x00, (byte)0x00}); // PrintStart

        sendPacketWaitResponse((byte)0x03, new byte[]{(byte)0x01}); // PageStart

        // 5. Размер страницы 50×30 мм, 203 dpi
        sendPacketWaitResponse((byte)0x13, new byte[]{(byte)0x00, (byte)0xf0, (byte)0x01, (byte)0x90, (byte)0x00, (byte)0x01});

        encodeImageAndSend(image);

        // 6. Закрываем страницу
        sendPacketWaitResponse((byte)0xe3, new byte[]{0x01}); // PageEnd

        var statusWait = true;
        while (statusWait) {
            var response = sendPacketWaitResponse((byte)0xa3, new byte[]{0x01}); // PageEnd
            if (response.getCommand() == (byte) 0xb3) {
                if(response.getData()[6] != (byte) 0x00)
                    throw new RuntimeException("Print error packet flag");
            }

            var pagePrintProgress = response.getData()[2];
            var pageFeedProgress  = response.getData()[3];

            if(pageFeedProgress == 100 && pagePrintProgress == 100) {
                statusWait = false;
            }
            Thread.sleep(300);
        }

        // 7. Завершаем печать
        sendPacket((byte)0xf3, new byte[]{0x01}); // PrintEnd
    }

    @Override
    public void close() throws Exception {
        serialPort.closePort();
    }
}
