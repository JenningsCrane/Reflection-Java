package Classes;

public class Car {
    private String name;
    private Integer horsePower;
    private Double discount;
    private Boolean leftHandDrive;
    private Long price;

    public Car() {
        this.name = "Default name";
        this.horsePower = 20;
        this.discount = 0.0;
        this.leftHandDrive = true;
        this.price = 1500000L;
    }

    public Car(String name, Integer horsePower, Double discount, Boolean leftHandDrive, Long price) {
        this.name = name;
        this.horsePower = horsePower;
        this.discount = discount;
        this.leftHandDrive = leftHandDrive;
        this.price = price;
    }

    public Long riseInPrice(Long markup) {
        this.price += markup;
        return price;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", horsePower=" + horsePower +
                ", discount=" + discount +
                ", leftHandDrive=" + leftHandDrive +
                ", price=" + price +
                '}';
    }
}
