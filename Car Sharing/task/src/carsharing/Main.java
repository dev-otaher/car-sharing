package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // write your code here
        System.out.println(Arrays.toString(args));
        String dbName = "anything";
        if (args.length > 1 && args[0].equals("-databaseFileName")) {
            dbName = args[1];
        }
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:./src/carsharing/db/" + dbName);
            con.setAutoCommit(true);
            Statement st = con.createStatement();
            String query =
                    "CREATE TABLE IF NOT EXISTS COMPANY(" +
                            "    ID INTEGER," +
                            "    NAME VARCHAR" +
                            ")";
            st.executeUpdate(query);
            st.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}