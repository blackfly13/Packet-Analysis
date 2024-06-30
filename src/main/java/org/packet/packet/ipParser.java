package org.packet.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ipParser {

    public static byte IP_VERSION;
    public static byte IP_HEADER_LENGTH;
    public static short IP_TOTAL_LENGTH;
    public static short IP_ID;
    public static short IP_FLAGS_FRAGMENT_OFFSET;
    public static byte IP_TTL;
    public static byte IP_PROTOCOL;
    public static short IP_CHECKSUM;
    public static byte[] IP_SRC_ADDR = new byte[4];
    public static byte[] IP_DST_ADDR = new byte[4];

    public static void readIpHeader(byte[] packet) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(packet).order(ByteOrder.BIG_ENDIAN);

        // Read the IP version and header length
        IP_VERSION = (byte) ((byteBuffer.get() & 0xF0) >> 4);
        IP_HEADER_LENGTH = (byte) (byteBuffer.get() & 0x0F);
        IP_HEADER_LENGTH *= 4; // Convert header length to 32-bit words
        // Read the total length
        IP_TOTAL_LENGTH = byteBuffer.getShort();
        // Read the identification
        IP_ID = byteBuffer.getShort();
        // Read the flags and fragment offset
        IP_FLAGS_FRAGMENT_OFFSET = byteBuffer.getShort();
        byte flags = (byte) (IP_FLAGS_FRAGMENT_OFFSET & 0xE0);
        byte fragmentOffset = (byte) (IP_FLAGS_FRAGMENT_OFFSET & 0x1F);
        if (fragmentOffset > 0) {
            // Set the "more fragments" flag if the fragment offset is non-zero
            IP_FLAGS_FRAGMENT_OFFSET |= 0x20;
        }

        // Read the time to live
        IP_TTL = byteBuffer.get();
        // Read the protocol
        IP_PROTOCOL = byteBuffer.get();
        // Read the checksum
        IP_CHECKSUM = byteBuffer.getShort();
        // Read the source and destination addresses
        byteBuffer.get(IP_SRC_ADDR);
        byteBuffer.get(IP_DST_ADDR);
    }

    public static byte getIpVersion(){
        return IP_VERSION;
    }

    public static byte getIpHeaderLength(){
        return IP_HEADER_LENGTH;
    }

    public static byte getIpProtocol(){
        return IP_PROTOCOL;
    }

    public static byte[] getSrcAddress(){
        return IP_SRC_ADDR;
    }

    public static byte[] getDesAddress(){
        return IP_DST_ADDR;
    }


}
