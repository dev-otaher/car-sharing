package carsharing;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {
    public List<Company> getCompanies();

    public void createCompany(Company company);

    public Optional<Company> getCompanyById(int id);

    public List<Car> getCarsByCompany(Company company);

    public void createCar(Car car);
}
