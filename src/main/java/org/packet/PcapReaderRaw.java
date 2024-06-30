package org.packet;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class PcapReaderRaw {

    public List<byte[]> readPcap(String filePath) throws PcapNativeException, NotOpenException {
        List<byte[]> packets = new ArrayList<>();

        PcapHandle handle = Pcaps.openOffline(filePath);
        Packet packet;
        while ((packet = handle.getNextPacket()) != null) {
            byte[] packetBytes = packet.getRawData();
            packets.add(packetBytes);
        }
        handle.close();

        return packets;
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        PcapReaderRaw reader = new PcapReaderRaw();
        List<byte[]> packets = reader.readPcap("C:\\Users\\Thejas\\Downloads\\ldap_anon_bind.pcap");
        for (byte[] packet : packets) {
            // Process the packet
            System.out.println("Packet length: " + packet.length);
        }
    }
}