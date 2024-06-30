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
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class LdapParser {

    public static Inet4Address SOURCE_ADDR, DEST_ADDR;
    public static int SOURCE_PORT, DEST_PORT;

    public static String request;

    public static void setSourceAddr(Inet4Address sourceAddr) {
        SOURCE_ADDR = sourceAddr;
    }

    public static void setDestAddr(Inet4Address destAddr) {
        DEST_ADDR = destAddr;
    }

    public static void setSourcePort(int sourcePort) {
        SOURCE_PORT = sourcePort;
    }

    public static void setDestPort(int destPort) {
        DEST_PORT = destPort;
    }

    public static String getRequest() {
        return request;
    }

    public static void setRequest(String request) {
        LdapParser.request = request;
    }

    public static Inet4Address getSourceAddr() {
        return SOURCE_ADDR;
    }

    public static Inet4Address getDestAddr() {
        return DEST_ADDR;
    }

    public static int getSourcePort() {
        return SOURCE_PORT;
    }

    public static int getDestPort() {
        return DEST_PORT;
    }

    public static void getLdapPacket(String path)
    {
        try (PcapHandle handle = Pcaps.openOffline(path)) {
            // Open the PCAP file

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

                                setDestAddr(ipV4Packet.getHeader().getDstAddr());
                                setSourceAddr(ipV4Packet.getHeader().getSrcAddr());
                                setDestPort(tcpPacket.getHeader().getDstPort().value() & 0xFFFF);   // to convert short to unsigned int
                                setSourcePort(tcpPacket.getHeader().getSrcPort().value() & 0xFFFF);
                                String ldapPayloadString = new String(ldapPayload, StandardCharsets.UTF_8);
                                String humanReadableString = ldapPayloadString.replaceAll("[^\\x20-\\x7E]", "");
                                setRequest(humanReadableString);
                                System.out.println("LDAP packet found:");
                                System.out.println("  Source IP: " + getSourceAddr());
                                System.out.println("  Destination IP: " + getDestAddr());
                                System.out.println("  Source Port: " + getSourcePort());
                                System.out.println("  Destination Port: " + getDestPort());
                                System.out.println("  LDAP request: " + getRequest());
                            }
                        }
                    }
                }
            }
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }
    }
}
