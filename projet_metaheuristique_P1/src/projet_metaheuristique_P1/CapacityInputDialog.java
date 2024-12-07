package projet_metaheuristique_P1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CapacityInputDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<JTextField> capacityFields;

    public CapacityInputDialog(JFrame parent, int numberOfSacks) {
        super(parent, "Enter Capacities", true); // true sets the dialog as modal
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Use BoxLayout with vertical orientation

        capacityFields = new ArrayList<>();
        for (int i = 0; i < numberOfSacks; i++) {
            JLabel label = new JLabel("Capacity for Sack " + (i + 1) + ":");
            JTextField textField = new JTextField(10); // Set preferred size
            capacityFields.add(textField);
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(label);
            panel.add(textField);
            mainPanel.add(panel);
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose()); // Close the dialog when OK is clicked

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack(); // Resize the dialog to fit its components
        setLocationRelativeTo(parent); // Center the dialog on the parent frame
    }
    public List<Integer> getCapacities() {
        List<Integer> capacities = new ArrayList<>();
        boolean validInput = true;
        for (JTextField textField : capacityFields) {
            String text = textField.getText();
            if (!text.isEmpty()) {
                try {
                    capacities.add(Integer.parseInt(text));
                } catch (NumberFormatException ignored) {
                    // Ignore invalid inputs
                    validInput = false;
                }
            }
        }
        // Return null if the close button is clicked without validating the input
        if (!validInput) {
            return null;
        }
        return capacities;
    }
}
