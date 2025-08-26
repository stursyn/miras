package kz.satpaev.sunkar.service;

import com.fazecast.jSerialComm.SerialPort;
import kz.satpaev.sunkar.util.ByteUtil;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static kz.satpaev.sunkar.util.ByteUtil.countPixelsForBitmapPacket;

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

    private void sendPacket(byte cmd, byte[] payload) {
        byte[] packet = buildPacket(cmd, payload);
        serialPort.writeBytes(packet, packet.length);
        System.out.println("Sent: " + bytesToHex(packet));
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

        int bytesPerRow = width / 8;
        int emptyRowsCount = 0;
        int emptyRowStart = 0;
        for (int row = 0; row < height; row++) {
            var isVoid = true;
            byte[] rowsData = new byte[bytesPerRow];
            for (int colOct = 0; colOct < bytesPerRow; colOct++) {
                byte pixelsOctet = 0;
                for (int colBit = 0; colBit < 8; colBit++) {
                    int x = colOct * 8 + colBit;
                    if (isPixelNonWhite(img, x, row)) {
                        isVoid = false;
                        pixelsOctet |= (byte) (1 << (7 - colBit));
                    }
                }
                rowsData[colOct] = pixelsOctet;
            }
            if (isVoid) {
                if(emptyRowStart == 0) emptyRowStart = row;
                emptyRowsCount++;
            } else {
                if (emptyRowsCount >0) {
                    byte[] bytes = ByteUtil.u16ToBytes(emptyRowStart);
                    sendPacket((byte) 0x84, new byte[]{bytes[0], bytes[1], (byte) emptyRowsCount});
                    emptyRowsCount = 0;
                }

                byte[] bytes = ByteUtil.u16ToBytes(row);
                var integerPair = countPixelsForBitmapPacket(rowsData, 384);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(bytes[0]);
                out.write(bytes[1]);
                for (var b : integerPair.getRight()) {
                    out.write(b);
                }
                out.write( 0x00);
                out.write( rowsData);
                sendPacket((byte) 0x85, out.toByteArray());
            }
        }

        if (emptyRowsCount >0) {
            byte[] bytes = ByteUtil.u16ToBytes(emptyRowStart);
            sendPacket((byte) 0x84, new byte[]{bytes[0], bytes[1], (byte) emptyRowsCount});
        }
    }

    public void testPrinter(BufferedImage image) throws InterruptedException {

        // 2. Устанавливаем плотность (8)
        sendPacket((byte)0x21, new byte[]{(byte)0x01});

        // 3. Тип ленты = стандарт
        sendPacket((byte)0x23, new byte[]{(byte)0x01});

        // 4. Start печати
        sendPacket((byte)0x01, new byte[]{(byte)0x00,(byte) 0x01, (byte)0x00,(byte) 0x00,(byte) 0x00, (byte)0x00, (byte)0x00}); // PrintStart

        sendPacket((byte)0x03, new byte[]{(byte)0x01}); // PageStart

        // 5. Размер страницы 50×30 мм, 203 dpi
        sendPacket((byte)0x13, new byte[]{(byte)0x00, (byte)0xf0, (byte)0x01, (byte)0x90, (byte)0x00, (byte)0x01});

        encodeImageAndSend(image);
        // 6. Закрываем страницу
        sendPacket((byte)0xe3, new byte[]{0x01}); // PageEnd

        boolean reading = true;
        while (reading) {
            Thread.sleep(500); // ждём, чтобы принтер успел ответить
            int available = serialPort.bytesAvailable();
            if (available > 0) {
                reading = false;
                byte[] buf = new byte[available];
                serialPort.readBytes(buf, buf.length);
                System.out.println("Recv: " + bytesToHex(buf));
            } else {
                System.out.println("Ждет ответа");
            }
        }
        // 7. Завершаем печать
        sendPacket((byte)0xf3, new byte[]{0x01}); // PrintEnd
    }

    private String bytesToHex(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    @Override
    public void close() throws Exception {
        serialPort.closePort();
    }
}
