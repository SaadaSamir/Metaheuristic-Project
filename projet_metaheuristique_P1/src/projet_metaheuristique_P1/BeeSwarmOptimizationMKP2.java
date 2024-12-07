package projet_metaheuristique_P1;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JTextArea;

import projet_metaheuristique_P1.BasePopulation.Item;

public class BeeSwarmOptimizationMKP2 {

    public static DataSavedMeta launchBSO(List<Integer> capacities, List<Item> items, int MaxIteration, int flip, int bees, JTextArea resultsArea, JTextArea metricsArea) {
        DataSavedMeta dataBSO = new DataSavedMeta(0, 0, false, 0);
        long startTime = System.currentTimeMillis();
        int iteration = 0;
        List<InitialSolution> tabooList = new ArrayList<>();
        List<Sack> sacks = capacities.stream().map(cap -> new Sack(cap, new ArrayList<>())).collect(Collectors.toList());

        InitialSolution sRef = generateInitialSolution(items, sacks);

        while (iteration < MaxIteration) {
            tabooList.add(sRef);
            List<Item> remainingItems = new ArrayList<>(items);
            remainingItems.removeAll(sRef.getSacks().stream()
                    .flatMap(sack -> sack.getItems().stream())
                    .collect(Collectors.toList()));

            List<InitialSolution> searchPoints = getSearchPoints(sRef, remainingItems, bees);
            printSolutions(searchPoints, resultsArea, "Search Point ");

            List<InitialSolution> newsearchPoints = localSearch(searchPoints, flip, resultsArea, tabooList);
            printSolutions(newsearchPoints, resultsArea, "New Search Point ");

            sRef = getBestSolution(newsearchPoints, resultsArea);
            printSacks(sRef.getSacks(), resultsArea);
            iteration++;
        }

        long endTime = System.currentTimeMillis();
        dataBSO.setDuration((endTime - startTime) / 1000.0);
        int targetVal = calculateTotalValueOfAllItems(items);
        double val = (double) sRef.calculateTotalValue() / targetVal;
        dataBSO.setSatRate(val);
        dataBSO.setSatisfiable(sRef.calculateTotalValue() == targetVal);
        dataBSO.setNumItems(items.size());

        return dataBSO;
    }

    public static InitialSolution generateInitialSolution(List<Item> items, List<Sack> sacks) {
        items.sort(Comparator.comparingDouble(Item::getValueToWeightRatio).reversed());
        sacks.sort(Comparator.comparingInt(Sack::getCapacity));

        for (Item item : items) {
            for (Sack sack : sacks) {
                if (sack.getRemainingCapacity() >= item.getWeight() && !sack.isFull()) {
                    sack.addItem(item);
                    break;
                }
            }
        }

        return new InitialSolution(sacks);
    }

    public static List<InitialSolution> getSearchPoints(InitialSolution initialSolution, List<Item> remainingItems, int numSearchPoints) {
        List<InitialSolution> searchPoints = new ArrayList<>();

        for (int k = 0; k < numSearchPoints; k++) {
            InitialSolution modifiedSolution = initialSolution.copy();

            List<Item> shuffledItems = modifiedSolution.getSacks().stream()
                    .flatMap(sack -> sack.getItems().stream())
                    .collect(Collectors.toList());
            Collections.shuffle(shuffledItems);

            modifiedSolution.getSacks().forEach(sack -> sack.getItems().clear());

            for (Item item : shuffledItems) {
                for (Sack sack : modifiedSolution.getSacks()) {
                    if (sack.getRemainingCapacity() >= item.getWeight()) {
                        sack.addItem(item);
                        break;
                    }
                }
            }

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

    public static List<InitialSolution> localSearch(List<InitialSolution> searchPoints, int flip, JTextArea resultsArea, List<InitialSolution> taboo) {
        List<InitialSolution> newSearchPoints = new ArrayList<>();

        for (InitialSolution solution : searchPoints) {
            for (int i = 0; i < solution.getSacks().size(); i++) {
                InitialSolution newSolution = flipItemsInSack(solution, i, flip, resultsArea);
                if (isValidSolution(newSolution) && !isInTabooList(newSolution, taboo)) {
                    newSearchPoints.add(newSolution);
                }
            }
        }

        return newSearchPoints;
    }

    private static InitialSolution flipItemsInSack(InitialSolution solution, int sackIndex, int flip, JTextArea resultsArea) {
        InitialSolution modifiedSolution = solution.copy();
        Sack sack = modifiedSolution.getSacks().get(sackIndex);
        List<Item> items = sack.getItems();

        if (items.size() >= flip) {
            Collections.rotate(items, flip);
        } else {
            Collections.rotate(items, items.size());
        }

        appendText(resultsArea, "Generated Solution:");

        return modifiedSolution;
    }

    public static int calculateTotalValueOfAllItems(List<Item> items) {
        return items.stream().mapToInt(Item::getValue).sum();
    }

    private static boolean isValidSolution(InitialSolution solution) {
        return solution.getSacks().stream().allMatch(sack -> sack.getRemainingCapacity() >= 0);
    }

    public static InitialSolution getBestSolution(List<InitialSolution> searchPoints, JTextArea resultsArea) {
        InitialSolution bestSolution = null;
        int maxTotalValue = Integer.MIN_VALUE;

        for (InitialSolution solution : searchPoints) {
            int totalValue = solution.calculateTotalValue();
            if (totalValue > maxTotalValue) {
                maxTotalValue = totalValue;
                bestSolution = solution;
            }
        }

        if (bestSolution != null) {
            appendText(resultsArea, "Best Solution:\n");
            appendText(resultsArea, "Max Total Value: " + maxTotalValue + "\n");
        } else {
            appendText(resultsArea, "No best solution found.\n");
        }

        return bestSolution;
    }

    private static boolean isInTabooList(InitialSolution solution, List<InitialSolution> tabooList) {
        return tabooList.stream().anyMatch(tabooSolution -> areSolutionsEqual(solution, tabooSolution));
    }

    private static boolean areSolutionsEqual(InitialSolution solution1, InitialSolution solution2) {
        if (solution1.getSacks().size() != solution2.getSacks().size()) return false;

        for (int i = 0; i < solution1.getSacks().size(); i++) {
            Sack sack1 = solution1.getSacks().get(i);
            Sack sack2 = solution2.getSacks().get(i);

            if (sack1.getItems().size() != sack2.getItems().size()) return false;

            for (int j = 0; j < sack1.getItems().size(); j++) {
                Item item1 = sack1.getItems().get(j);
                Item item2 = sack2.getItems().get(j);

                if (item1.getWeight() != item2.getWeight() || item1.getValue() != item2.getValue()) return false;
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
        area.setCaretPosition(area.getDocument().getLength());
    }

    private static void printSolutions(List<InitialSolution> solutions, JTextArea resultsArea, String label) {
        for (int i = 0; i < solutions.size(); i++) {
            appendText(resultsArea, label + (i + 1) + ":\n");
            printSacks(solutions.get(i).getSacks(), resultsArea);
        }
    }

    public static class Item {
        private final int weight;
        private final int value;

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

    public static class Sack {
        private final int capacity;
        private final List<Item> items;

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

    public static class InitialSolution {
        private final List<Sack> sacks;

        public InitialSolution(List<Sack> sacks) {
            this.sacks = sacks;
        }

        public List<Sack> getSacks() {
            return sacks;
        }

        public InitialSolution copy() {
            List<Sack> copiedSacks = sacks.stream()
                    .map(sack -> new Sack(sack.getCapacity(), new ArrayList<>(sack.getItems())))
                    .collect(Collectors.toList());
            return new InitialSolution(copiedSacks);
        }

        public int calculateTotalValue() {
            return sacks.stream()
                    .flatMap(sack -> sack.getItems().stream())
                    .mapToInt(Item::getValue)
                    .sum();
        }
    }
}
