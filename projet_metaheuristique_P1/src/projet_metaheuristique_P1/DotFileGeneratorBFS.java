package projet_metaheuristique_P1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import projet_metaheuristique_P1.MultipleKnapsackBFS.State;

public class DotFileGeneratorBFS {
    public static void generateDotFile(List<State> allStates) {
        try {
            FileWriter writer = new FileWriter("search_treeBFS.dot");

            writer.write("digraph G {\n");

            for (State state : allStates) {
                int j = state.getNodeNumber(); // Assuming getNodeNumber() returns the node number
                writer.write("  Node_" + state.getId() + " [label=\"" +
                        "Node Number: " + j + "\\n" +
                        "Depth: " + state.itemIndex + "\\n" +
                        "Total Value: " + state.getTotalValue() + "\\n" +
                        "Items: " + state.getItemsAsString() + "\"];\n");

                for (State childState : state.getChildren()) {
                    writer.write("  Node_" + state.getId() + " -> Node_" + childState.getId() + ";\n");
                }
            }

            writer.write("}\n");
            writer.close();

            // Generate high-resolution PNG image (same as previous code)

            System.out.println("DOT file with node numbers generated successfully.");

        } catch (IOException e) {
            System.err.println("Error while generating DOT file: " + e.getMessage());
        }
    }
}
