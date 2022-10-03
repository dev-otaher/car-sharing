package carsharing;

import javax.swing.text.html.Option;
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

    @Override
    public void closeConnection() {
        try {
            con.close();
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
        String createCompanyTable =
                "CREATE TABLE IF NOT EXISTS company (" +
                        "   id INT IDENTITY PRIMARY KEY, " +
                        "   name VARCHAR UNIQUE NOT NULL" +
                        ");";
        String createCarTable =
                "CREATE TABLE IF NOT EXISTS car (" +
                        "   id INT IDENTITY PRIMARY KEY, " +
                        "   name VARCHAR UNIQUE NOT NULL, " +
                        "   company_id INT NOT NULL, " +
                        "   CONSTRAINT fk_company_id FOREIGN KEY (company_id) " +
                        "   REFERENCES company(id)" +
                        ");";
        String createCustomerTable =
                "CREATE TABLE IF NOT EXISTS customer (" +
                        "   id INT IDENTITY PRIMARY KEY, " +
                        "   name VARCHAR UNIQUE NOT NULL, " +
                        "   rented_car_id INT, " +
                        "   CONSTRAINT fk_rented_car_id FOREIGN KEY (rented_car_id) " +
                        "   REFERENCES car(id)" +
                        ");";
        st.executeUpdate(createCompanyTable + createCarTable + createCustomerTable);
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
        String query =
                "SELECT id, name " +
                        "FROM company";
        List<Company> companies = new ArrayList<>();
        try {
            ResultSet resultSet = con.createStatement().executeQuery(query);
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
                "SELECT id, name " +
                        "FROM company " +
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
    public Optional<Car> getCarById(int id) {
        String query =
                "SELECT id, name, company_id " +
                        "FROM car " +
                        "WHERE id = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Company company = getCompanyById(resultSet.getInt("company_id")).orElseThrow();
                return Optional.of(new Car(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        company
                ));
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

    @Override
    public List<Customer> getCustomers() {
        String query =
                "SELECT id, name, rented_car_id " +
                        "FROM customer;";
        List<Customer> customers = new ArrayList<>();
        try {
            ResultSet resultSet = con.createStatement().executeQuery(query);
            while (resultSet.next()) {
                Car rentedCar = getCarById(resultSet.getInt("rented_car_id")).orElse(null);
                customers.add(
                        new Customer(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                rentedCar)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    @Override
    public void createCustomer(Customer customer) {
        String query =
                "INSERT INTO customer (name) " +
                        "VALUES (?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, customer.getName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void rentCar(Customer customer, Car car) {
        String query =
                "UPDATE customer " +
                        "SET rented_car_id = ? " +
                        "WHERE id = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, car.getId());
            preparedStatement.setInt(2, customer.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnCar(Customer customer) {
        String query =
                "UPDATE customer " +
                        "SET rented_car_id = null " +
                        "WHERE id = ?;";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, customer.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Car> getAvailableCarsByCompany(Company company) {
        String query =
                "SELECT id, name, company_id " +
                        "FROM car " +
                        "WHERE company_id = ? " +
                        "AND id NOT IN (" +
                        "   SELECT rented_car_id FROM customer WHERE rented_car_id IS NOT NULL" +
                        ");";
        List<Car> cars = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, company.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(
                        new Car(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                company)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cars;
    }
}
