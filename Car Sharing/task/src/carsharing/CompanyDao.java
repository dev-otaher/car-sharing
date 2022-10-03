package carsharing;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CompanyDao {
    public void closeConnection();

    public List<Company> getCompanies();

    public void createCompany(Company company);

    public Optional<Company> getCompanyById(int id);

    public Optional<Car> getCarById(int id);

    public List<Car> getCarsByCompany(Company company);

    public void createCar(Car car);

    public List<Customer> getCustomers();

    public void createCustomer(Customer customer);

    public void rentCar(Customer customer, Car car);

    public void returnCar(Customer customer);

    List<Car> getAvailableCarsByCompany(Company chosenCompany);
}
