package projet_metaheuristique_P1;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import projet_metaheuristique_P1.AStarAlgo.Item;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JTextArea;



import java.util.PriorityQueue;

public class AStarAlgo {
	
	

	 public static class Item {
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

	    @Override
	    public String toString() {
	        return "(" + id + ", " + weight + ", " + value + ")";
	    }
	    public static void resetItemCount() {
	        itemCount = 0;
	    }
	    public int getValue() {
	    	return this.value;
	    }
	    public int getWeight() {
	    	return this.weight;
	    }
	}

	 public static class State {
		static int stateCount = 0;
        List<List<Item>> sacks;
        int id;
        List<Item> allitems;
        int totalWeight;
        double ech;
        int itemIndex;
        int nodeNumber;
        int addedItem = -1;
        int sackFilled;
        boolean visited;
        Duration visitDuration; // Add timestamp field
        List<State> children; // Add a list to hold child states

        public State(List<List<Item>> sacks, int totalWeight, int itemIndex, List<Item> allitems) {
            this.sacks = sacks;
            this.id = stateCount++;
            this.totalWeight = totalWeight;
            this.allitems = allitems;
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
        public void seth(double eich) {
            this.ech = eich;
        }
        public void setSackF(int a) {
            this.sackFilled = a;
        }
        public int getSackF() {
            return this.sackFilled;
        }
        public int getAddedItem() {
            return this.addedItem;
        }
        public void setAddedItem(int i) {
            this.addedItem= i;
        }
        public double geth() {
            return this.ech;
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
        public void setVisitDuration(Duration visitDuration) {
            this.visitDuration = visitDuration;
        }
        public List<List<Item>> getSacks(){
        	return this.sacks;
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

		public List<Integer> getRemainingCapacities() {
			// TODO Auto-generated method stub
			return null;
		}
		public List<Integer> getRemainingCapacities(List<Integer> capacities) {
	        List<Integer> remainingCapacities = new ArrayList<>();
	        for (int i = 0; i < sacks.size(); i++) {
	            int remainingCapacity = capacities.get(i) - calculateSackWeight(sacks.get(i));
	            remainingCapacities.add(remainingCapacity);
	        }
	        return remainingCapacities;
	    }
		public List<Item> getRemainingItems(List<Item> it) {
			List<List<Item>> sackContents = this.getSacks();
	        List<Item> stateItems = new ArrayList<>();
	        for(List<Item> sack: sackContents) {
	        	for(Item item: sack) {
	        		stateItems.add(item);
	        	}
	        }
	        List<Item> remainingItems = new ArrayList<>(allitems);
	        remainingItems.removeAll(stateItems);
	        return remainingItems;
	    }
		

	    // Helper method to calculate the weight of items in a sack
	    private int calculateSackWeight(List<Item> sack) {
	        int totalWeight = 0;
	        for (Item item : sack) {
	            totalWeight += item.getWeight();
	        }
	        return totalWeight;
	    }
    }

    public static DataSaved aStar(List<Integer> capacities, List<Item> items, JTextArea resultsArea, JTextArea metricsArea, int maximumDepth, Graph graph) {
    	int numSacks = capacities.size();
        int numItems = items.size();
        DataSaved data = new DataSaved();
        boolean insufficientCapacity = false;
        boolean maximumDepthReached = false;
        boolean targetValueReached = false;
        int totalWeightOfItems = calculateTotalWeightOfItems(items);
        int totalCapacityOfSacks = calculateTotalCapacityOfSacks(capacities);
        insufficientCapacity = totalWeightOfItems > totalCapacityOfSacks;
        
        
        
        int targetVal = calculateMaxVal(items);
        
        if (insufficientCapacity) {
            resultsArea.append("Total capacity of the sacks is insufficient to carry all the items.\n");
        }
        

        List<List<Item>> initialSacks = new ArrayList<>();
        for (int i = 0; i < numSacks; i++) {
            initialSacks.add(new ArrayList<>());
        }
        State initialState = new State(initialSacks, 0, 0, items);
        
        
        
      

        PriorityQueue<State> priorityQueue = new PriorityQueue<>(
        	    (node1, node2) -> {
        	        double f1 = AStarHeuristic.g(node1, items) + AStarHeuristic.h(node1,  capacities, items);
        	        double f2 = AStarHeuristic.g(node2, items) + AStarHeuristic.h(node2, capacities, items);
        	        return Double.compare(f2, f1); // Compare f1 to f2 to order elements in descending order
        	    }
        	);
        priorityQueue.offer(initialState);
        
        double eff = AStarHeuristic.h(initialState, capacities, items) + AStarHeuristic.g(initialState, items);
        
        int j = 0;
        int maxDepth = 0;
        List<State> allSacks = new ArrayList<>();
        initialState.setNodeNumber(0);
        List<State> allStates = new ArrayList<>();
      
       
        long startTime = System.currentTimeMillis(); //measure time begins
        Instant startingTime = Instant.now();
    

        while (!priorityQueue.isEmpty() && j<20000) {
            State state = priorityQueue.poll();
            double Stateff = AStarHeuristic.h(state, capacities, items) + AStarHeuristic.g(state, items);
            state.setNodeNumber(j);
            int nodeJ = j;
            
          
            
            
            Instant stateStartTime = Instant.now();
            
            // Calculate visit duration for state
            Duration visitDuration = Duration.between(startingTime, stateStartTime);
            
            
            state.setVisitDuration(visitDuration); // Set visit duration
            
            
            
            
           
            
            
            System.out.println("f"+ Stateff);
            
            
            
            
            List<List<Item>> sacks = state.sacks;
            int totalWeight = state.totalWeight;
            int itemIndex = state.itemIndex;
            
            
            targetValueReached = targetVal <= calculateCurrentVal(sacks);
            
            
            // Update max depth
            maxDepth = Math.max(maxDepth, itemIndex);
            
            maximumDepthReached = itemIndex > maximumDepth;
            
            
            if(maximumDepthReached) {
            	
            	continue;
            }



            int totalValue = 0;
           
            
            
            if (itemIndex <= items.size()) {
                resultsArea.append("Sacks:\n");
                int sackNumber = 1;
                for (List<Item> sack : sacks) {
                    resultsArea.append("Sack " + sackNumber + " " + sack.toString() + "\n");
                    totalValue += calculateTotalValue(sack);
                    sackNumber++;
                }
                resultsArea.append("Total Value: " + totalValue + "\n\n");
            }
            
            state.setNodeNumber(j);
            state.setVisited(true);
           
            j++;
            
            
            
            if(targetValueReached) {
            	data.setSatisfiable(true);
            	totalValue = 0;
            	resultsArea.append("Sacks When Target Reached:\n");
                int sackNumber = 1;
                for (List<Item> sack : sacks) {
                    resultsArea.append("Sack " + sackNumber + " " + sack.toString() + "\n");
                    totalValue += calculateTotalValue(sack);
                    sackNumber++;
                }
                resultsArea.append("Total Value: " + totalValue + "\n\n");
            	
            	break;
            }
            
            
            

            

                if (itemIndex >= items.size()) {
                    
                    continue;
                }
            

                for (int i = 0; i < numSacks; i++) {
            for(int k = 0; k< items.size(); k++) {
            
            	
            	 List<List<Item>> parentSacks = copySacks(sacks);
                 totalWeight = state.totalWeight;
                 
            	
                if (canFit(capacities.get(i), parentSacks.get(i), items.get(k))  && !state.containsItem(items.get(k).getId())) {
                    List<List<Item>> newSacks = copySacks(sacks);
                    newSacks.get(i).add(items.get(k));
                    totalWeight += items.get(k).weight;
                    State newState = new State(newSacks, totalWeight, k, items);
                    if(!isSimilarState(newState, allSacks)) {
                        state.addChild(newState);
                        double ach = AStarHeuristic.h(state,  capacities, items);
                       
                        newState.seth(ach);
                        newState.setSackF(i);
                        newState.setNodeNumber(nodeJ);                       
                        newState.setAddedItem(k);
                        
                        priorityQueue.offer(newState);
                        }
                   
             
                }
            }
            

           
        }
            /*
            if(itemIndex <= items.size()) {
            	List<List<Item>> parentSacks = copySacks(sacks);
            	int parentWeight = state.totalWeight;
            	State ParentalState = new State(parentSacks, parentWeight, itemIndex + 1);
            	state.addChild(ParentalState);
            	allStates.add(ParentalState);
            	double ach = AStarHeuristic.h(state, items.subList(state.itemIndex, items.size()), capacities);
                System.out.println(ach);
            	ParentalState.setSackF(-1);
            	ParentalState.seth(ach);
            	priorityQueue.offer(ParentalState);
            	
        	}
        	*/
                allSacks.add(state);
                allStates.add(state);
                double ach = AStarHeuristic.h(state, capacities, items);
                
               
               
                
                // Create a new list to store sorted items
                
            
            
            
        }
        allSacks.sort(Comparator.comparingInt((State state) -> state.getTotalValue()).reversed()
                .thenComparingInt((State state) -> state.getNodeNumber()));

        
        Item.resetItemCount();
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        double durationSeconds = durationMillis / 1000.0;
        metricsArea.append("DFS Algorithm Duration: " + durationMillis + " milliseconds (" + durationSeconds + " seconds)\n");

        
     // Build the graph based on the parent-child relationships
        /*
        for (State parentState : allStates) {
            for (State childState : parentState.getChildren()) {    	
                // Add nodes if they don't exist already
                if (graph.getNode("Node_" + parentState.getId()) == null) {
                    Node parentNode = graph.addNode("Node_" + parentState.getId());
                    // Set the label for the parent node with the total value
                    parentNode.setAttribute("label", "h: " + parentState.geth() + "\nTotal Value: " + parentState.getTotalValue());
                }
                if(childState.getVisited()) {
                if (graph.getNode("Node_" + childState.getId()) == null) {
                    Node childNode = graph.addNode("Node_" + childState.getId());
                    // Set the label for the child node with the total value
                    childNode.setAttribute("label", "h: " + childState.geth() + "\nTotal Value: " + childState.getTotalValue());
                }
            }
                if(childState.getVisited()) {
                // Add edges between parent and child states
                graph.addEdge("Edge_" + parentState.getId() + "_" + childState.getId(),
                              "Node_" + parentState.getId(),
                              "Node_" + childState.getId());
                }
            }
        }
        graph.setAttribute("layout.algorithm", "tree");
        
       */
    		resultsArea.append("Best result possible: \n");
    		bestState(allSacks, resultsArea);
    	
        DotFileGeneratorASTAR.generateDotFile(allStates);
        metricsArea.append("The number of nodes in the search tree: " + j + "\n");
        if(maximumDepthReached) {
        metricsArea.append("The depth of the search tree: " + maximumDepth + "\n");
        data.setMaximumDepth(maximumDepth);
        }else {
        	metricsArea.append("The depth of the search tree: " + maxDepth + "\n");
        	data.setMaximumDepth(maxDepth);
        }
        
        data.setnumItems(numItems);
        data.setDuration(durationSeconds);
        
        double val = ((double)calculateCurrentVal(allSacks.get(0).sacks))/ targetVal;
       
        data.setSatRate(val);
        data.setNodesTraversed(j);
        data.setNodeSole(allSacks.get(0).getNodeNumber());
        data.setNodeSoleTime(allSacks.get(0).getVisitDuration().toMillis()/1000.0);

        
        return(data);
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

    private static boolean canFit(int capacity, List<Item> sack, Item item) {
        if (sack == null) return true;
        int sackWeight = calculateSackWeight(sack);
        return sackWeight + item.weight <= capacity;
    }

    private static int calculateSackWeight(List<Item> sack) {
        int weight = 0;
        for (Item item : sack) {
            weight += item.weight;
        }
        return weight;
    }

    private static int calculateTotalValue(List<Item> sack) {
        int totalValue = 0;
        for (Item item : sack) {
            totalValue += item.value;
        }
        return totalValue;
    }

    private static int calculateTotalWeightOfItems(List<Item> items) {
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