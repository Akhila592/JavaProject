package menu;

public class VegItem extends FoodItem {
    public VegItem(String name, double price, int qty) {
        super(name, price, qty);
    }

    public String getType() {
        return "Veg";
    }
} 