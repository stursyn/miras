package kz.satpaev.sunkar.util;

import org.apache.commons.lang3.tuple.Pair;

public class ByteUtil {
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
}
