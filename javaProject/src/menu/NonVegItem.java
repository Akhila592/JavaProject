package menu;

public class NonVegItem extends FoodItem {
    public NonVegItem(String name, double price, int qty) {
        super(name, price, qty);
    }

    public String getType() {
        return "Non-Veg";
    }
} 