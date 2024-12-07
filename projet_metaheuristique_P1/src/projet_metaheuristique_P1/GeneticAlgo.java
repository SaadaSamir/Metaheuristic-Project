package projet_metaheuristique_P1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JTextArea;


import projet_metaheuristique_P1.BasePopulation.Item;


public class GeneticAlgo {
	
	public static DataSavedMeta activateGeneticAlgorithm(List<Integer> capacities, List<BasePopulation.Item> items, int populationSize, int maxGenerations, double mutationRate, JTextArea resultsArea, JTextArea metricsArea) {
		DataSavedMeta data = new DataSavedMeta(0, 0, false, 0);
		System.out.println("pop "+populationSize);
		System.out.println("iter "+ maxGenerations);
		System.out.println("mutation "+mutationRate);
		long startTime = System.currentTimeMillis();
		List<BasePopulation.State> population = new ArrayList<>();
		population = generatePopulation(population, capacities, items, populationSize);
		printPopulation(population, resultsArea);
		BasePopulation.State bestState = new BasePopulation.State(new ArrayList<>(), 0, 0);
		
		for(int i=0; i<maxGenerations; i++) {
			
			System.out.println("popsize: "+population.size()+"\n");
		List<BasePopulation.State> parents = rankBasedSelection(population);
		System.out.println("popsizeafter: "+population.size()+"\n");
		
		resultsArea.append("******************parents: "+i+" ***********************");
		printPopulation(parents, resultsArea);
        List<BasePopulation.State> offspringPop = performUniformCrossover(parents, capacities, items, populationSize);
        printPopulation(offspringPop, resultsArea);
        mutatePopulation(offspringPop, mutationRate, capacities);
        resultsArea.append("******************offsprings: "+i+" ***********************");
        printPopulation(offspringPop, resultsArea);
        population = createNextPopulation(parents, offspringPop, populationSize);
        resultsArea.append("******************new gen: "+i+" ***********************");
        printPopulation(population, resultsArea);
        // Print the resulting population
        bestState = selectBestState(population);
        
       
		}
		
		
		long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        double durationSeconds = (durationMillis / 1000.0);
        data.setDuration(durationSeconds);
       
		
		int targetVal = calculateTotalValueOfAllItems(items);
		
		double val = ((double) bestState.getTotalValue()/ targetVal);
		data.setSatRate(val);
		data.setSatisfiable(val==targetVal);
		data.setNumItems(items.size());
		printBestState(population);
		
        return data;
		 
		  
	}

    public static List<BasePopulation.State> generatePopulation(List<BasePopulation.State> population, List<Integer> capacities, List<BasePopulation.Item> items, int populationSize) {
        

        while (population.size() < populationSize) {
            // Generate a random state and add it to the population
            BasePopulation.State state = generateState(capacities, items);
            population.add(state);
        }
        
        return population;
        
    }

    private static void printPopulation(List<BasePopulation.State> population, JTextArea resultArea) {
        resultArea.append("Generated Population:\n");
        for (BasePopulation.State state : population) {
            resultArea.append("State:" + state.getId() + "\n");
            for (int i = 0; i < state.sacks.size(); i++) {
                List<BasePopulation.Item> sack = state.sacks.get(i);
                resultArea.append("Sack " + (i + 1) + ": ");
                for (BasePopulation.Item item : sack) {
                    resultArea.append(item.toString() + " ");
                }
                resultArea.append("\n");
            }
            resultArea.append("Total Weight: " + state.totalWeight + "\n");
            resultArea.append("Total Value: " + state.getTotalValue() + "\n\n");
        }
    }

    private static BasePopulation.State generateState(List<Integer> capacities, List<BasePopulation.Item> items) {
        List<List<BasePopulation.Item>> sacks = new ArrayList<>();
        for (int i = 0; i < capacities.size(); i++) {
            sacks.add(new ArrayList<>()); // Initialize each sack as empty
        }

        // Shuffle the list of items
        Collections.shuffle(items);

        // Store seen items for duplicate check
        Set<Integer> seenItems = new HashSet<>();

        // Fill the sacks with shuffled items
        for (BasePopulation.Item item : items) {
            for (int sackIndex = 0; sackIndex < capacities.size(); sackIndex++) {
                int capacity = capacities.get(sackIndex);
                List<BasePopulation.Item> sack = sacks.get(sackIndex);
                if (canFit(capacity, sack, item) && !seenItems.contains(item.getId())) {
                    sack.add(item);
                    seenItems.add(item.getId());
                    break;
                }
            }
        }

        // Calculate the total weight of items in each sack
        int totalWeight = 0;
        for (List<BasePopulation.Item> sack : sacks) {
            totalWeight += calculateSackWeight(sack);
        }

        // Create and return the state
        return new BasePopulation.State(sacks, totalWeight, items.size());
    }
    public static List<BasePopulation.State> rankBasedSelection(List<BasePopulation.State> population) {
        List<BasePopulation.State> selectedParents = new ArrayList<>();
        List<BasePopulation.State> p = new ArrayList<>();
        p = population;
        int numParents = (p.size() / 2);

        // Sort the population based on fitness (ascending order)
        Collections.sort(p, Comparator.comparingDouble(BasePopulation.State::getFitness));

        // Calculate cumulative probabilities (normalized)
        double totalRank = p.size() * (p.size() + 1) / 2.0;
        double cumulativeProbability = 0.0;
        double[] cumulativeProbabilities = new double[p.size()];
        for (int i = 0; i < p.size(); i++) {
            cumulativeProbability += (i + 1) / totalRank;
            cumulativeProbabilities[i] = cumulativeProbability;
        }

        // Perform selection
        Random random = new Random();
        for (int i = 0; i < numParents; i++) {
            double r = random.nextDouble();
            int selectedIdx = 0;
            while (selectedIdx < p.size() - 1 && r > cumulativeProbabilities[selectedIdx]) {
                selectedIdx++;
            }
            selectedParents.add(p.remove(selectedIdx)); // Remove selected parent from population
        }

        return selectedParents;
    }

    public static List<BasePopulation.State> performUniformCrossover(List<BasePopulation.State> population, List<Integer> capacities, List<BasePopulation.Item> items, int pop) {
        List<BasePopulation.State> offspringPopulation = new ArrayList<>();

        for (int i = 0; i < population.size(); i += 2) {
            // Check if there are at least two parents left for crossover
        
            if (i + 1 < population.size()) {
                BasePopulation.State parent1 = population.get(i);
                BasePopulation.State parent2 = population.get(i + 1);

                // Perform uniform crossover
                BasePopulation.State offspringState1 = uniformCrossover(parent1, parent2);
                BasePopulation.State offspringState2 = uniformCrossover(parent1, parent2);

                // Repair offspring state for capacity constraints
                repairOffspringState(offspringState1, capacities, items);
                repairOffspringState(offspringState2, capacities, items);

                // Add the offspring to the new population
                offspringPopulation.add(offspringState1);
                offspringPopulation.add(offspringState2);
            } else {
                // If there's only one parent left, add it to the offspring population
                offspringPopulation.add(population.get(i));
            }
        }

        return offspringPopulation;
    }

    private static BasePopulation.State uniformCrossover(BasePopulation.State parent1, BasePopulation.State parent2) {
        // Initialize offspring state
        BasePopulation.State offspringState = new BasePopulation.State(new ArrayList<>(), 0, 0);

        // Perform uniform crossover at the level of items
        Random random = new Random();
        int maxSize = Math.min(parent1.sacks.size(), parent2.sacks.size());

        for (int i = 0; i < maxSize; i++) {
            List<BasePopulation.Item> offspringSack = new ArrayList<>();
            List<BasePopulation.Item> sack1 = parent1.sacks.get(i);
            List<BasePopulation.Item> sack2 = parent2.sacks.get(i);

            // Choose items from parent1 or parent2 with equal probability
            for (int j = 0; j < Math.min(sack1.size(), sack2.size()); j++) {
                BasePopulation.Item item1 = sack1.get(j);
                BasePopulation.Item item2 = sack2.get(j);
                if (random.nextBoolean()) {
                    offspringSack.add(item1);
                } else {
                    offspringSack.add(item2);
                }
            }

            offspringState.sacks.add(offspringSack);
        }

        // Update total value and total weight of the offspring state
        offspringState.updateTotalValue();
        offspringState.updateTotalWeight();

        return offspringState;
    }

    private static void repairOffspringState(BasePopulation.State offspringState, List<Integer> capacities, List<BasePopulation.Item> items) {
        // Remove duplicate items
        removeDuplicateItems(offspringState);

        // Try to fit new items into the offspring sacks
        fitNewItems(offspringState, capacities, items);

        // Update total value and total weight of the offspring state
        offspringState.updateTotalValue();
        offspringState.updateTotalWeight();
    }

    private static void removeDuplicateItems(BasePopulation.State offspringState) {
        Set<Integer> seenItems = new HashSet<>();
        List<List<BasePopulation.Item>> uniqueSacks = new ArrayList<>();

        for (List<BasePopulation.Item> sack : offspringState.sacks) {
            List<BasePopulation.Item> uniqueItems = new ArrayList<>();
            for (BasePopulation.Item item : sack) {
                if (!seenItems.contains(item.getId())) {
                    uniqueItems.add(item);
                    seenItems.add(item.getId());
                }
            }
            uniqueSacks.add(uniqueItems);
        }

        offspringState.sacks = uniqueSacks;
    }



    private static void fitNewItems(BasePopulation.State offspringState, List<Integer> capacities, List<BasePopulation.Item> items) {
        // Create a list of remaining items initially containing all items
        List<BasePopulation.Item> remainingItems = new ArrayList<>(items);

        // Remove items already present in the offspring state from the remaining items list
        for (List<BasePopulation.Item> sack : offspringState.sacks) {
            for (BasePopulation.Item item : sack) {
                // Check if the item's ID is similar to one of the items inside the offspring state
                remainingItems.removeIf(remainingItem -> remainingItem.getId() == item.getId());
            }
        }

        // Iterate through each sack
        for (int i = 0; i < offspringState.sacks.size(); i++) {
            List<BasePopulation.Item> sack = offspringState.sacks.get(i);
            int capacity = capacities.get(i);

            // Sort remaining items for the current sack
            remainingItems.sort((item1, item2) -> {
                double ratio1 = (double) item1.getValue() / (item1.getWeight() / capacity);
                double ratio2 = (double) item2.getValue() / (item2.getWeight() / capacity);
                return Double.compare(ratio2, ratio1); // Sort in descending order of ratio
            });

            // Iterate over the sorted list of remaining items and try to fit them into the sack
            Iterator<BasePopulation.Item> iterator = remainingItems.iterator();
            while (iterator.hasNext()) {
                BasePopulation.Item item = iterator.next();
                if (canFit(capacity, sack, item)) {
                    sack.add(item);
                    iterator.remove(); // Remove the item from remaining items once it's added to the sack
                }
            }
        }
    }



    public static void mutatePopulation(List<BasePopulation.State> population, double mutationRate, List<Integer> capacities) {
        Random random = new Random();

        for (BasePopulation.State state : population) {
            // Check if there are sacks available for mutation
            if (!state.sacks.isEmpty()) {
                // Apply mutation with probability mutationRate for each individual
                if (random.nextDouble() < mutationRate) {
                    // Select a sack randomly
                    int sackIndex = random.nextInt(state.sacks.size());
                    List<BasePopulation.Item> sack = state.sacks.get(sackIndex);

                    // Check if the sack is not empty
                    if (!sack.isEmpty()) {
                        // Select one item randomly from the sack
                        int itemIndex = random.nextInt(sack.size());
                        BasePopulation.Item selectedItem = sack.get(itemIndex);

                        // Generate a list of items available for swapping
                        List<BasePopulation.Item> availableItems = new ArrayList<>();
                        for (BasePopulation.State otherState : population) {
                            if (otherState != state) {
                                for (List<BasePopulation.Item> otherSack : otherState.sacks) {
                                    availableItems.addAll(otherSack);
                                }
                            }
                        }

                        // Remove items already present in the offspring state
                        availableItems.removeAll(sack);

                        // Check if there are available items for swapping
                        if (!availableItems.isEmpty()) {
                            // Randomly select one available item for swapping
                            BasePopulation.Item newItem = availableItems.get(random.nextInt(availableItems.size()));

                            // Swap the selected items
                            sack.set(itemIndex, newItem);

                            // Check if the swap violates capacity restrictions
                            if (!isValidSackCapacity(sack, capacities.get(sackIndex))) {
                                // Revert the swap
                                sack.set(itemIndex, selectedItem);
                            }
                        }
                    }
                }
            }
        }
    }



    public static List<BasePopulation.State> createNextPopulation(List<BasePopulation.State> parents, List<BasePopulation.State> offspring, int populationSize) {
        // Combine parents and offspring into a single list
        List<BasePopulation.State> combinedPopulation = new ArrayList<>(parents);
        
        combinedPopulation.addAll(offspring);

        // Sort the combined population by total value in descending order
        combinedPopulation.sort(Comparator.comparingInt(BasePopulation.State::getTotalValue).reversed());
        System.out.println("new pop: "+combinedPopulation.size()+"\n");
        // Return the top individuals up to the population size
        return combinedPopulation;
    }
    public static BasePopulation.State selectBestState(List<BasePopulation.State> population) {
        BasePopulation.State bestState = null;
        int highestValue = Integer.MIN_VALUE;

        for (BasePopulation.State state : population) {
            if (state.getTotalValue() > highestValue) {
                highestValue = state.getTotalValue();
                bestState = state;
            }
        }

        return bestState;
    }
    public static void printBestState(List<BasePopulation.State> population) {
        BasePopulation.State bestState = selectBestState(population);

        if (bestState != null) {
            System.out.println("Best State:");
            System.out.println("Total Value: " + bestState.getTotalValue());
            System.out.println("Total Weight: " + bestState.getTotalWeight());
            System.out.println("Items in Sacks:");

            for (int i = 0; i < bestState.sacks.size(); i++) {
                List<BasePopulation.Item> sack = bestState.sacks.get(i);
                System.out.print("Sack " + (i + 1) + ": ");
                for (BasePopulation.Item item : sack) {
                    System.out.print(item.toString() + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("Population is empty.");
        }
    }


    private static boolean isValidSackCapacity(List<BasePopulation.Item> sack, int capacity) {
        int totalWeight = calculateSackWeight(sack);
        return totalWeight <= capacity;
    }

   
    
    public static void evaluatePopulation(List<BasePopulation.State> population) {
        for (BasePopulation.State state : population) {
            int totalWeight = calculateTotalWeight(state);
            int totalValue = calculateTotalValue(state);
            state.setTotalWeight(totalWeight);
            state.setTotalValue(totalValue);
        }
    }

    private static int calculateTotalWeight(BasePopulation.State state) {
        int totalWeight = 0;
        for (List<BasePopulation.Item> sack : state.sacks) {
            totalWeight += calculateSackWeight(sack);
        }
        return totalWeight;
    }
    

    private static int calculateTotalValue(BasePopulation.State state) {
        int totalValue = 0;
        for (List<BasePopulation.Item> sack : state.sacks) {
            totalValue += calculateSackValue(sack);
        }
        return totalValue;
    }

    private static int calculateSackWeight(List<BasePopulation.Item> sack) {
        int weight = 0;
        for (BasePopulation.Item item : sack) {
            weight += item.weight;
        }
        return weight;
    }
    public static boolean canFit(int capacity, List<Item> sack, Item item) {
        if (sack == null) return true;
        int sackWeight = calculateSackWeight(sack);
        ;
        return sackWeight + item.weight <= capacity;
    }
    private static boolean canFithere(int capacity, List<List<BasePopulation.Item>> sacks, BasePopulation.Item item) {
        // Iterate through each sack in the state
        for (List<BasePopulation.Item> sack : sacks) {
            // Calculate the total weight of items in the current sack
            int totalWeightInSack = calculateSackWeight(sack);
            // Check if adding the item to the current sack would exceed its capacity
            if (totalWeightInSack + item.weight <= capacity) {
                // If the item can fit in the sack without exceeding its capacity, return true
                return true;
            }
        }
        // If the item cannot fit in any sack without exceeding capacity, return false
        return false;
    }

    private static int calculateSackValue(List<BasePopulation.Item> sack) {
        int value = 0;
        for (BasePopulation.Item item : sack) {
            value += item.value;
        }
        return value;
    }
    public static int calculateTotalValueOfAllItems(List<BasePopulation.Item> items) {
        int totalValue = 0;
        for (Item item : items) {
            totalValue += item.value;
        }
        return totalValue;
    }
}
