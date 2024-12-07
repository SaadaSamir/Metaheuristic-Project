package projet_metaheuristique_P1;

import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;



import java.awt.*;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.io.BufferedReader;




public class KnapsackInterface extends JFrame {
    private static final long serialVersionUID = 1L;
    private List<List<Integer>> allWeightsFromFile;
    private List<List<Integer>> allValuesFromFile;
    private JComboBox<String> algorithmComboBox;
    private JTextField numberOfSacksField;
    
    private JTextArea metricsArea;
    private JTextArea resultsArea;
    
    private JTextField maxDepthField; // Added maxDepthField
    
    
    
    
    
    
    public KnapsackInterface() {
    	allWeightsFromFile = new ArrayList<>();
        allValuesFromFile = new ArrayList<>();
        
    	
        setTitle("Knapsack Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Panel
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Upper Left Panel
        JPanel upperLeftPanel = new JPanel();
        upperLeftPanel.setLayout(new BoxLayout(upperLeftPanel, BoxLayout.Y_AXIS)); // Use BoxLayout with vertical orientation
        upperLeftPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));

        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmComboBox = new JComboBox<>(new String[]{"DFS", "BFS", "A*", "Genetic", "BSO"});
        JLabel maxDepthLabel = new JLabel("Max Depth:");
        maxDepthField = new JTextField();
        JLabel numberOfSacksLabel = new JLabel("Number of Sacks:");
        numberOfSacksField = new JTextField();
        
        
        //JLabel capacityLabel = new JLabel("Capacity:");
        
        

        upperLeftPanel.add(algorithmLabel);
        upperLeftPanel.add(algorithmComboBox);
        upperLeftPanel.add(maxDepthLabel);
        upperLeftPanel.add(maxDepthField);
        upperLeftPanel.add(numberOfSacksLabel);
        upperLeftPanel.add(numberOfSacksField);
        //upperLeftPanel.add(capacityLabel);
        
        
        
        
        
        
        JScrollPane parametersScrollPane = new JScrollPane(upperLeftPanel);
        leftPanel.add(parametersScrollPane, BorderLayout.NORTH);
        
        
        
        // Lower Left Panel
        JPanel lowerLeftPanel = new JPanel(new BorderLayout());
        lowerLeftPanel.setBorder(BorderFactory.createTitledBorder("Metrics"));
        metricsArea = new JTextArea(10, 30); // Set preferred row and column sizes
        metricsArea.setEditable(false);
        lowerLeftPanel.add(new JScrollPane(metricsArea));

        leftPanel.add(lowerLeftPanel, BorderLayout.CENTER);

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        rightPanel.add(new JScrollPane(resultsArea));

        // Add panels to the main frame
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        
        
        // Panel for buttons
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResultsAndMetrics();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right
        JButton openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle opening file explorer here
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("C:\\Users\\Asus\\eclipse-workspace\\projet_metaheuristique_P1"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fileChooser.setFileFilter(filter);
                fileChooser.setMultiSelectionEnabled(true); // Allow multiple file selection
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles(); // Get all selected files
                    
                    // Sort the selected files based on their numerical order
                    Arrays.sort(selectedFiles, new Comparator<File>() {
                        @Override
                        public int compare(File file1, File file2) {
                            int number1 = extractNumber(file1.getName());
                            int number2 = extractNumber(file2.getName());
                            return Integer.compare(number1, number2);
                        }

                        private int extractNumber(String name) {
                        	String[] parts = name.substring(0, name.lastIndexOf('.')).split("_");
                            return Integer.parseInt(parts[1]);
                            
                        }
                    });

                    // Load and validate each selected CSV file in the order they were selected
                    for (File selectedFile : selectedFiles) {
                        boolean valid = loadAndValidateCSV(selectedFile);
                        if (valid) {
                            // If CSV file is valid, display success message or perform any other action
                            JOptionPane.showMessageDialog(null, "CSV file " + selectedFile.getName() + " loaded successfully!");
                        } else {
                            // If CSV file is invalid, display error message or perform any other action
                            JOptionPane.showMessageDialog(null, "Invalid CSV file format: " + selectedFile.getName());
                        }
                    }
                }
            }

        });
        
        JButton generateCSVButton = new JButton("Generate CSV");
        generateCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openGenerateCSVWindow();
            }
        });
        buttonPanel.add(openButton);
        buttonPanel.add(generateCSVButton);
        buttonPanel.add(clearButton);
        
        // Run Algorithm Button (unchanged)
        JButton runButton = new JButton("Run Algorithm");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runAlgorithm();
            }
        });
        buttonPanel.add(runButton); // Add the runButton to the button panel
        JButton generateGraphButton = new JButton("Generate Graph");
        generateGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle generating graph here
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("C:\\Users\\Asus\\eclipse-workspace\\projet_metaheuristique_P1"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("DOT Files", "dot");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String dotFilePath = selectedFile.getAbsolutePath();
                    String pngFile = "search_tree.pdf";
                    GraphvizExecutor.generateGraph(dotFilePath);
                    GraphvizExecutor.openFile(pngFile);
                }
            }
        });
        buttonPanel.add(generateGraphButton, 0); // Add the generateGraphButton to the leftmost position in the button panel
        JButton generateCurveButton = new JButton("Generate Curve");
        generateCurveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("C:\\Users\\Asus\\eclipse-workspace\\projet_metaheuristique_P1"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fileChooser.setFileFilter(filter);
                fileChooser.setMultiSelectionEnabled(true); // Allow multiple file selection
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    if (selectedFiles.length != 2) {
                        JOptionPane.showMessageDialog(null, "Please select exactly two CSV files.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String csvFilePath1 = selectedFiles[0].getAbsolutePath();
                    String csvFilePath2 = selectedFiles[1].getAbsolutePath();
                    StatCurve.generateCurve(csvFilePath1, csvFilePath2);
                    JOptionPane.showMessageDialog(null, "Curve generated successfully!");
                }
            }
        });
        buttonPanel.add(generateCurveButton, 0); // Add the generateCurveButton to the leftmost position in the button panel


        // Add button panel to the main frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Set size and visibility
        setSize(800, 600);
        setVisible(true);
    }
    
    private void clearResultsAndMetrics() {
        resultsArea.setText("");
        metricsArea.setText("");
    }
    
    private void openGenerateCSVWindow() {
        GenerateCSVDialog dialog = new GenerateCSVDialog(this);
        dialog.setVisible(true);
        int numberOfFiles = dialog.getNumberOfFiles();
        int numberOfItems = dialog.getNumberOfItems();
        int Incrementation = dialog.getIncrement();
        int maxWeight = dialog.getMaxWeight();
        int minWeight = dialog.getMinWeight();
        int maxValue = dialog.getMaxValue();
        int minValue = dialog.getMinValue();
        if (numberOfItems != -1 && maxWeight != -1 && maxValue != -1 && minValue != -1 && numberOfFiles != -1 && Incrementation != -1) {
            CSVGenerator.generateCSV("sample",numberOfItems, maxWeight, minWeight,maxValue,minValue, numberOfFiles, Incrementation);
        }
    }


    private boolean loadAndValidateCSV(File file) {
        List<Integer> weightsFromFile = new ArrayList<>();
        List<Integer> valuesFromFile = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    return false; // Invalid format
                }
                try {
                    int weight = Integer.parseInt(parts[0]);
                    int value = Integer.parseInt(parts[1]);
                    weightsFromFile.add(weight);
                    valuesFromFile.add(value);
                } catch (NumberFormatException e) {
                    return false; // Unable to parse weight or value
                }
            }
            // Add loaded data to the lists
            allWeightsFromFile.add(weightsFromFile);
            allValuesFromFile.add(valuesFromFile);
            return true; // CSV file is valid
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Error reading file
        }
    }

    private void runAlgorithm() {
    	if (allWeightsFromFile == null || allWeightsFromFile.isEmpty() || allValuesFromFile == null || allValuesFromFile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose a CSV file before running the algorithm.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    	Font font = new Font("Arial", Font.BOLD, 16); // Example: Arial, bold, size 16
    	resultsArea.setFont(font);

    	// Change text color
    	resultsArea.setForeground(Color.RED);
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        int maxDepth = 1000;
        String maxDepthText = maxDepthField.getText();
        if (!maxDepthText.isEmpty()) {
            try {
                maxDepth = Integer.parseInt(maxDepthText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Max depth must be a valid integer.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        
        
        
        String numberOfSacksText = numberOfSacksField.getText();
        if (numberOfSacksText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the number of sacks.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int numberOfSacks; 
        try {
            numberOfSacks = Integer.parseInt(numberOfSacksText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Number of sacks must be a valid integer.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (numberOfSacks <= 0) {
            JOptionPane.showMessageDialog(this, "Number of sacks must be a positive integer.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
      
        // Retrieve capacities
        int iter, flip, bee, pop, maxiter;
        iter = 50; flip=3; bee= 5; pop=50;maxiter = 20; 
        double mutRate;
        mutRate = 0.4;
        List<Integer> capacities = new ArrayList<>();
        if(selectedAlgorithm == "BSO") {
        	List<Integer> param = updateCapacityFieldsBSO();
        	
        	for(int h=0; h<numberOfSacks; h++) {
        		capacities.add(param.get(h));
        	}
        	int h = numberOfSacks;
        	iter = param.get(h);
        	h++;
        	flip = param.get(h);
        	h++;
        	bee = param.get(h);
        	if (capacities == null || capacities.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter capacities for all sacks.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        	
        }else if (selectedAlgorithm.equals("Genetic")) {
            List<Hybrid> param = updateCapacityFieldsGA();
            
             // Assuming the last 3 elements are for population size, iterations, and mutation rate
            for (int l = 0; l < numberOfSacks; l++) {
                int capa = (int) param.get(l).getValue(0); // Cast the value to int
                capacities.add(capa);
            }
            int l = numberOfSacks;
            pop = (int) param.get(l).getValue(0); // Cast the value to int
            l++;
            maxiter = (int) param.get(l).getValue(0); // Cast the value to int
            l++;
            mutRate = (double) param.get(l).getValue(0); // Cast the value to double
            if (capacities == null || capacities.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter capacities for all sacks.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }else if( selectedAlgorithm=="DFS"||selectedAlgorithm=="BFS"||selectedAlgorithm=="A*"){
        
        	capacities = updateCapacityFields();
        if (capacities == null || capacities.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter capacities for all sacks.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        }
        

        // Call the appropriate algorithm method
        
        switch (selectedAlgorithm) {
        
            case "DFS":
            	 
            	DataSaved data = new DataSaved();
            	Graph graph1 = new SingleGraph("Search Tree");
            	
            	resultsArea.append("DFS Algorithm Results(All Nodes):\n\n");
            	metricsArea.append("***************************************************** "+"\n");
            	for(int k=0; k < capacities.size(); k++) {
            		metricsArea.append("sacks N°: "+ (k+1) +  " capacity: "+ capacities.get(k) + "\n");
            	}
            	
			// Call the dfs algorithm method
            	for (int fileIndex = 0; fileIndex < allWeightsFromFile.size(); fileIndex++) {
            	    List<Integer> weightsFromFile = allWeightsFromFile.get(fileIndex);
            	    
            	    List<Integer> valuesFromFile = allValuesFromFile.get(fileIndex);
            	    
            	    List<MultipleKnapsack.Item> items = new ArrayList<>();

            	    // Iterate over the data of the current file and add items
            	    for (int i = 0; i < weightsFromFile.size(); i++) {
            	        items.add(new MultipleKnapsack.Item(weightsFromFile.get(i), valuesFromFile.get(i)));
            	    }
            	    ItemsDisplay.displayItems(items, metricsArea, fileIndex);
            	   data = MultipleKnapsack.dfs(capacities, items, resultsArea, metricsArea, maxDepth, graph1);
            	   //if(fileIndex !=0) {
            	   DataSaved.saveDataToCSV(data, "DFSData.csv");
            	   //}
            	   
            	}
            	data.saveToCSV("DFSData.csv", "DFSMetrics.csv");
            	//StatCurve.generateCurve("DFSData.csv", "DFSMetrics.csv");
            	
            	
            	
                
               
             // Convert to seconds
                
                //graph1.setAutoCreate(true);
                //displayGraph(graph1);
            	allWeightsFromFile.clear();
            	allValuesFromFile.clear();
                
                break;
            case "BFS":
            	DataSaved dataDFS = new DataSaved();
            	Graph graph2 = new SingleGraph("Search Tree");
            	// Call the dfs algorithm method
            	resultsArea.append("BFS Algorithm Results(All Nodes):\n\n");
            	metricsArea.append("***************************************************** "+"\n");
            	metricsArea.append("sacks capacities: \n");
            	for(int k=0; k < capacities.size(); k++) {
            		metricsArea.append("sacks N°: "+ (k+1) +  " capacity: "+ capacities.get(k) + "\n");
            	}
            	
            	for (int fileIndex = 0; fileIndex < allWeightsFromFile.size(); fileIndex++) {
            	    List<Integer> weightsFromFile = allWeightsFromFile.get(fileIndex);
            	    
            	    List<Integer> valuesFromFile = allValuesFromFile.get(fileIndex);
            	    
            	    List<MultipleKnapsackBFS.Item> itemsBFS = new ArrayList<>();

            	    // Iterate over the data of the current file and add items
            	    for (int i = 0; i < weightsFromFile.size(); i++) {
            	        itemsBFS.add(new MultipleKnapsackBFS.Item(weightsFromFile.get(i), valuesFromFile.get(i)));
            	    }
            	    ItemsDisplay.displayItemsBFS(itemsBFS, metricsArea, fileIndex);
            	   dataDFS = MultipleKnapsackBFS.bfs(capacities, itemsBFS, resultsArea, metricsArea, maxDepth, graph2);
             	   DataSaved.saveDataToCSV(dataDFS, "BFSData.csv");
             	   dataDFS.saveToCSV("BFSData.csv", "BFSmetrics.csv");
             	}
            	
            	
            	                

             // Convert to seconds
                
                graph2.setAutoCreate(true);
                //displayGraph(graph2);
                allWeightsFromFile.clear();
                allValuesFromFile.clear();
                
                break;
            case "A*":
                // Call A* algorithm method
            	DataSaved dataASTAR = new DataSaved();
            	Graph graph3 = new SingleGraph("Search Tree");
            	resultsArea.append("ASTAR Algorithm Results(All Nodes):\n\n");
            	metricsArea.append("***************************************************** "+"\n");
            	for(int k=0; k < capacities.size(); k++) {
            		metricsArea.append("sacks N°: "+ (k+1) +  " capacity: "+ capacities.get(k) + "\n");
            	}
            	for (int fileIndex = 0; fileIndex < allWeightsFromFile.size(); fileIndex++) {
            	    List<Integer> weightsFromFile = allWeightsFromFile.get(fileIndex);
            	    
            	    List<Integer> valuesFromFile = allValuesFromFile.get(fileIndex);
            	    
            	    List<AStarAlgo.Item> itemsASTAR = new ArrayList<>();

            	    // Iterate over the data of the current file and add items
            	    for (int i = 0; i < weightsFromFile.size(); i++) {
            	    	itemsASTAR.add(new AStarAlgo.Item(weightsFromFile.get(i), valuesFromFile.get(i)));
            	    }
            	   ItemsDisplay.displayItemsASTAR(itemsASTAR, metricsArea , fileIndex);
            	   dataASTAR = AStarAlgo.aStar(capacities, itemsASTAR, resultsArea, metricsArea, maxDepth, graph3);
             	   DataSaved.saveDataToCSV(dataASTAR, "ASTARData.csv");
             	  dataASTAR.saveToCSV("ASTARData.csv", "ASTARmetrics.csv");
             	}                
                graph3.setAutoCreate(true);
                //displayGraph(graph3);
                allWeightsFromFile.clear();
                allValuesFromFile.clear();
                break;
            case "Genetic":
            	System.out.println("****Genetic algorithm testing****");
            	
            	
            	
            	for (int fileIndex = 0; fileIndex < allWeightsFromFile.size(); fileIndex++) {
            	    List<Integer> weightsFromFile = allWeightsFromFile.get(fileIndex);
            	    
            	    List<Integer> valuesFromFile = allValuesFromFile.get(fileIndex);
            	    
            	    List<BasePopulation.Item> itemsGA = new ArrayList<>();

            	    // Iterate over the data of the current file and add items
            	    for (int i = 0; i < weightsFromFile.size(); i++) {
            	        itemsGA.add(new BasePopulation.Item(weightsFromFile.get(i), valuesFromFile.get(i)));
            	    }
            	   
            	   DataSavedMeta dataGA = GeneticAlgo.activateGeneticAlgorithm(capacities, itemsGA, pop, maxiter, mutRate, resultsArea, metricsArea);
            	   DataSavedMeta.saveDataToCSV(dataGA, "GAData.csv");
             	}
            	
            	break;
            case "BSO":
            	
            	for (int fileIndex = 0; fileIndex < allWeightsFromFile.size(); fileIndex++) {
            	    List<Integer> weightsFromFile = allWeightsFromFile.get(fileIndex);
            	    
            	    List<Integer> valuesFromFile = allValuesFromFile.get(fileIndex);
            	    
            	    List<BeeSwarmOptimizationMKP2.Item> itemsBSO = new ArrayList<>();

            	    // Iterate over the data of the current file and add items
            	    for (int i = 0; i < weightsFromFile.size(); i++) {
            	    	itemsBSO.add(new BeeSwarmOptimizationMKP2.Item(weightsFromFile.get(i), valuesFromFile.get(i)));
            	    }
            	   
            	    DataSavedMeta dataBSO = BeeSwarmOptimizationMKP2.launchBSO(capacities, itemsBSO, iter, flip, bee, resultsArea, metricsArea);
            	    DataSavedMeta.saveDataToCSV(dataBSO, "BSOData.csv");
             	}
            	
                
        }

        // Update metrics (if any)
        // metricsArea.setText("Metrics: ...");
    }
   

    public class Hybrid {
        private List<Object> values;

        public Hybrid() {
            this.values = new ArrayList<>();
        }

        public void addValue(Object value) {
            values.add(value);
        }

        public Object getValue(int index) {
            return values.get(index);
        }
    }


    private List<Integer> updateCapacityFields() {
        // Update the number of capacity fields based on the selected number of sacks
        int numberOfSacks = Integer.parseInt(numberOfSacksField.getText());
        
       
        	
        	
       
        CapacityInputDialog dialog = new CapacityInputDialog(this, numberOfSacks);
        dialog.setVisible(true); // Display the dialog window
        List<Integer> capacities = dialog.getCapacities();
        return capacities;
        }
        
        private List<Integer> updateCapacityFieldsBSO(){
        	int numberOfSacks = Integer.parseInt(numberOfSacksField.getText());
        	CapacityInputDialogBSO dialogBSO = new CapacityInputDialogBSO(this, numberOfSacks);
            dialogBSO.setVisible(true); // Display the dialog window
            List<Integer> paramBSO = dialogBSO.getCapacities();
            paramBSO.add(dialogBSO.getMaxIteration());
            paramBSO.add(dialogBSO.getNumberOfBees());
            paramBSO.add(dialogBSO.getFlip());
            return paramBSO;
        
        }
        private List<Hybrid> updateCapacityFieldsGA(){
        	int numberOfSacks = Integer.parseInt(numberOfSacksField.getText());
        	// Use the capacities obtained from the dialog
            CapacityInputDialogGA dialogGA = new CapacityInputDialogGA(this, numberOfSacks);
            dialogGA.setVisible(true); // Display the dialog window
            List<Hybrid> paramGA = new ArrayList<>();
            for (int capacity : dialogGA.getCapacities()) {
                Hybrid hybrid = new Hybrid();
                hybrid.addValue(capacity);
                paramGA.add(hybrid);
            }

            // Add population size
            Hybrid hybridPopSize = new Hybrid();
            hybridPopSize.addValue(dialogGA.getPopulationSize());
            paramGA.add(hybridPopSize);

            // Add iterations
            Hybrid hybridIterations = new Hybrid();
            hybridIterations.addValue(dialogGA.getIterations());
            paramGA.add(hybridIterations);

            // Add mutation rate
            Hybrid hybridMutationRate = new Hybrid();
            hybridMutationRate.addValue(dialogGA.getMutationRate());
            paramGA.add(hybridMutationRate);

            return paramGA;
        }
        
        
    
    private static void displayGraph(Graph graph) {
        // Set the layout algorithm to a tree layout algorithm
        graph.setAttribute("layout.algorithm", "tree");

        // Create a JFrame for the graph viewer
        JFrame frame = new JFrame("Search Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Create a Viewer for the graph
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        View view = viewer.addDefaultView(false);
        
        // Add the view to the frame
        frame.getContentPane().add((Component) view);

        // Enable auto layout
        viewer.enableAutoLayout();

        // Make the frame visible
        frame.setVisible(true);
    }
    

    

        // Update the graph layout
       




    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KnapsackInterface();
            }
        });
    }
}
