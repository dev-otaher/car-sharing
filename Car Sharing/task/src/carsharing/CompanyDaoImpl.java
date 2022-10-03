package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDaoImpl implements CompanyDao {
    private Connection con;

    public CompanyDaoImpl(String dbName) {
        try {
            if (con == null) {
                Class.forName("org.h2.Driver");
                createConnection(dbName);
                createTables();
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Company> getCompanies() {
        try {
            String query = "SELECT ID, NAME FROM COMPANY";
            ResultSet resultSet = con.createStatement().executeQuery(query);
            List<Company> companies = new ArrayList<>();
            while (resultSet.next()) {
                companies.add(new Company(resultSet.getInt("id"), resultSet.getString("name")));
            }
            return companies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertCompany(Company company) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO COMPANY (name) VALUES (?)");
            preparedStatement.setString(1, company.getName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createConnection(String dbName) throws SQLException {
        con = DriverManager.getConnection("jdbc:h2:./src/carsharing/db/" + dbName);
        con.setAutoCommit(true);
    }

    private void createTables() throws SQLException {
        Statement st = con.createStatement();
        String query = "CREATE TABLE IF NOT EXISTS COMPANY (" +
                "ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(60) UNIQUE NOT NULL);";
        st.executeUpdate(query);
        st.close();
    }
}
