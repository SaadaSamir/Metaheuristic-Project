package projet_metaheuristique_P1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CapacityInputDialogGA extends JDialog {
    private List<JTextField> capacityFields;
    private JTextField populationSizeField;
    private JTextField iterationsField;
    private JTextField mutationRateField;

    public CapacityInputDialogGA(JFrame parent, int numberOfSacks) {
        super(parent, "Enter Capacities and GA Parameters", true);
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

        // Population Size field
        JPanel populationSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel populationSizeLabel = new JLabel("Population Size:");
        populationSizeField = new JTextField(10);
        populationSizePanel.add(populationSizeLabel);
        populationSizePanel.add(populationSizeField);
        mainPanel.add(populationSizePanel);

        // Iterations field
        JPanel iterationsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel iterationsLabel = new JLabel("Iterations:");
        iterationsField = new JTextField(10);
        iterationsPanel.add(iterationsLabel);
        iterationsPanel.add(iterationsField);
        mainPanel.add(iterationsPanel);

        // Mutation Rate field
        JPanel mutationRatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel mutationRateLabel = new JLabel("Mutation Rate:");
        mutationRateField = new JTextField(10);
        mutationRatePanel.add(mutationRateLabel);
        mutationRatePanel.add(mutationRateField);
        mainPanel.add(mutationRatePanel);

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

    public int getPopulationSize() {
        String text = populationSizeField.getText();
        if (!text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                // Ignore invalid inputs
            }
        }
        return 0; // Return 0 if input is invalid
    }

    public int getIterations() {
        String text = iterationsField.getText();
        if (!text.isEmpty()) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                // Ignore invalid inputs
            }
        }
        return 0; // Return 0 if input is invalid
    }

    public double getMutationRate() {
        String text = mutationRateField.getText();
        if (!text.isEmpty()) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                // Ignore invalid inputs
            }
        }
        return 0.0; // Return 0.0 if input is invalid
    }
}
