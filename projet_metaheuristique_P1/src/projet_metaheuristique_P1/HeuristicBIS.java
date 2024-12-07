package projet_metaheuristique_P1;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import projet_metaheuristique_P1.AStarAlgo.Item;
import projet_metaheuristique_P1.AStarAlgo.State;

public class HeuristicBIS {
	
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
	        capacity = capa;
	    }

	    public void setContent(List<Item> c) {
	        content = c;
	    }
	    public List<Item> getContent() {
	        return content;
	    }
	    public int getCapacity() {
	        return capacity;
	    }

	    // Add getters for totalValue, totalWeight, and capacity if needed
	}
	

	public static double g(State state) {
	    double totalValue = 0;
	    List<List<Item>> sackContents = state.getSacks();

	    for (List<Item> sackItems : sackContents) {
	        for (Item item : sackItems) {
	            totalValue += item.getValue();
	        }
	    }

	    return totalValue;
	}

	
	public static int h(State state, List<Item> items, List<Integer> capacities) {
	    List<Item> remainingItems = new ArrayList<>(items); // Create a copy of the items list

	    // Remove items already present in sacks
	    for (List<Item> sackContent : state.getSacks()) {
	        remainingItems.removeAll(sackContent);
	    }
	    

	    // Sort remaining items by density in descending order
	    Collections.sort(remainingItems, new DensityComparator());

	    int totalValue = 0;

	    for (Item item : remainingItems) {
	        boolean packed = false;
	        for (List<Item> sackContent : state.getSacks()) {
	            int remainingCapacity = calculateRemainingCapacity(sackContent, state.getRemainingCapacities());
	            if (item.getWeight() <= remainingCapacity) {
	                sackContent.add(item);
	                totalValue += item.getValue();
	                packed = true;
	                break;
	            }
	        }
	        if (!packed) {
	            break; // No more items can be packed
	        }
	    }

	    return totalValue;
	}


    private static int calculateRemainingCapacity(List<Item> sackContent, int capacity) {
        int totalWeight = 0;
        for (Item item : sackContent) {
            totalWeight += item.getWeight();
        }
        return capacity - totalWeight;
    }

    private static int calculateRemainingCapacity(List<Item> sackContent, List<Integer> remainingCapacities) {
        int totalWeight = 0;
        for (Item item : sackContent) {
            totalWeight += item.getWeight();
        }
        // Assuming the remaining capacities list corresponds to the capacities of the sacks
        return remainingCapacities.get(0) - totalWeight; // Adjust index if needed
    }
    
    
   


	
}

