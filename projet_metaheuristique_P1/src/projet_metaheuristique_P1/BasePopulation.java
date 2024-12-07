package projet_metaheuristique_P1;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

import projet_metaheuristique_P1.AStarAlgo.Item;
import projet_metaheuristique_P1.AStarAlgo.State;




public class BasePopulation {
	static class Item {
	    static int itemCount = 0; // Static counter to generate unique identifiers
	    int id;
	    int weight;
	    int value;

	    public Item(int weight, int value) {
	        this.id = itemCount++; // Assign unique identifier and then increment the counter
	        this.weight = weight;
	        this.value = value;
	    }

	    public int getId() {
	        return id;
	    }
	    public int getValue() {
	    	return value;
	    }
	    public int getWeight() {
	    	return weight;
	    }
	    

	    @Override
	    public String toString() {
	        return "(" + id + ", " + weight + ", " + value + ")";
	    }
	    public static void resetItemCount() {
	        itemCount = 0;
	    }
	}

	static class State {
		static int stateCount = 0;
        List<List<Item>> sacks;
        int id;
        List<BasePopulation.Item> items;
        int totalWeight;
        int itemIndex;
        int totalValue;
        int nodeNumber;
        boolean visited;
        Duration visitDuration; // Add timestamp field
        List<State> children; // Add a list to hold child states

        public State(List<List<Item>> sacks, int totalWeight, int itemIndex) {
            this.sacks = sacks;
            this.id = stateCount++;
            this.totalWeight = totalWeight;
            this.itemIndex = itemIndex;
            this.children = new ArrayList<>(); // Initialize the list
            this.visited = false;
            this.visitDuration = null; // Initialize visit duration
        }

        public int getTotalValue() {
            int totalValue = 0;
            for (List<Item> sack : sacks) {
                totalValue += calculateTotalValue(sack);
            }
            return totalValue;
        }
        public int getTotalWeight() {
            int totalWeight = 0;
            for (List<Item> sack : sacks) {
                totalWeight += calculateSackWeight(sack);
            }
            return totalWeight;
        }

        // Method to add child states
        public void addChild(State child) {
            children.add(child);
        }

        // Method to retrieve child states
        public List<State> getChildren() {
            return children;
        }
        public int getId() {
            return id;
        }
        public int getNodeNumber() {
            return nodeNumber;
        }
        public void setNodeNumber(int n) {
            this.nodeNumber= n;
        }
        public void setVisited(boolean bol) {
        	this.visited = bol;
        }
        public boolean getVisited() {
        	return visited;
        }
        public void setTotalWeight(int weight) {
        	this.totalWeight = weight;
        }
        public void setVisitDuration(Duration visitDuration) {
            this.visitDuration = visitDuration;
        }
        public void setTotalValue(int val) {
        	this.totalValue = val;
        }
        public double getFitness() {
        	return totalValue;
        }
     // Getter for items
        public List<BasePopulation.Item> getItems() {
            return items;
        }

        // Setter for items
        public void setItems(List<BasePopulation.Item> items) {
            this.items = items;
        }

        // Method to get visit duration
        public Duration getVisitDuration() {
            return visitDuration;
        }
        public boolean containsItem(int itemId) {
            for (List<Item> sack : sacks) {
                for (Item item : sack) {
                    if (item.getId() == itemId) {
                        return true;
                    }
                }
            }
            return false;
        }
        private static int calculateTotalValue(List<Item> sack) {
            int totalValue = 0;
            for (Item item : sack) {
                totalValue += item.value;
            }
            return totalValue;
        }
        public String getItemsAsString() {
            StringBuilder sb = new StringBuilder();
            for (List<Item> sack : sacks) {
                sb.append("[");
                for (Item item : sack) {
                    sb.append(item.toString()).append(", ");
                }
                sb.append("], ");
            }
            return sb.toString();
        }
        private static void bestState(List<State> allSacks, JTextArea resultsArea) {
            if (!allSacks.isEmpty()) {
                State firstState = allSacks.get(0);
                System.out.println("TWO " + firstState.getVisitDuration());
                resultsArea.append("Best:\n");
                int totalValueFirstState = 0;
                Duration visitDuration = firstState.getVisitDuration();

             // Convert duration to string and remove the "PT" prefix
                	String dur = visitDuration.toString().substring(2);
                for (int i = 0; i < firstState.sacks.size(); i++) {
                    List<Item> sack = firstState.sacks.get(i);
                    resultsArea.append("Sack " + (i + 1) + ": " + sack.toString() + "\n");
                    totalValueFirstState += calculateTotalValue(sack);
                }
                resultsArea.append("Total value of the first state: " + totalValueFirstState + " Duration to reach this state: "+ dur +"\n");
            } else {
                resultsArea.append("The allSacks list is empty.\n");
            }
        }
        

        

        public static int calculateSackWeight(List<Item> sack) {
            int weight = 0;
            for (Item item : sack) {
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
       

        public static int calculateTotalWeightOfItems(List<Item> items) {
            int totalWeight = 0;
            for (Item item : items) {
                totalWeight += item.weight;
            }
            return totalWeight;
        }

        private static List<List<Item>> copySacks(List<List<Item>> sacks) {
            List<List<Item>> copy = new ArrayList<>();
            for (List<Item> sack : sacks) {
                copy.add(new ArrayList<>(sack));
            }
            return copy;
        }
        

        // Method to remove child states
        public void removeChild(State child) {
            children.remove(child);
            // Update total value and total weight when a child state is removed
            updateTotalValue();
            updateTotalWeight();
        }

        // Method to update the total value of the state
        public void updateTotalValue() {
            totalValue = calculateTotalValue();
        }

        // Method to update the total weight of the state
        public void updateTotalWeight() {
            totalWeight = calculateTotalWeight();
        }

        // Method to calculate the total value of the state
        private int calculateTotalValue() {
            int totalValue = 0;
            for (List<Item> sack : sacks) {
                totalValue += calculateTotalValue(sack);
            }
            return totalValue;
        }

        // Method to calculate the total weight of the state
        private int calculateTotalWeight() {
            int totalWeight = 0;
            for (List<Item> sack : sacks) {
                totalWeight += calculateSackWeight(sack);
            }
            return totalWeight;
        }

        private static int calculateTotalCapacityOfSacks(List<Integer> capacities) {
            int totalCapacity = 0;
            for (int capacity : capacities) {
                totalCapacity += capacity;
            }
            System.out.println(totalCapacity);
            return totalCapacity;
        }
    /*
        private static void printSacks(List<List<Item>> sacks, JTextArea resultsArea) {
            resultsArea.append("Sacks: Optimum Result\n");
            int sackNumber = 1;
            int totalValue = 0;
            for (List<Item> sack : sacks) {
                resultsArea.append("Sack " + sackNumber + " " + sack.toString() + "\n");
                totalValue += calculateTotalValue(sack);
                sackNumber++;
            }
            resultsArea.append("Total Value: " + totalValue + "\n\n");
        }
        */
        private static int calculateMaxVal(List<Item> items) {
        	int maxVal = 0;
        	for(Item item: items) {
        		maxVal += item.value;
        	}
        	return maxVal;
        }
        private static int calculateCurrentVal(List<List<Item>> sacks) {
        	int currentValue = 0;
        	for (List<Item> sack : sacks) {
                
        		currentValue += calculateTotalValue(sack);
                
            }
        	return currentValue;
        }

        private static List<Integer> getRemainingCapacities(State state, List<Integer> capacities) {
            List<Integer> remainingCapacities = new ArrayList<>(capacities);
            for (int i = 0; i < state.sacks.size(); i++) {
                int sackWeight = calculateSackWeight(state.sacks.get(i));
                remainingCapacities.set(i, capacities.get(i) - sackWeight);
            }
            return remainingCapacities;
        }
        public static boolean isSimilarState(State childState, List<State> allStates) {
            for (State state : allStates) {
                if (areStatesSimilar(childState, state)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean areStatesSimilar(State state1, State state2) {
            List<List<Item>> sacks1 = state1.sacks;
            List<List<Item>> sacks2 = state2.sacks;

            // Check if the number of sacks is the same
            if (sacks1.size() != sacks2.size()) {
                return false;
            }

            // Check if each sack in state1 has a corresponding similar sack in state2
            for (int i = 0; i < sacks1.size(); i++) {
                List<Item> sack1 = sacks1.get(i);
                List<Item> sack2 = sacks2.get(i);

                // Check if the number of items in the sacks is the same
                if (sack1.size() != sack2.size()) {
                    return false;
                }

                // Check if each item in sack1 has a corresponding similar item in sack2
                for (Item item : sack1) {
                    boolean foundSimilarItem = false;
                    for (Item item2 : sack2) {
                        if (item.getId() == item2.getId()) {
                            foundSimilarItem = true;
                            break;
                        }
                    }
                    if (!foundSimilarItem) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

}

