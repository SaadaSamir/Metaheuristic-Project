package projet_metaheuristique_P1;

import java.util.List;

import javax.swing.JTextArea;

public class ItemsDisplay {
    public static void displayItems(List<MultipleKnapsack.Item> items, JTextArea metricsArea, int j) {
        if (items == null || items.isEmpty()) {
            metricsArea.append("No items to display.\n");
            return;
        }else {
        	metricsArea.append("Sample number "+ (j+1) +"\n");
        }

        for (int i = 0; i < items.size(); i++) {
            MultipleKnapsack.Item item = items.get(i);
            metricsArea.append("Item" + (i + 1) + ": Value: " + item.getValue() + ", Weight: " + item.getWeight() + "\n");
        }
    }
    public static void displayItemsBFS(List<MultipleKnapsackBFS.Item> items, JTextArea metricsArea, int j) {
        if (items == null || items.isEmpty()) {
            metricsArea.append("No items to display.\n");
            return;
        }else {
        	metricsArea.append("Sample number "+ (j+1) +"\n");
        }

        for (int i = 0; i < items.size(); i++) {
        	MultipleKnapsackBFS.Item item = items.get(i);
            metricsArea.append("Item" + (i + 1) + ": Value: " + item.getValue() + ", Weight: " + item.getWeight() + "\n");
        }
    }
    public static void displayItemsASTAR(List<AStarAlgo.Item> items, JTextArea metricsArea, int j) {
        if (items == null || items.isEmpty()) {
            metricsArea.append("No items to display.\n");
            return;
        }else {
        	metricsArea.append("Sample number "+ (j+1) +"\n");
        }

        for (int i = 0; i < items.size(); i++) {
        	AStarAlgo.Item item = items.get(i);
            metricsArea.append("Item" + (i + 1) + ": Value: " + item.getValue() + ", Weight: " + item.getWeight() + "\n");
        }
    }
}




