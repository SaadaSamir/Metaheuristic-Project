package projet_metaheuristique_P1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JTextArea;

import projet_metaheuristique_P1.BasePopulation.Item;



public class BeeSwarmOptimizationMKP {
	
	
	public static DataSavedMeta launchBSO(List<Integer> capacities, List<Item> items , int MaxIteration, int flip, int bees, JTextArea resultsArea, JTextArea metricsArea) {
		DataSavedMeta dataBSO = new DataSavedMeta(0, 0, false, 0);
		long startTime = System.currentTimeMillis();
		int iteration = 0;
		System.out.println(MaxIteration);
		List<InitialSolution> tabooList = new ArrayList<>();
		List<Sack> sacks = new ArrayList<>();
		for (int cap: capacities) {
			sacks.add(new Sack(cap, new ArrayList<>()));
		}
		
		InitialSolution sRef = generateInitialSolution(items, sacks);
		
		while(iteration < MaxIteration) {
			tabooList.add(sRef);
        List<Item> remainingItems = new ArrayList<>(items);
        remainingItems.removeAll(sRef.getSacks().stream()
                .flatMap(sack -> sack.getItems().stream())
                .toList());

        List<InitialSolution> searchPoints = getSearchPoints(sRef, remainingItems, bees);
        System.out.println(searchPoints.size());
        // Print search points
        for (int i = 0; i < searchPoints.size(); i++) {
            appendText(resultsArea, "Search Point " + (i + 1) + ":");
            printSacks(searchPoints.get(i).getSacks(), resultsArea);
        }
        List<InitialSolution> newsearchPoints = localSearch(searchPoints, flip, resultsArea, tabooList);
        
        for (int i = 0; i < searchPoints.size(); i++) {
        	appendText(resultsArea, "New Search Point " + (i) + ":");
            printSacks(newsearchPoints.get(i).getSacks(), resultsArea);
        }
        
         sRef = getBestSolution(newsearchPoints, resultsArea);
         printSacks(sRef.getSacks(), resultsArea);
         iteration++;
		}
		long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        double durationSeconds = (durationMillis / 1000.0);
        dataBSO.setDuration(durationSeconds);
       
		
		int targetVal = calculateTotalValueOfAllItems(items);
		
		double val = ((double) sRef.calculateTotalValue()/ targetVal);
		dataBSO.setSatRate(val);
		dataBSO.setSatisfiable(val==targetVal);
		dataBSO.setNumItems(items.size());
		
		
        return dataBSO;
		
	}
	
	

    public static InitialSolution generateInitialSolution(List<Item> items, List<Sack> sacks) {
        // Sort items by value-to-weight ratio in descending order
        Collections.sort(items, Comparator.comparingDouble(Item::getValueToWeightRatio).reversed());

        // Sort sacks by capacity in ascending order
        sacks.sort(Comparator.comparingInt(Sack::getCapacity));

        // Iterate through sorted items
        for (Item item : items) {
            // Iterate through sacks
            for (Sack sack : sacks) {
                // Attempt to place item in sack if it fits and sack is not full
                if (sack.getRemainingCapacity() >= item.getWeight() && !sack.isFull()) {
                    sack.addItem(item);
                    break; // Move to the next item after placing it in a sack
                }
            }
        }

        // Print sacks with items
        

        // Return the initial solution
        return new InitialSolution(sacks);
    }

    public static List<InitialSolution> getSearchPoints(InitialSolution initialSolution, List<Item> remainingItems, int numSearchPoints) {
        List<InitialSolution> searchPoints = new ArrayList<>();

        for (int k = 0; k < numSearchPoints; k++) {
            InitialSolution modifiedSolution = initialSolution.copy();

            // Shuffle the items in the modified solution
            List<Item> shuffledItems = modifiedSolution.getSacks().stream()
                    .flatMap(sack -> sack.getItems().stream())
                    .collect(Collectors.toList());
            Collections.shuffle(shuffledItems);

            // Clear items from the sacks
            modifiedSolution.getSacks().forEach(sack -> sack.getItems().clear());

            // Try redistributing the shuffled items among the sacks
            for (Item item : shuffledItems) {
                for (Sack sack : modifiedSolution.getSacks()) {
                    if (sack.getRemainingCapacity() >= item.getWeight()) {
                        sack.addItem(item);
                        break;
                    }
                }
            }

            // Try to fit additional items from remaining items list
            for (Item remainingItem : remainingItems) {
                for (Sack sack : modifiedSolution.getSacks()) {
                    if (sack.getRemainingCapacity() >= remainingItem.getWeight()) {
                        sack.addItem(remainingItem);
                        break;
                    }
                }
            }

            searchPoints.add(modifiedSolution);
        }

        return searchPoints;
    }
    
    public static List<InitialSolution> localSearch(List<InitialSolution> searchPoints,int flip, JTextArea resultsArea, List<InitialSolution> taboo) {
        List<InitialSolution> newSearchPoints = new ArrayList<>();

        // Iterate over each solution in the search points
        for (InitialSolution solution : searchPoints) {
            // Flip each item in each sack and check if the new solution is valid
            for (int i = 0; i < solution.getSacks().size(); i++) {
                InitialSolution newSolution = flipItemsInSack(solution, i, resultsArea);
                if (isValidSolution(newSolution)) {
                    newSearchPoints.add(newSolution);
                }
            }
        }

        return newSearchPoints;
    }

    private static InitialSolution flipItemsInSack(InitialSolution solution, int sackIndex, JTextArea resultsArea) {
        InitialSolution modifiedSolution = solution.copy();
        Sack sack = modifiedSolution.getSacks().get(sackIndex);
        List<Item> items = sack.getItems();
        Collections.rotate(items, 1); // Rotate the items in the sack by one position
        appendText(resultsArea, "Generated Solution:");
        
        return modifiedSolution;
    }
    public static int calculateTotalValueOfAllItems(List<Item> items) {
        int totalValue = 0;
        for (Item item : items) {
            totalValue += item.value;
        }
        return totalValue;
    }

    private static boolean isValidSolution(InitialSolution solution) {
        // Check if the solution respects the capacity constraints of all sacks
        for (Sack sack : solution.getSacks()) {
            if (sack.getRemainingCapacity() < 0) {
                return false;
            }
        }
        return true;
    }
    
    public static InitialSolution getBestSolution(List<InitialSolution> searchPoints, JTextArea resultsArea) {
        InitialSolution bestSolution = null;
        int maxTotalValue = Integer.MIN_VALUE;

        for (InitialSolution solution : searchPoints) {
            int totalValue = solution.getSacks().stream()
                    .flatMap(sack -> sack.getItems().stream())
                    .mapToInt(Item::getValue)
                    .sum();
            if (totalValue > maxTotalValue) {
                maxTotalValue = totalValue;
                bestSolution = solution;
            }
        }

        if (bestSolution != null) {
        	appendText(resultsArea,"Best Solution:\n");
            
        	appendText(resultsArea, "Max Total Value: " + maxTotalValue);
        } else {
        	appendText(resultsArea, "No best solution found.");
        }

        return bestSolution;
    }
    private static boolean isInTabooList(InitialSolution solution, List<InitialSolution> tabooList) {
        for (InitialSolution tabooSolution : tabooList) {
            if (areSolutionsEqual(solution, tabooSolution)) {
                return true;
            }
        }
        return false;
    }

    private static boolean areSolutionsEqual(InitialSolution solution1, InitialSolution solution2) {
        List<Sack> sacks1 = solution1.getSacks();
        List<Sack> sacks2 = solution2.getSacks();

        if (sacks1.size() != sacks2.size()) {
            return false;
        }

        for (int i = 0; i < sacks1.size(); i++) {
            Sack sack1 = sacks1.get(i);
            Sack sack2 = sacks2.get(i);

            List<Item> items1 = sack1.getItems();
            List<Item> items2 = sack2.getItems();

            if (items1.size() != items2.size()) {
                return false;
            }

            for (int j = 0; j < items1.size(); j++) {
                Item item1 = items1.get(j);
                Item item2 = items2.get(j);

                if (item1.getWeight() != item2.getWeight() || item1.getValue() != item2.getValue()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void printSacks(List<Sack> sacks, JTextArea resultsArea) {
        for (Sack sack : sacks) {
            appendText(resultsArea, "Sack Capacity: " + sack.getCapacity() + "\n");
            appendText(resultsArea, "Items in Sack:\n");
            for (Item item : sack.getItems()) {
                appendText(resultsArea, "  Weight: " + item.getWeight() + ", Value: " + item.getValue() + "\n");
            }
            appendText(resultsArea, "\n");
        }
    }

    private static void appendText(JTextArea area, String text) {
        area.append(text);
        area.setCaretPosition(area.getDocument().getLength()); // Scroll to the bottom
    }
	
    // Item class representing an item with weight, value, and value-to-weight ratio
    public static class Item {
        private int weight;
        private int value;

        public Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }

        public double getValueToWeightRatio() {
            return (double) value / weight;
        }

        public int getWeight() {
            return weight;
        }

        public int getValue() {
            return value;
        }
    }

    // Sack class representing a knapsack with capacity and items
    public static class Sack {
        private int capacity;
        private List<Item> items;

        public Sack(int capacity, List<Item> items) {
            this.capacity = capacity;
            this.items = items;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getRemainingCapacity() {
            int usedCapacity = items.stream().mapToInt(Item::getWeight).sum();
            return capacity - usedCapacity;
        }

        public boolean isFull() {
            return getRemainingCapacity() <= 0;
        }

        public void addItem(Item item) {
            items.add(item);
        }

        public List<Item> getItems() {
            return items;
        }
    }

    // InitialSolution class representing the initial solution with allocated items to sacks
    public static class InitialSolution {
        private List<Sack> sacks;

        public InitialSolution(List<Sack> sacks) {
            this.sacks = sacks;
        }

        public List<Sack> getSacks() {
            return sacks;
        }

        // Create a deep copy of the InitialSolution object
        public InitialSolution copy() {
            List<Sack> copiedSacks = new ArrayList<>();
            for (Sack sack : sacks) {
                List<Item> copiedItems = new ArrayList<>(sack.getItems());
                copiedSacks.add(new Sack(sack.getCapacity(), copiedItems));
            }
            return new InitialSolution(copiedSacks);
        }
        public int calculateTotalValue() {
            int totalValue = 0;
            for (Sack sack : sacks) {
                for (Item item : sack.getItems()) {
                    totalValue += item.getValue();
                }
            }
            return totalValue;
        }
        
    }
}

