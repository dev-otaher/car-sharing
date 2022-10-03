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
                            "\n0. Exit"
            );
            choice = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (choice) {
                case 1:
                    logInAsManager();
                    break;
                case 0:
                default:
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

    private static void createCompany() {
        System.out.println("Enter the company name:");
        String name = new Scanner(System.in).nextLine();
        companyDao.createCompany(new Company(name));
        System.out.println("The company was created!\n");
    }

    public static void openCompanyList() {
        int companyIndex = chooseCompany();
        if (companyIndex < 1) {
            return;
        }
        Company company = companyDao.getCompanies().get(companyIndex - 1);
        int choice;
        System.out.printf("'%s' company\n", company.getName());
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
                default:
                    break;
            }
        } while (choice > 0);
    }

    private static int chooseCompany() {
        List<Company> companies = companyDao.getCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!\n");
            return -100;
        }
        System.out.println("Choose the company:");
        printList(companies);
        System.out.println("0. Back");
        int companyIndex = Integer.parseInt(scanner.nextLine());
        System.out.println();
        return companyIndex;
    }

    private static void printList(List<?> list) {
        int index = 1;
        for (Object object : list) {
            System.out.printf("%d. %s\n", index++, object.toString());
        }
    }

    private static void createCar(Company company) {
        System.out.println("Enter the car name:");
        String name = scanner.nextLine();
        companyDao.createCar(new Car(name, company));
        System.out.println("The car was added!\n");
    }

    private static void openCarList(Company company) {
        List<Car> cars = companyDao.getCarsByCompany(company);
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!\n");
        } else {
            System.out.println("Car list:");
            printList(cars);
        }
    }
}