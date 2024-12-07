package projet_metaheuristique_P1;

import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CSVGenerator {
	
	

    public static void generateCSV(String baseFileName, int itemCount, int maxWeight,int minWeight, int maxValue, int minValue,int nFiles, int inc) {
    	if (itemCount <= 0 || maxWeight <= 0 || minWeight <= 0 || maxValue <= 0 || nFiles <= 0 || inc <= 0) {
            JOptionPane.showMessageDialog(null, "Invalid input arguments. All values must be positive integers.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            StringBuilder successMessage = new StringBuilder("CSV files generated successfully:\n");

            // Iterate over the number of files
            for (int fileNumber = 1; fileNumber <= nFiles; fileNumber++) {
                String fileName = baseFileName + "_" + fileNumber + ".csv"; // Append file number to base filename
                FileWriter writer = new FileWriter(fileName);

                // Write CSV header
                // Optional: Add CSV header if needed

                Random random = new Random();

                // Generate random weight and value for each item and write to CSV
                for (int i = 0; i < itemCount + (inc * (fileNumber - 1)); i++) { // Increment item count based on file number
                	int weightRange = maxWeight - minWeight;
                	
                	int weight = random.nextInt(weightRange + 1) + minWeight;
                	
                	int valueRange = maxValue - minValue;
                	
                
                	// Generate random weight between minweight and maxWeight
                                  
                	int value = random.nextInt(valueRange + 1) + minValue; // Generate random value between 1 and maxValue
                    writer.append(weight + "," + value + "\n");
                }

                writer.flush();
                writer.close();

                successMessage.append(fileName).append("\n");
            }

            // Display success message using a dialog box
            JOptionPane.showMessageDialog(null, successMessage.toString(), "CSV Files Generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            // Display error message using a dialog box
            JOptionPane.showMessageDialog(null, "Error while generating CSV files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 
}
