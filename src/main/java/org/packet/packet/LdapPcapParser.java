package org.packet.packet;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.namednumber.IpNumber;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

public class LdapPcapParser {
    public static void main(String[] args) {
        PcapHandle handle = null;
        try {
            // Open the PCAP file
            handle = Pcaps.openOffline("C:\\Users\\Thejas\\Downloads\\dump.pcap");

            // Loop through each packet in the PCAP file
            while (true) {
                Packet packet;
                try {
                    packet = handle.getNextPacketEx();
                } catch (TimeoutException e) {
                    continue; // TimeoutException should not break the loop
                } catch (EOFException e) {
                    break; // End of file reached
                }

                if (packet == null) {
                    break;
                }

                // Check if the packet is an LDAP packet over TCP
                if (packet.contains(IpV4Packet.class)) {
                    IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                    if (ipV4Packet.getHeader().getProtocol() == IpNumber.TCP) {
                        TcpPacket tcpPacket = ipV4Packet.get(TcpPacket.class);
                        if (tcpPacket.getHeader().getDstPort().value() == 389) {
                            // Ensure the payload is not null before attempting to get the raw data
                            if (tcpPacket.getPayload() != null) {
                                byte[] ldapPayload = tcpPacket.getPayload().getRawData();
                                System.out.println("LDAP packet found:");
                                System.out.println("  Source IP: " + ipV4Packet.getHeader().getSrcAddr());
                                System.out.println("  Destination IP: " + ipV4Packet.getHeader().getDstAddr());
                                System.out.println("  LDAP request: " + new String(ldapPayload));
                            }
                        }
                    }
                }
            }
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }
}
