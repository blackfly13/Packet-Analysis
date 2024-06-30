package org.packet.packet;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class pcapFileHeader {

    public static int MAGIC_NUMBER;
    public static short MAJOR_VERSION;
    public static short MINOR_VERSION;
    public static int OFFSET;
    public static int ACCURACY;
    public static int MAX_LENGTH;
    public static int DATA_LINK_TYPE;


    public static void readPcapFileHeader(String filePath) throws IOException {

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            byte[] buffer = new byte[24];
            int bytesRead = dis.read(buffer);
            if (bytesRead != 24) {
                throw new IOException("Unexpected End of Stream");
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
            MAGIC_NUMBER = byteBuffer.getInt();
            MAJOR_VERSION = byteBuffer.getShort();
            MINOR_VERSION = byteBuffer.getShort();
            OFFSET = byteBuffer.getInt();
            ACCURACY = byteBuffer.getInt();
            MAX_LENGTH = byteBuffer.getInt();
            DATA_LINK_TYPE = byteBuffer.getInt();
        }
    }

    public static int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    public static short getMajorVersion() {
        return MAJOR_VERSION;
    }

    public static short getMinorVersion() {
        return MINOR_VERSION;
    }

    public static int getOFFSET() {
        return OFFSET;
    }

    public static int getACCURACY() {
        return ACCURACY;
    }

    public static int getMaxLength() {
        return MAX_LENGTH;
    }

    public static int getDataLinkType() {
        return DATA_LINK_TYPE;
    }

}
