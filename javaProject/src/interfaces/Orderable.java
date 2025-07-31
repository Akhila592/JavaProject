package interfaces;

public interface Orderable {
    void addItem(menu.FoodItem item, int quantity) throws exceptions.InvalidOrderException;
} 