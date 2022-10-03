package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private void createConnection(String dbName) throws SQLException {
        con = DriverManager.getConnection("jdbc:h2:./src/carsharing/db/" + dbName);
        con.setAutoCommit(true);
    }

    private void createTables() throws SQLException {
        Statement st = con.createStatement();
        String createCompanyQuery =
                "CREATE TABLE IF NOT EXISTS company (" +
                        "id INT IDENTITY PRIMARY KEY, " +
                        "name VARCHAR UNIQUE NOT NULL);";
        String createCarQuery =
                "CREATE TABLE IF NOT EXISTS CAR (" +
                        "id INT IDENTITY PRIMARY KEY , " +
                        "name VARCHAR UNIQUE NOT NULL, " +
                        "company_id INT NOT NULL, " +
                        "CONSTRAINT fk_company_id FOREIGN KEY (company_id) " +
                        "REFERENCES company(id));";
        st.executeUpdate(createCompanyQuery + createCarQuery);
        st.close();
    }

    @Override
    public void createCompany(Company company) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO company (name) VALUES (?)");
            preparedStatement.setString(1, company.getName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Company> getCompanies() {
        try {
            String query = "SELECT id, name FROM company";
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
    public Optional<Company> getCompanyById(int id) {
        String query =
                "SELECT id, name FROM company " +
                "WHERE id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Company(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getCarsByCompany(Company company) {
        try {
            String query =
                    "SELECT id, name FROM car " +
                    "WHERE company_id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, company.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(new Car(resultSet.getInt("id"), resultSet.getString("name"), company));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createCar(Car car) {
        try {
            String query =
                    "INSERT INTO car (name, company_id) " +
                    "VALUES (?, ?);";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, car.getName());
            preparedStatement.setInt(2, car.getCompany().getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
