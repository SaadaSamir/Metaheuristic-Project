package projet_metaheuristique_P1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.ChartUtils;






public class StatCurve {

    public static void generateCurve(String csvFile, String statsFile) {
        String line;
        String csvSplitBy = ","; // CSV delimiter

        // Create series for the chart
        XYSeries series = new XYSeries("Statistical Curve");

        // Read data from the CSV file and populate the series
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                // Assuming the CSV file has two columns: x and y
                double x = Double.parseDouble(data[0]);
                double y = Double.parseDouble(data[1]);
                series.add(x, y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a dataset and add the series to it
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Pour 3 sacs", // Chart title
                "Nombre D'objet", // X-axis label
                "Durée De l'Execution", // Y-axis label
                dataset // Dataset
        );

        // Read average and standard deviation from the stats file
        double average = 0;
        double standardDeviation = 0;
        boolean headerSkipped = false;
        try (BufferedReader br = new BufferedReader(new FileReader(statsFile))) {
            String line1;
            while ((line1 = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header line
                }
                String[] parts = line1.split(csvSplitBy);
                average = Double.parseDouble(parts[0]); // Assuming the average is in the second column
                standardDeviation = Double.parseDouble(parts[1]); // Assuming the standard deviation is in the third column
                break; // Exit the loop after reading the first data line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add annotations for average and standard deviation
        XYTextAnnotation avgAnnotation = new XYTextAnnotation("Average: " + average, series.getMaxX() * 0.50, series.getMaxY() * 0.25);
        avgAnnotation.setFont(new Font("SansSerif", Font.PLAIN, 12));
        avgAnnotation.setTextAnchor(TextAnchor.TOP_RIGHT);
        
        XYTextAnnotation stdDevAnnotation = new XYTextAnnotation("Standard Deviation: " + standardDeviation, series.getMaxX() * 0.57, series.getMaxY() * 0.20);
        stdDevAnnotation.setFont(new Font("SansSerif", Font.PLAIN, 12));
        stdDevAnnotation.setTextAnchor(TextAnchor.TOP_RIGHT);
        
        chart.getXYPlot().addAnnotation(avgAnnotation);
        chart.getXYPlot().addAnnotation(stdDevAnnotation);

        // Display the chart in a frame
     // Create a button to save the chart
        JButton saveButton = new JButton("Save Chart");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChart(chart);
            }
        });

        // Display the chart and the save button in a frame
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Statistical Curve Generator");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ChartPanel chartPanel = new ChartPanel(chart);
            frame.add(chartPanel, BorderLayout.CENTER);
            frame.add(saveButton, BorderLayout.SOUTH);
            frame.pack();
            frame.setVisible(true);
        });
        
    }
 // Method to save the chart as an image
    private static void saveChart(JFreeChart chart) {
        try {
            String fileName = "StatisticalCurve.png";
            ChartUtils.saveChartAsPNG(new File(fileName), chart, 800, 600);
            JOptionPane.showMessageDialog(null, "Chart saved as " + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
