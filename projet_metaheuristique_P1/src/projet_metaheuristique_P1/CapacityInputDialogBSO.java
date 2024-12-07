package projet_metaheuristique_P1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CapacityInputDialogBSO extends JDialog {
    private List<JTextField> capacityFields;
    private JTextField maxIterationField;
    private JTextField numberOfBeesField;
    private JTextField flipField;

    public CapacityInputDialogBSO(JFrame parent, int numberOfSacks) {
        super(parent, "Enter Capacities and BSO Parameters", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Capacity fields
        capacityFields = new ArrayList<>();
        for (int i = 0; i < numberOfSacks; i++) {
            JLabel label = new JLabel("Capacity for Sack " + (i + 1) + ":");
            JTextField textField = new JTextField(10);
            capacityFields.add(textField);
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(label);
            panel.add(textField);
            mainPanel.add(panel);
        }

        // MaxIteration field
        JPanel maxIterationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel maxIterationLabel = new JLabel("Max Iteration:");
        maxIterationField = new JTextField(10);
        maxIterationPanel.add(maxIterationLabel);
        maxIterationPanel.add(maxIterationField);
        mainPanel.add(maxIterationPanel);

        // Number of Bees field
        JPanel numberOfBeesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel numberOfBeesLabel = new JLabel("Number of Bees:");
        numberOfBeesField = new JTextField(10);
        numberOfBeesPanel.add(numberOfBeesLabel);
        numberOfBeesPanel.add(numberOfBeesField);
        mainPanel.add(numberOfBeesPanel);

        // Flip field
        JPanel flipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel flipLabel = new JLabel("Flip:");
        flipField = new JTextField(10);
        flipPanel.add(flipLabel);
        flipPanel.add(flipField);
        mainPanel.add(flipPanel);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    public List<Integer> getCapacities() {
        List<Integer> capacities = new ArrayList<>();
        for (JTextField textField : capacityFields) {
            String text = textField.getText();
            if (!text.isEmpty()) {
                try {
                    capacities.add(Integer.parseInt(text));
                } catch (NumberFormatException ignored) {
                    // Ignore invalid inputs
                }
            }
        }
        return capacities;
    }

    public int getMaxIteration() {
        String text = maxIterationField.getText();
        if (!text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                // Ignore invalid inputs
            }
        }
        return 0; // Return 0 if input is invalid
    }

    public int getNumberOfBees() {
        String text = numberOfBeesField.getText();
        if (!text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                // Ignore invalid inputs
            }
        }
        return 0; // Return 0 if input is invalid
    }

    public int getFlip() {
        String text = flipField.getText();
        if (!text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                // Ignore invalid inputs
            }
        }
        return 0; // Return 0 if input is invalid
    }
}
