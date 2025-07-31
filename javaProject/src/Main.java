import menu.*;
import order.*;
import exceptions.InvalidOrderException;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // User Authentication
        String correctUser = "admin";
        String correctPass = "password";
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        if (!user.equals(correctUser) || !pass.equals(correctPass)) {
            System.out.println("Authentication failed. Exiting.");
            return;
        }
        System.out.println("Login successful!\n");

        List<FoodItem> menu = new ArrayList<>();
        menu.add(new VegItem("Paneer Butter Masala", 180, 10));
        menu.add(new NonVegItem("Chicken Biryani", 250, 5));
        menu.add(new VegItem("Veg Burger", 100, 20));

        Order order = new Order();

        while (true) {
            System.out.println("\n------ Restaurant POS ------");
            System.out.println("1. View Menu");
            System.out.println("2. Add to Order");
            System.out.println("3. Remove Item from Order");
            System.out.println("4. View Current Order");
            System.out.println("5. Filter Menu (Veg/Non-Veg/Price)");
            System.out.println("6. Edit Order Quantity");
            System.out.println("7. Generate Bill & Exit");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");
            int choice;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // clear buffer
                continue;
            }

            switch (choice) {
                case 1:
                    for (FoodItem item : menu)
                        System.out.println(item);
                    break;
                case 2:
                    System.out.print("Enter item name: ");
                    sc.nextLine(); // clear buffer
                    String name = sc.nextLine();
                    System.out.print("Enter quantity: ");
                    int qty;
                    try {
                        qty = sc.nextInt();
                        if (qty <= 0) {
                            System.out.println("Quantity must be positive.");
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid quantity. Please enter a positive number.");
                        sc.nextLine();
                        break;
                    }
                    try {
                        FoodItem selected = menu.stream()
                            .filter(f -> f.getName().equalsIgnoreCase(name))
                            .findFirst()
                            .orElseThrow(() -> new InvalidOrderException("Item not found."));
                        order.addItem(selected, qty);
                        System.out.println("Added to order.");
                    } catch (InvalidOrderException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter item name to remove: ");
                    sc.nextLine(); // clear buffer
                    String removeName = sc.nextLine();
                    FoodItem toRemove = menu.stream()
                        .filter(f -> f.getName().equalsIgnoreCase(removeName))
                        .findFirst()
                        .orElse(null);
                    if (toRemove == null) {
                        System.out.println("Item not found in menu.");
                        break;
                    }
                    if (!order.getItems().containsKey(toRemove)) {
                        System.out.println("Item not in order.");
                        break;
                    }
                    order.removeItem(toRemove);
                    System.out.println("Item removed from order and stock restored.");
                    break;
                case 4:
                    order.printOrderSummary();
                    break;
                case 5:
                    System.out.println("Filter by: 1. Veg  2. Non-Veg  3. Price Range");
                    int filterChoice = sc.nextInt();
                    if (filterChoice == 1) {
                        menu.stream().filter(f -> f.getType().equalsIgnoreCase("Veg")).forEach(System.out::println);
                    } else if (filterChoice == 2) {
                        menu.stream().filter(f -> f.getType().equalsIgnoreCase("Non-Veg")).forEach(System.out::println);
                    } else if (filterChoice == 3) {
                        System.out.print("Enter min price: ");
                        double min = sc.nextDouble();
                        System.out.print("Enter max price: ");
                        double max = sc.nextDouble();
                        menu.stream().filter(f -> f.getPrice() >= min && f.getPrice() <= max).forEach(System.out::println);
                    } else {
                        System.out.println("Invalid filter choice.");
                    }
                    break;
                case 6:
                    System.out.print("Enter item name to update: ");
                    sc.nextLine(); // clear buffer
                    String editName = sc.nextLine();
                    FoodItem toEdit = menu.stream()
                        .filter(f -> f.getName().equalsIgnoreCase(editName))
                        .findFirst()
                        .orElse(null);
                    if (toEdit == null) {
                        System.out.println("Item not found in menu.");
                        break;
                    }
                    if (!order.getItems().containsKey(toEdit)) {
                        System.out.println("Item not in order.");
                        break;
                    }
                    System.out.print("Enter new quantity: ");
                    int newQty;
                    try {
                        newQty = sc.nextInt();
                        if (newQty <= 0) {
                            System.out.println("Quantity must be positive.");
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid quantity. Please enter a positive number.");
                        sc.nextLine();
                        break;
                    }
                    int currentQty = order.getItems().get(toEdit);
                    if (newQty > currentQty) {
                        int diff = newQty - currentQty;
                        if (!toEdit.isAvailable(diff)) {
                            System.out.println("Insufficient stock to increase quantity.");
                            break;
                        }
                        order.getItems().put(toEdit, newQty);
                        toEdit.reduceQuantity(diff);
                    } else if (newQty < currentQty) {
                        int diff = currentQty - newQty;
                        order.getItems().put(toEdit, newQty);
                        toEdit.restoreQuantity(diff);
                    }
                    System.out.println("Order quantity updated.");
                    break;
                case 7:
                    // Discounts/Offers
                    System.out.print("Enter discount percentage (0 for none): ");
                    double discount = 0;
                    try {
                        discount = sc.nextDouble();
                        if (discount < 0 || discount > 100) {
                            System.out.println("Invalid discount. Skipping discount.");
                            discount = 0;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Skipping discount.");
                        sc.nextLine();
                    }
                    double taxRate = 0.05; // 5% tax
                    Bill bill = new Bill(order);
                    double subtotal = bill.calculateBill();
                    double discountAmount = subtotal * (discount / 100.0);
                    double taxed = (subtotal - discountAmount) * (1 + taxRate);
                    System.out.printf("Subtotal: ₹%.2f\n", subtotal);
                    if (discount > 0) System.out.printf("Discount: -₹%.2f\n", discountAmount);
                    System.out.printf("Tax (5%%): ₹%.2f\n", (subtotal - discountAmount) * taxRate);
                    System.out.printf("Total Amount: ₹%.2f\n", taxed);
                    bill.generateBillFile();
                    System.out.println("Bill saved. Exiting...");
                    return;
                case 8:
                    System.out.println("Exiting without generating bill.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
} 