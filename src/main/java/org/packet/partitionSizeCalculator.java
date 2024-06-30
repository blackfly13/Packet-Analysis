package org.packet;

import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class partitionSizeCalculator {

   private static String  url = "jdbc:clickhouse://localhost:8123/asi_data_db";                              //clickHouse default url
   private static String userName= "default";                       //clickHouse default user
   private static String  pass = "password";                          //clickHouse default password

    //need to change url, user, pass from api code
    
    partitionSizeCalculator(String url, String userName, String pass)
    {
        partitionSizeCalculator.url = url;
        partitionSizeCalculator.userName = userName;
        partitionSizeCalculator.pass = pass;

    }

    public static double calculateUsage(Map<String, Integer> table) throws SQLException { // all information must be supplied by front end, change based on requirement
        double result = 0;

        // iterating all values from the map

        for (Map.Entry<String, Integer> entry : table.entrySet()) {

            String tableName = entry.getKey();      //Name of table to retrieve data from
            double days = entry.getValue();         //NO. of days the data will be stored

            System.out.println(tableName);
            //getting relevant values from database
            double totalSize = getSizeFromPartition(tableName);

            double agingSize = (totalSize * days);          // can be returned in a map if needed

            result += agingSize;                           // final size to be returned after all aging calculations
            System.out.println(totalSize * days);
        }
        //total projected usage based on previous day's data(returned in bytes)
        if (result < 0)
            return 0;       //fallback in case of false value return
        else
            return result;
    }

    // SQL query reference

    /*SELECT formatReadableSize(sum(data_compressed_bytes)) AS size
FROM system.parts
WHERE partition IN (
    SELECT partition
    FROM system.parts
    WHERE table = 'KHIRAW' and partition LIKE '%20240321%'
);   */

    private static double getSizeFromPartition(String tableName) throws SQLException {     //Function to get partition size from partition

        double size = 0;
        int i=1;

        Properties properties = new Properties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);

        do {                                        //looping the statement in case yesterday data was not inserted
                                             //taking the most recent bay before yesterday.
            try {
                String query = "SELECT sum(bytes_on_disk) AS size\n" +
                        "FROM system.parts \n" +
                        "WHERE partition IN (\n" +
                        "    SELECT partition \n" +
                        "    FROM system.parts \n" +
                        "    WHERE table = ? and partition LIKE '%"
                        + LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd")) +    // statement to insert yesterday's date from code formatted for sql
                        "%' and active) ";

                Connection con = dataSource.getConnection(userName, pass);
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, tableName);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        size = rs.getDouble("size");
                    }
                }
                if(size>0)
                    break;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            i++;
        }while(true);
        return size;
    }

    public static void main(String[] args) {  //driver code

        double result = 0;

        Map<String, Integer> Table = new HashMap<>();        //hash map containing table name and no of days the data is going to be stored(will be predefined)
        Table.put("KPIRAW", 10);
        Table.put("KHIRAW", 10);
        Table.put("ATTACKSURFACE", 10);//test table name and no of days

        try
        {
            result = calculateUsage(Table);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        double readable = result/1024/1024;      //Megabyte conversion, need to modify the final code to give out relevant result based on situation

        System.out.printf("%.2f", readable);  //console output can be removed for final code
    }
}


































//redundant code no longer in use (reference only)



/*
    private static double getTotalSize(String tableName) throws SQLException {        // function to get total table size from system.parts

        double size = 0;

        Properties properties = new Properties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);

        try {
            String partition;
            String query = "SELECT partition FROM system.parts WHERE table = ? AND  partition LIKE '%"+ LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))+"%'";
            System.out.println(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            Connection con = dataSource.getConnection(userName, pass);
            PreparedStatement pstmt = con.prepareStatement(query);

            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    partition= rs.getString("partition");
                    size = getSizeFromId(partition);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(size > 0)
            return size;
        else {          //Fallback condition in case data from Yesterday() is not available
            int i=1;
            String query = "SELECT partition FROM system.parts WHERE table = ? AND  partition LIKE '%"+ LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd"))+"%'";
            System.out.println(LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            do{
                String partition;
                i++;
                try {
                    Connection con = dataSource.getConnection(userName, pass);
                    PreparedStatement pstmt = con.prepareStatement(query);

                    pstmt.setString(1, tableName);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            partition = rs.getString("partition");
                            size += getSizeFromId(partition);
                        }
                        if(size>0)
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }while (true);
            return size;
        }
    }
*/

/*    private static double getSizeFromId(String partition) throws SQLException {     //Function to get partition size from partition ID

        double size = 0;

        String query = "SELECT sum(data_compressed_bytes) AS size FROM system.parts WHERE partition = ?";
        Properties properties = new Properties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
        Connection con = dataSource.getConnection(userName, pass);
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, partition);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                size += rs.getDouble("size");
            }
        }
        return size;
    }*/
