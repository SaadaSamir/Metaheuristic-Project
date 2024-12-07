package projet_metaheuristique_P1;

import java.util.Comparator;

import projet_metaheuristique_P1.AStarAlgo.Item;

public class DensityComparator implements Comparator<Item> {

    @Override
    public int compare(Item item1, Item item2) {
        // Calculate density for each item (value divided by weight)
        double density1 = (double) item1.getValue() / item1.getWeight();
        double density2 = (double) item2.getValue() / item2.getWeight();

        // Compare densities and return the result
        if (density1 < density2) {
            return 1; // item1 comes after item2
        } else if (density1 > density2) {
            return -1; // item1 comes before item2
        } else {
            return 0; // densities are equal
        }
    }
}
