package projet_metaheuristique_P1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataSavedMeta {

    private int numItems;
    private double duration;
    private int maximumDepth;
    private boolean satisfiable;
    private double satRate;

    public DataSavedMeta(int numItems, double duration, boolean satisfiable, double satRate) {
        this.numItems = numItems;
        this.duration = duration;
        
        this.satisfiable = satisfiable;
        this.satRate = satRate;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

   
    

    public boolean isSatisfiable() {
        return satisfiable;
    }

    public void setSatisfiable(boolean satisfiable) {
        this.satisfiable = satisfiable;
    }

    public double getSatRate() {
        return satRate;
    }

    public void setSatRate(double satRate) {
        this.satRate = satRate;
    }

    public static void saveDataToCSV(DataSavedMeta data, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            // If the file does not exist, write the CSV header
            if (!new File(fileName).exists()) {
                writer.append("NumItems,Duration,Satisfiable,SatRate\n");
            }

            // Write data to CSV
            writer.append(data.getNumItems() + ",");
            writer.append(data.getDuration() + ",");
            
            writer.append(data.isSatisfiable() + ",");
            writer.append(data.getSatRate() + "\n");

            System.out.println("Data appended to CSV file '" + fileName + "'.");
        } catch (IOException e) {
            System.err.println("Error while saving CSV file: " + e.getMessage());
        }
    }
}
