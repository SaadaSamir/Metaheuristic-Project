package projet_metaheuristique_P1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataSaved {

    	private int numItems;
        private double duration;
        private int nodesTraversed;
        private int maximumDepth;
        private boolean satisfiable;
        private double satRate;
        private int node;
        private double temps;
        

        public DataSaved(int numItems,double duration, int nodesTraversed, int maximumDepth, boolean satisfiable, double satRate, int node, double temps) {
            this.duration = duration;
            this.nodesTraversed = nodesTraversed;
            this.maximumDepth = maximumDepth;
            this.satisfiable = satisfiable;
            this.satRate = satRate;
            this.node = node;
            this.temps = temps;
        }
        
        

        public void setnumItems(int numItems) {
            this.numItems = numItems;
        }
        
        public void setDuration(double duration) {
            this.duration = duration;
        }

        public void setNodesTraversed(int nodesTraversed) {
            this.nodesTraversed = nodesTraversed;
        }

        public void setMaximumDepth(int maximumDepth) {
            this.maximumDepth = maximumDepth;
        }

        public void setSatisfiable(boolean satisfiable) {
            this.satisfiable = satisfiable;
        }
        public void setSatRate(double satRate) {
            this.satRate = satRate;
        }
        public void setNodeSole(int node) {
            this.node = node;
        }
        public void setNodeSoleTime(double temps) {
            this.temps = temps;
        }
        
        public int getnumItems() {
            return numItems;
        }
        
        public double getDuration() {
            return duration;
        }

        public int getNodesTraversed() {
            return nodesTraversed;
        }

        public int getMaximumDepth() {
            return maximumDepth;
        }
        
        public double getSatRate() {
            return satRate * 100;
        }
        
        public int getNodeSol() {
            return node;
        }
        public double getNodeSolTime() {
            return temps;
        }
        

        public boolean isSatisfiable() {
            return satisfiable;
        }
    
    
    private List<DataSaved> dataEntries;

    public DataSaved() {
        dataEntries = new ArrayList<>();
    }

    // Method to add a new data entry
    public void addDataEntry(DataSaved entry) {
        dataEntries.add(entry);
    }
    public static void saveDataToCSV(DataSaved data, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            // If the file does not exist, write the CSV header
            if (!new File(fileName).exists()) {
                writer.append("Objets,Durée,Noeuds_traversés,Profondeur,Satisfiable,SatRate,nodeSol,nodeSoleTime\n");
            }

            // Write data to CSV
                writer.append(data.getnumItems() + ",");
                writer.append(data.getDuration() + ",");
                writer.append(data.getNodesTraversed() + ",");
                writer.append(data.getMaximumDepth() + ",");
                writer.append(data.isSatisfiable() + ",");
                writer.append(data.getSatRate() + ",");
                writer.append(data.getNodeSol() + ",");
                writer.append(data.getNodeSolTime() + "\n");
            

            System.out.println("Data appended to CSV file '" + fileName + "'.");
        } catch (IOException e) {
            System.err.println("Error while saving CSV file: " + e.getMessage());
        }
    }
 // Method to save data to a CSV file
    public void saveToCSV(String filename1, String filename2) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename2, true))) {
            // Append the average and standard deviation columns to the header
            writer.append("Average Duration,Standard Deviation\n");

            // Calculate average and standard deviation of durations
            double averageDuration = calculateAverageDuration(filename1);
            double standardDeviation = calculateStandardDeviation(filename1);

            
            writer.append(averageDuration + "," + standardDeviation + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to calculate the average duration
    private double calculateAverageDuration(String fileName) {
    	
    	String csvFile = fileName; // Path to your CSV file
        String csvSplitBy = ","; // CSV delimiter
        int durationColumnIndex = 1; // Index of the "duration" column (assuming it's the first column)

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            double sum = 0;
            int count = 0;
            boolean headerSkipped = false;

            // Read each line of the CSV file
            while ((line = br.readLine()) != null) {
                // Skip the first line (header)
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                // Split the line into columns
                String[] columns = line.split(csvSplitBy);
                
                // Check if the line has enough columns and the "duration" column is not empty
                if (columns.length > durationColumnIndex && !columns[durationColumnIndex].isEmpty()) {
                    try {
                        double duration = Double.parseDouble(columns[durationColumnIndex]); // Parse the "duration" value
                        
                        sum += duration;
                        count++;
                       
                    } catch (NumberFormatException e) {
                        // Handle parsing errors if necessary
                        System.err.println("Error parsing duration value: " + e.getMessage());
                    }
                }
            }

            if (count > 0) {
                double average = sum / count;
                return average;
            } else {
                System.out.println("No valid data found in the 'duration' column.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to calculate the standard deviation of durations
    private double calculateStandardDeviation(String fileName) {
        String csvFile = fileName; // Path to your CSV file
        String csvSplitBy = ","; // CSV delimiter
        int durationColumnIndex = 1; // Index of the "duration" column (assuming it's the first column)

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            double sum = 0;
            double sumSquaredDifferences = 0;
            int count = 0;
            boolean headerSkipped = false;

            // Read each line of the CSV file
            while ((line = br.readLine()) != null) {
                // Skip the first line (header)
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                // Split the line into columns
                String[] columns = line.split(csvSplitBy);
                
                // Check if the line has enough columns and the "duration" column is not empty
                if (columns.length > durationColumnIndex && !columns[durationColumnIndex].isEmpty()) {
                    try {
                        double duration = Double.parseDouble(columns[durationColumnIndex]); // Parse the "duration" value
                        sum += duration;
                        sumSquaredDifferences += Math.pow(duration, 2); // Accumulate the sum of squared differences
                        count++;
                    } catch (NumberFormatException e) {
                        // Handle parsing errors if necessary
                        System.err.println("Error parsing duration value: " + e.getMessage());
                    }
                }
            }

            if (count > 0) {
                double mean = sum / count;
                double variance = (sumSquaredDifferences / count) - Math.pow(mean, 2);
                double standardDeviation = Math.sqrt(variance);
                return standardDeviation;
            } else {
                System.out.println("No valid data found in the 'duration' column.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


   
}
