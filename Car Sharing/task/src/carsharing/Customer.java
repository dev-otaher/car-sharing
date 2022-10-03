package carsharing;

public class Customer {
    private int id;
    private String name;
    private Car rentedCar;

    public Customer(String name) {
        this.name = name;
    }

    public Customer(int id, String name, Car rentedCar) {
        this.id = id;
        this.name = name;
        this.rentedCar = rentedCar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Car getRentedCar() {
        return rentedCar;
    }

    public void setRentedCar(Car rentedCar) {
        this.rentedCar = rentedCar;
    }

    @Override
    public String toString() {
        return name;
    }
}
