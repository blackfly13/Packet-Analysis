package org.packet.packet;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class pcapReader {

    // File Header Properties
    public static int MAGIC_NUMBER;
    public static short MAJOR_VERSION;
    public static short MINOR_VERSION;
    public static int OFFSET;
    public static int ACCURACY;
    public static int MAX_LENGTH;
    public static int DATA_LINK_TYPE;

    // Packet Header Properties
    public static long TIMESTAMP_SECONDS;
    public static long TIMESTAMP_MICROSECONDS;
    public static int OCTETS_SAVED;
    public static int ACTUAL_LENGTH;

    // Ethernet Properties
    public static byte[] DEST_ADDR = new byte[6];
    public static byte[] SRC_ADDR = new byte[6];
    public static short ETHERNET_TYPE;

    // IP Properties
    public static byte IP_VERSION;
    public static byte IP_HEADER_LENGTH;
    public static byte IP_PROTOCOL;
    public static byte[] IP_SRC_ADDR = new byte[4];
    public static byte[] IP_DST_ADDR = new byte[4];

    //TCP Properties
    public static int TCP_SRC_PORT;
    public static int TCP_DST_PORT;
    public static long TCP_SEQUENCE_NUMBER;


    //method to read packet raw
    public static byte[] readPacket(DataInputStream dis, int length) throws IOException {
        byte[] buffer = new byte[2048];
        dis.read(buffer,0,length);
        return buffer;

    }

    private void setFileProperties()
    {
        MAGIC_NUMBER = pcapFileHeader.getMagicNumber();
        MAJOR_VERSION = pcapFileHeader.getMajorVersion();
        MINOR_VERSION = pcapFileHeader.getMinorVersion();
        OFFSET = pcapFileHeader.getOFFSET();
        ACCURACY = pcapFileHeader.getACCURACY();
        MAX_LENGTH = pcapFileHeader.getMaxLength();
        DATA_LINK_TYPE = pcapFileHeader.getDataLinkType();
    }

    private void setPacketProperties(){

        TIMESTAMP_SECONDS = packetHeader.getTimeStampSec();
        TIMESTAMP_MICROSECONDS = packetHeader.getTimestampMicro();
        OCTETS_SAVED = packetHeader.getOctetsSaved();
        ACTUAL_LENGTH = packetHeader.getActualLength();

    }

    private void setEthernetProperties(){

        DEST_ADDR = ethernetParser.getDestAddr();
        SRC_ADDR = ethernetParser.getSrcAddr();
        ETHERNET_TYPE = ethernetParser.getType();

    }

    private void setIpProperties(){

        IP_VERSION = ipParser.getIpVersion();
        IP_HEADER_LENGTH = ipParser.getIpHeaderLength();
        IP_PROTOCOL = ipParser.getIpProtocol();
        IP_SRC_ADDR = ipParser.getSrcAddress();
        IP_DST_ADDR = ipParser.getDesAddress();

    }

    private void setTcpProperties(){

        TCP_SRC_PORT=tcpParser.getTcpSrcPort();
        TCP_DST_PORT=tcpParser.getTcpDstPort();
        TCP_SEQUENCE_NUMBER=tcpParser.getTcpSequenceNumber();

    }

    public static void main(String[] args) {

        String filePath = "C:\\Users\\Thejas\\Downloads\\dump.pcap";

        try {
            pcapFileHeader.readPcapFileHeader(filePath);

            DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
            dis.skipBytes(24); // Skip the file header
            packetHeader.readPacketHeader(dis);

            pcapReader p = new pcapReader();
            p.setFileProperties();
            p.setPacketProperties();

            byte[] result = readPacket(dis, OCTETS_SAVED);
            ethernetParser.readEthernetHeader(result);
            p.setEthernetProperties();


            if(ETHERNET_TYPE == 8)
            {
                ipParser.readIpHeader(result);
                p.setIpProperties();

                System.out.print(IP_PROTOCOL);

                if(IP_PROTOCOL==6)
                {
                    tcpParser.readTcpHeader(result);
                    p.setTcpProperties();
                }

                LdapParser.getLdapPacket(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
        }
    }
}