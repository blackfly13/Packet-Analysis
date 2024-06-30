package org.packet.packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class packetHeader {
    public static long TIMESTAMP_SECONDS;
    public static long TIMESTAMP_MICROSECONDS;
    public static int OCTETS_SAVED;
    public static int ACTUAL_LENGTH;

    public static void readPacketHeader(DataInputStream dis) throws IOException {
        byte[] buffer = new byte[16];
        int bytesRead = dis.read(buffer);
        String bufferString = Arrays.toString(buffer);

        if (bytesRead != 16) {
            throw new IOException("Unexpected End of Stream");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

        TIMESTAMP_SECONDS = byteBuffer.getInt();
        TIMESTAMP_MICROSECONDS = byteBuffer.getInt();
        OCTETS_SAVED = byteBuffer.getInt();
        ACTUAL_LENGTH = byteBuffer.getInt();

    }

    public static long getTimeStampSec(){
        return TIMESTAMP_SECONDS;
    }

    public static long getTimestampMicro(){
        return TIMESTAMP_MICROSECONDS;
    }

    public static int getOctetsSaved(){
        return OCTETS_SAVED;
    }

    public static int getActualLength(){
        return ACTUAL_LENGTH;
    }
}
