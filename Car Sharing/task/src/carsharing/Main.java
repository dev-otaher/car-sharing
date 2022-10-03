package carsharing;

import java.util.List;
import java.util.Scanner;

public class Main {
    static CompanyDao companyDao;

    static Scanner scanner;

    public static void main(String[] args) {
        // write your code here
        String dbName = "anything";
        if (args.length > 1 && args[0].equals("-databaseFileName")) {
            dbName = args[1];
        }
        companyDao = new CompanyDaoImpl(dbName);
        scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println(
                    "1. Log in as a manager" +
                            "\n2. Log in as a customer" +
                            "\n3. Create a customer" +
                            "\n0. Exit"
            );
            choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (choice) {
                case 1:
                    logInAsManager();
                    break;
                case 2:
                    logInAsCustomer();
                    break;
                case 3:
                    createCustomer();
                    break;
                case 0:
                default:
                    companyDao.closeConnection();
                    break;
            }
        } while (choice > 0);
    }

    private static void logInAsManager() {
        int choice;
        do {
            System.out.println(
                    "1. Company list" +
                            "\n2. Create a company" +
                            "\n0. Back"
            );
            choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (choice) {
                case 1:
                    openCompanyList();
                    break;
                case 2:
                    createCompany();
                    break;
                case 0:
                default:
                    break;
            }
        } while (choice > 0);
    }

    private static void logInAsCustomer() {
        int customerIndex = chooseCustomer();
        if (customerIndex < 1) {
            return;
        }
        Customer chosenCustomer = companyDao.getCustomers().get(customerIndex - 1);
        int choice;
        do {
            System.out.println(
                    "1. Rent a car" +
                            "\n2. Return a rented car" +
                            "\n3. My rented car" +
                            "\n0. Back");
            choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (choice) {
                case 1:
                    rentCar(chosenCustomer);
                    break;
                case 2:
                    returnCar(chosenCustomer);
                    break;
                case 3:
                    printCustomerRentedCar(chosenCustomer);
                    break;
                case 0:
                default:
                    break;
            }
        } while (choice != 0);
    }

    private static void openCompanyList() {
        int companyIndex = chooseCompany();
        if (companyIndex < 1) {
            return;
        }
        Company company = companyDao.getCompanies().get(companyIndex - 1);
        System.out.printf("'%s' company\n", company.getName());
        int choice;
        do {
            System.out.println(
                    "1. Car list\n" +
                            "2. Create a car\n" +
                            "0. Back"
            );
            choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (choice) {
                case 1:
                    openCarList(company);
                    break;
                case 2:
                    createCar(company);
                    break;
                case 0:
                default:
                    break;
            }
        } while (choice > 0);
    }

    private static void openCarList(Company company) {
        List<Car> cars = companyDao.getCarsByCompany(company);
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!\n");
        } else {
            System.out.println("Car list:");
            printList(cars);
            System.out.println();
        }
    }

    private static int chooseCompany() {
        List<Company> companies = companyDao.getCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!\n");
            return -100;
        }
        System.out.println("Choose a company:");
        printList(companies);
        System.out.println("0. Back");
        int companyIndex = Integer.parseInt(scanner.nextLine());
        System.out.println();
        return companyIndex;
    }

    private static int chooseCustomer() {
        List<Customer> customers = companyDao.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!\n");
            return -100;
        }
        System.out.println("Choose a customer:");
        printList(customers);
        System.out.println("0. Back");
        int customerIndex = Integer.parseInt(scanner.nextLine());
        System.out.println();
        return customerIndex;
    }

    private static void returnCar(Customer customer) {
        if (customer.getRentedCar() == null) {
            System.out.println("You didn't rent a car!\n");
            return;
        }
        companyDao.returnCar(customer);
        System.out.println("You've returned a rented car!");
    }

    private static void printCustomerRentedCar(Customer customer) {
        if (customer.getRentedCar() == null) {
            System.out.println("You didn't rent a car!");
        } else {
            System.out.println("Your rented car:");
            System.out.println(customer.getRentedCar().getName());
            System.out.println("Company:");
            System.out.println(customer.getRentedCar().getCompany().getName() + "\n");
        }
    }

    private static void rentCar(Customer customer) {
        if (customer.getRentedCar() != null) {
            System.out.println("You've already rented a car!\n");
            return;
        }
        int companyIndex = chooseCompany();
        if (companyIndex < 1) {
            return;
        }
        Company chosenCompany = companyDao.getCompanies().get(companyIndex - 1);
        int carIndex = chooseCar(chosenCompany);
        if (carIndex < 1) {
            return;
        }
        Car chosenCar = companyDao.getCarsByCompany(chosenCompany).get(carIndex - 1);
        companyDao.rentCar(customer, chosenCar);
        System.out.printf("You rented '%s'\n\n", chosenCar.getName());
        customer.setRentedCar(chosenCar);
    }

    private static int chooseCar(Company company) {
        List<Car> cars = companyDao.getAvailableCarsByCompany(company);
        if (cars.isEmpty()) {
            System.out.printf("No available cars in the '%s' company\n", company.getName());
            return -100;
        }
        System.out.println("Choose a car:");
        printList(cars);
        System.out.println("0. Back");
        int carIndex = Integer.parseInt(scanner.nextLine());
        System.out.println();
        return carIndex;
    }

    private static void createCustomer() {
        System.out.println("Enter the customer name:");
        companyDao.createCustomer(new Customer(scanner.nextLine()));
        System.out.println("The customer was added!\n");
    }

    private static void createCompany() {
        System.out.println("Enter the company name:");
        String name = new Scanner(System.in).nextLine();
        companyDao.createCompany(new Company(name));
        System.out.println("The company was created!\n");
    }

    private static void createCar(Company company) {
        System.out.println("Enter the car name:");
        String name = scanner.nextLine();
        companyDao.createCar(new Car(name, company));
        System.out.println("The car was added!\n");
    }

    private static void printList(List<?> list) {
        int index = 1;
        for (Object item : list) {
            System.out.printf("%d. %s\n", index++, item.toString());
        }
    }
}