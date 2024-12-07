package projet_metaheuristique_P1;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class GraphvizExecutor {

    public static String generateGraph(String dotFilePath) {
        // Replace this path with your desired output directory
    	String outputImagePath = "search_tree.pdf";

        try {
            // Build the command
        	ProcessBuilder builder = new ProcessBuilder("dot","-Tpdf",  dotFilePath, "-o", "search_tree.pdf");

            builder.directory(new File(System.getProperty("user.dir"))); // Set working directory to current directory

            // Start the process
            Process process = builder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Graph image generated successfully.");
                return outputImagePath;
            } else {
                System.err.println("Error: Graphviz command failed with exit code " + exitCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing Graphviz command: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    static void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                System.err.println("Error: File not found.");
            }
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
