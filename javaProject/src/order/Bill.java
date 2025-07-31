package order;

import java.io.*;
import java.util.Map;
import menu.FoodItem;
import interfaces.Billable;

public class Bill implements Billable {
    private Order order;

    public Bill(Order order) {
        this.order = order;
    }

    public double calculateBill() {
        double total = 0;
        for (Map.Entry<FoodItem, Integer> entry : order.getItems().entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void generateBillFile() {
        try {
            // Ensure the data directory exists
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter("data/bills.txt", true));
            bw.write("----- BILL -----\n");
            for (Map.Entry<FoodItem, Integer> entry : order.getItems().entrySet()) {
                bw.write(entry.getKey().getName() + " x" + entry.getValue() + "\n");
            }
            bw.write("Total: ₹" + calculateBill() + "\n\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 