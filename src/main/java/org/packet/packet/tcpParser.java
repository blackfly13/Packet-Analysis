package org.packet.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class tcpParser {

    public static int TCP_SRC_PORT;
    public static int TCP_DST_PORT;
    public static long TCP_SEQUENCE_NUMBER;
    public static long TCP_ACKNOWLEDGEMENT_NUMBER;
    public static byte TCP_HEADER_LENGTH_RESERVED;
    public static byte TCP_FLAGS;
    public static short TCP_WINDOW_SIZE;
    public static short TCP_CHECKSUM;
    public static short TCP_URGENT_POINTER;

    public static void readTcpHeader(byte[] packet) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(packet).order(ByteOrder.BIG_ENDIAN);

        // Read the source and destination ports
        TCP_SRC_PORT = byteBuffer.getShort();
        TCP_DST_PORT = byteBuffer.getShort();

        // Read the sequence number
        TCP_SEQUENCE_NUMBER = byteBuffer.getInt();

        // Read the acknowledgement number
        TCP_ACKNOWLEDGEMENT_NUMBER = byteBuffer.getInt();

        // Read the header length, reserved, and flags
        TCP_HEADER_LENGTH_RESERVED = byteBuffer.get();
        TCP_HEADER_LENGTH_RESERVED = (byte) ((TCP_HEADER_LENGTH_RESERVED & 0xF0) >> 4); // Extract header length
        TCP_HEADER_LENGTH_RESERVED = (byte) (TCP_HEADER_LENGTH_RESERVED << 4); // Shift back to original position
        TCP_FLAGS = (byte) (TCP_HEADER_LENGTH_RESERVED & 0x0F); // Extract flags

        // Read the window size
        TCP_WINDOW_SIZE = byteBuffer.getShort();

        // Read the checksum
        TCP_CHECKSUM = byteBuffer.getShort();

        // Read the urgent pointer
        TCP_URGENT_POINTER = byteBuffer.getShort();
    }

    public static int getTcpSrcPort(){
        return TCP_SRC_PORT;
    }

    public static int getTcpDstPort(){
        return TCP_DST_PORT;
    }

    public static long getTcpSequenceNumber(){
        return TCP_SEQUENCE_NUMBER;
    }
}
