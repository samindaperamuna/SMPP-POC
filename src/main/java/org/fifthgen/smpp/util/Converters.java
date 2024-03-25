package org.fifthgen.smpp.util;

public class Converters {

    public static String encodeHexString(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();

        for (byte aByte : bytes) {
            hexStringBuilder.append(byteToHex(aByte));
        }

        return hexStringBuilder.toString();
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);

        return new String(hexDigits);
    }
}
