package org.packet.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ethernetParser {

    public static byte[] DEST_ADDR = new byte[6];
    public static byte[] SRC_ADDR = new byte[6];
    public static short TYPE;

    public static void readEthernetHeader(byte[] packet) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);

        // Read the destination address
        byteBuffer.get(DEST_ADDR);
        // Read the source address
        byteBuffer.get(SRC_ADDR);
        // Read the type
        TYPE = byteBuffer.getShort();
    }

    public static byte[] getDestAddr(){
        return DEST_ADDR;
    }

    public static byte[] getSrcAddr(){
        return SRC_ADDR;
    }

    public static short getType(){
        return TYPE;
    }
}
