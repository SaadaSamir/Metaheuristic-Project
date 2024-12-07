package projet_metaheuristique_P1;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import projet_metaheuristique_P1.AStarAlgo.Item;
import projet_metaheuristique_P1.AStarAlgo.State;




	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.Comparator;
	import java.util.List;

	public class AStarHeuristic {
	    
	    static class Sack {
	        private int capacity;
	        private List<Item> content;
	        private int totalValue;
	        private int totalWeight;

	        public int calculateTotalValue() {
	            totalValue = 0;
	            for (Item item : content) {
	                totalValue += item.getValue();
	            }
	            return totalValue;
	        }

	        public int calculateTotalWeight() {
	            totalWeight = 0;
	            for (Item item : content) {
	                totalWeight += item.getWeight();
	            }
	            return totalWeight;
	        }

	        public int calculateRemainingCapacity() {
	            return capacity - calculateTotalWeight();
	        }

	        public void setCapacity(int capa) {
	            this.capacity = capa;
	        }
	        public int getCapacity() {
	           return this.capacity;
	        }

	        public void setContent(List<Item> c) {
	            content = c;
	        }

	        // Add getters for totalValue, totalWeight, and capacity if needed
	    }

	    // g function: returns normalized total value of the state
	    public static double g(State state, List<Item> itemsT) {
	        double totalValue = state.getTotalValue();
	         
	        return totalValue;
	    }
	    // h function: returns normalized remaining capacity
	    public static double h(State state,  List<Integer> capacities, List<Item> items) {
	        // Get remaining capacities for each knapsack
	        List<Integer> remainingCapacities = new ArrayList<>();
	        Set<Item> packedItems = new HashSet<>();
	        
	        for (int i = 0; i < state.getSacks().size(); i++) {
	            List<Item> sack = state.getSacks().get(i);
	            int totalWeight = sack.stream().mapToInt(Item::getWeight).sum();
	            remainingCapacities.add(capacities.get(i) - totalWeight);
	            packedItems.addAll(sack);
	        }

	        // Filter out items that are already in the knapsacks
	        List<Item> unpackedItems = new ArrayList<>();
	        for (Item item : items) {
	            if (!packedItems.contains(item)) {
	                unpackedItems.add(item);
	            }
	        }

	        // Sort items by value-to-weight ratio in descending order
	        unpackedItems.sort((item1, item2) -> Double.compare((double) item2.getValue() / item2.getWeight(), (double) item1.getValue() / item1.getWeight()));

	        double maxValueEstimate = 0.0;

	        for (Item item : unpackedItems) {
	            for (int i = 0; i < remainingCapacities.size(); i++) {
	                if (item.getWeight() <= remainingCapacities.get(i)) {
	                    maxValueEstimate += item.getValue();
	                    remainingCapacities.set(i, remainingCapacities.get(i) - item.getWeight());
	                    break;
	                } else {
	                    // Take fraction of the item that fits
	                    double fraction = (double) remainingCapacities.get(i) / item.getWeight();
	                    maxValueEstimate += item.getValue() * fraction;
	                    remainingCapacities.set(i, 0);
	                }
	            }
	        }

	        return maxValueEstimate;
	    }
	    // Function to calculate the maximum possible value for normalization
	    private static double getMaxValue(State state, List<Item> i) {
	        List<Item> allItems = i; // Assume this method exists and returns all items in consideration
	        return allItems.stream().mapToDouble(Item::getValue).sum();
	    }

	    
	    // Item and State classes would be defined in projet_metaheuristique_P1.AStarAlgo package
	

  
	}
    


