package projet_metaheuristique_P1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GenerateCSVDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField numberOfFilesField;
	private JTextField numberOfItemsField;
	private JTextField incrementItemField;
    private JTextField maxWeightField;
    private JTextField minWeightField;
    private JTextField maxValueField;
    private JTextField minValueField;
    private JButton generateButton;
    
    private int numberOfFiles;
    private int numberOfItems;
    private int incrementItem;
    private int maxWeight;
    private int minWeight;
    private int maxValue;
    private int minValue;

    public GenerateCSVDialog(JFrame parent) {
        super(parent, "Generate CSV", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(8, 4));
        
        JLabel numberOfFilesLabel = new JLabel("Number of files:");
        numberOfFilesField = new JTextField();
        
        JLabel numberOfItemsLabel = new JLabel("Number of Items:");
        numberOfItemsField = new JTextField();
        
        JLabel incrementItemLabel = new JLabel("Increrement of Items:");
        incrementItemField = new JTextField();
        JLabel maxWeightLabel = new JLabel("Max Weight:");
        maxWeightField = new JTextField();
        JLabel minWeightLabel = new JLabel("Min Weight:");
        minWeightField = new JTextField();
        
        JLabel maxValueLabel = new JLabel("Max Value:");
        maxValueField = new JTextField();
        
        JLabel minValueLabel = new JLabel("Min Value:");
        minValueField = new JTextField();
        
        panel.add(numberOfFilesLabel);
        panel.add(numberOfFilesField);
        panel.add(numberOfItemsLabel);
        panel.add(numberOfItemsField);
        panel.add(incrementItemLabel);
        panel.add(incrementItemField);
        panel.add(maxWeightLabel);
        panel.add(maxWeightField);
        panel.add(minWeightLabel);
        panel.add(minWeightField);
        panel.add(maxValueLabel);
        panel.add(maxValueField);
        panel.add(minValueLabel);
        panel.add(minValueField);

        generateButton = new JButton("Generate");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleGenerateButtonClick();
            }
        });

        panel.add(generateButton);

        getContentPane().add(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void handleGenerateButtonClick() {
        try {
        	numberOfFiles=Integer.parseInt(numberOfFilesField.getText());
            numberOfItems = Integer.parseInt(numberOfItemsField.getText());
            incrementItem=Integer.parseInt(incrementItemField.getText());
            maxWeight = Integer.parseInt(maxWeightField.getText());
            minWeight = Integer.parseInt(minWeightField.getText());
            maxValue = Integer.parseInt(maxValueField.getText());
            minValue = Integer.parseInt(minValueField.getText());
           
         // Check if all input fields are filled with positive integers
            if (numberOfFiles <= 0 || numberOfItems <= 0 || incrementItem <= 0 || maxWeight <= 0 || minWeight <= 0
                    || maxValue <= 0 || minValue <=0) {
                JOptionPane.showMessageDialog(this, "Please enter positive integer values in all fields.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose(); // Close the dialog after retrieving input
            setVisible(false); // Hide the dialog after closing
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integer values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    
    public int getNumberOfFiles() {
        return numberOfFiles;
    }
    public int getNumberOfItems() {
        return numberOfItems;
    }
    public int getMaxValue() {
        return maxValue;
    }
    public int getMinValue() {
        return minValue;
    }

    public int getMaxWeight() {
        return maxWeight;
    }
    public int getMinWeight() {
        return minWeight;
    }

    public int getIncrement() {
        return incrementItem;
    }
 // Override dispose method to handle cancellation
    @Override
    public void dispose() {
        // Check if the dialog should be closed without generating CSV
        // For example, if the user clicked on the close button (X)
        if (shouldCancelOperation()) {
            super.dispose(); // Close the dialog
        }
    }

    // Method to determine if the operation should be canceled
    private boolean shouldCancelOperation() {
        // Check if any of the text fields are not filled
        boolean anyEmpty = numberOfFilesField.getText().isEmpty() || numberOfItemsField.getText().isEmpty()
                || incrementItemField.getText().isEmpty() || maxWeightField.getText().isEmpty()
                || minWeightField.getText().isEmpty() || maxValueField.getText().isEmpty() || minValueField.getText().isEmpty();
        return anyEmpty;
    }
    
}
