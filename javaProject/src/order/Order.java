package order;

import java.util.*;
import menu.FoodItem;
import interfaces.Orderable;
import exceptions.InvalidOrderException;

public class Order implements Orderable {
    private Map<FoodItem, Integer> items = new HashMap<>();

    public void addItem(FoodItem item, int quantity) throws InvalidOrderException {
        if (!item.isAvailable(quantity)) {
            throw new InvalidOrderException("Insufficient quantity for: " + item.getName());
        }
        items.put(item, items.getOrDefault(item, 0) + quantity);
        item.reduceQuantity(quantity);
    }

    public void removeItem(FoodItem item) {
        Integer qty = items.remove(item);
        if (qty != null) {
            item.restoreQuantity(qty);
        }
    }

    public void printOrderSummary() {
        if (items.isEmpty()) {
            System.out.println("Order is empty.");
            return;
        }
        double total = 0;
        System.out.println("\n--- Current Order ---");
        for (Map.Entry<FoodItem, Integer> entry : items.entrySet()) {
            FoodItem item = entry.getKey();
            int qty = entry.getValue();
            double itemTotal = item.getPrice() * qty;
            total += itemTotal;
            System.out.printf("%s x%d @ ₹%.2f each = ₹%.2f\n", item.getName(), qty, item.getPrice(), itemTotal);
        }
        System.out.printf("Total: ₹%.2f\n", total);
    }

    public Map<FoodItem, Integer> getItems() {
        return items;
    }
} 