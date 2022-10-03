package carsharing;

import java.sql.SQLException;
import java.util.List;

public interface CompanyDao {
    public List<Company> getCompanies();

    public void insertCompany(Company company);
}
