package menu;

public abstract class FoodItem {
    protected String name;
    protected double price;
    protected int quantityAvailable;

    public FoodItem(String name, double price, int qty) {
        this.name = name;
        this.price = price;
        this.quantityAvailable = qty;
    }

    public abstract String getType();

    public String getName() { return name; }
    public double getPrice() { return price; }

    public boolean isAvailable(int qty) {
        return quantityAvailable >= qty;
    }

    public void reduceQuantity(int qty) {
        quantityAvailable -= qty;
    }

    public void restoreQuantity(int qty) {
        quantityAvailable += qty;
    }

    public String toString() {
        return name + " (" + getType() + ") - ₹" + price + " [" + quantityAvailable + "]";
    }
} 