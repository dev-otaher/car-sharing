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
            switch (choice) {
                case 1:
                    printCompanies();
                    break;
                case 2:
                    createCompany();
                    break;
                case 0:
                default:
                    System.out.println();
                    break;
            }
        } while (choice > 0);
    }

    public static void printCompanies() {
        List<Company> companies = companyDao.getCompanies();
        System.out.println();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            System.out.println("Company list:");
            int i = 1;
            for (Company company : companyDao.getCompanies()) {
                System.out.printf("%d. %s\n", i++, company.getName());
            }
        }
        System.out.println();
    }

    private static void createCompany() {
        System.out.println("\nEnter the company name:");
        String name = new Scanner(System.in).nextLine();
        companyDao.insertCompany(new Company(name));
        System.out.println("The company was created!\n");
    }
}