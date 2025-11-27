package kz.satpaev.sunkar.util;

import kz.satpaev.sunkar.model.entity.PaymentType;
import kz.satpaev.sunkar.model.projection.SaleSummaryProjection;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HexFormat;

public class AppUtil {
    public static int calculateChecksum(String digits12) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(digits12.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return (10 - (sum % 10)) % 10;
    }
    public static byte[] u16ToBytes(int value) {
        if (value < 0 || value > 0xFFFF) {
            throw new IllegalArgumentException("Value out of range for u16: " + value);
        }
        byte high = (byte) ((value >> 8) & 0xFF); // старший байт
        byte low  = (byte) (value & 0xFF);        // младший байт
        return new byte[] { high, low };
    }

    public static byte[] indexPixels(byte[] data) {
        var result = new ArrayList<Byte>();

        for (int bytePos = 0; bytePos < data.length; bytePos++) {
            int b = data[bytePos] & 0xFF; // unsigned byte

            for (int bitPos = 0; bitPos < 8; bitPos++) {
                // проверка бита от старшего к младшему
                if ((b & (1 << (7 - bitPos))) != 0) {
                    int pixelIndex = bytePos * 8 + bitPos;
                    byte[] idxBytes = u16ToBytes(pixelIndex);
                    result.add(idxBytes[0]);
                    result.add(idxBytes[1]);
                }
            }
        }

        // преобразуем List<Byte> в byte[]
        byte[] out = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            out[i] = result.get(i);
        }
        return out;
    }

    public static Pair<Integer, int[]> countPixelsForBitmapPacket(
            byte[] buf,
            int printheadPixels
    ) {
        int total = 0;
        int[] parts = new int[]{0, 0, 0};
        int chunkSize = printheadPixels / 8 / 3; // Each byte stores 8 pixels
        boolean split = buf.length <= chunkSize * 3;

        for (int byteN = 0; byteN < buf.length; byteN++) {
            byte value = buf[byteN];
            int chunkIdx = byteN / chunkSize;

            for (int bitN = 0; bitN < 8; bitN++) {
                if ((value & (1 << bitN)) != 0) { // черный пиксель
                    total++;

                    if (!split) continue;

                    if (chunkIdx > 2) {
                        System.out.println("Overflow (chunk index " + chunkIdx + ")");
                        continue;
                    }

                    parts[chunkIdx]++;
                    if (parts[chunkIdx] > 255) {
                        System.out.println("Pixel count overflow");
                    }
                }
            }
        }

        if (split) {
            return Pair.of(total, parts);
        }

        byte[] bytes = u16ToBytes(total);
        return Pair.of(total, new int[]{0, bytes[1] & 0xFF, bytes[0] & 0xFF});
    }

    public static int ean13Checksum(String ean12) {
        if (!ean12.matches("\\d{12}")) throw new IllegalArgumentException("EAN-13 needs 12 digits payload");
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = ean12.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : 3 * d; // positions 1..12 (0-based here)
        }
        int mod = sum % 10;
        return mod == 0 ? 0 : 10 - mod;
    }

    public static String generateBarcode(long seq) {
        String elment12 = Constants.MERCHANT_CODE +
            Constants.KZ_GS1_CODE +
            fillZero(seq);
        return elment12 + ean13Checksum(elment12);
    }

    private static String fillZero(long seq) {
        int count = (seq + "").length();
      return "0".repeat(Math.max(0, 7 - count)) + seq;
    }

    private static SaleSummaryProjection defaultSaleSummaryProjection(PaymentType paymentType) {
        return new SaleSummaryProjection() {
            @Override
            public PaymentType getPaymentType() {
                return paymentType;
            }

            @Override
            public BigDecimal getTotalAmount() {
                return BigDecimal.ZERO;
            }

            @Override
            public Integer getTotalCount() {
                return 0;
            }

            @Override
            public BigDecimal getKaspiAmount() {
                return BigDecimal.ZERO;
            }

            @Override
            public BigDecimal getCashAmount() {
                return BigDecimal.ZERO;
            }

            @Override
            public BigDecimal getHalykAmount() {
                return BigDecimal.ZERO;
            }

            @Override
            public BigDecimal getDutyAmount() {
                return BigDecimal.ZERO;
            }
        };
    }
}
