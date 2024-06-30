package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class ClickHouseUsageEstimator {
    final static String url = "jdbc:clickhouse://localhost:8000/<database>";

    public static long getEstimatedUsage(String tableName, String databaseName) {

        String query = "SELECT sum(bytes_on_disk) AS bytes_on_disk " +
                "FROM system.parts " +
                "WHERE database = ? AND " +
                "table = ? AND active AND " +
                "min_date = yesterday() AND max_date = yesterday() ";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, databaseName);
            pstmt.setString(2, tableName);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getLong("bytes_on_disk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long sendTotalUsage(String tableName, String databaseName, int multiplier)//multiplier must be given from user input
    {
        long totalUsage = 0;

        long oneDaySize = getEstimatedUsage(tableName, databaseName);  //table name changes based on what is requested get it from api
        long fiveMinSize = getEstimatedUsage(tableName, databaseName);
        long oneMinSize = getEstimatedUsage(tableName, databaseName);
        //modify based on api code
        totalUsage = (oneDaySize * multiplier) + (fiveMinSize * multiplier) + (oneMinSize * multiplier);

        return totalUsage;
    }
}
