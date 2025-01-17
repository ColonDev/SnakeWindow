package Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import java.util.Properties;

public class ConfiguratorUI extends JFrame {
    private final Configurator config;
    private final JPanel mainPanel;

    public ConfiguratorUI(Configurator config) {
        this.config = config;
        mainPanel = new JPanel(new GridBagLayout());
        JButton saveButton = new JButton("Save");

        setTitle("Configurator Properties Editor");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProperties();
            }
        });
    }

    private void initUI() {
        Properties properties = config.getAllProperties();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;
        for (Entry<Object, Object> entry : properties.entrySet()) {
            JLabel keyLabel = new JLabel(entry.getKey().toString());
            keyLabel.setToolTipText("Property: " + entry.getKey().toString());
            JTextField valueField = new JTextField(entry.getValue().toString());
            valueField.setPreferredSize(new Dimension(200, 24));

            gbc.gridx = 0;
            gbc.gridy = row;
            mainPanel.add(keyLabel, gbc);

            gbc.gridx = 1;
            mainPanel.add(valueField, gbc);

            row++;
        }
    }

    private void saveProperties() {
        Component[] components = mainPanel.getComponents();
        for (int i = 0; i < components.length; i += 2) {
            JLabel keyLabel = (JLabel) components[i];
            JTextField valueField = (JTextField) components[i + 1];
            config.setProperty(keyLabel.getText(), valueField.getText());
        }
        config.saveProperties();
        JOptionPane.showMessageDialog(this, "Properties saved successfully!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Configurator config = new Configurator("src/config.properties");
            ConfiguratorUI configUI = new ConfiguratorUI(config);
            configUI.setVisible(true);
        });
    }
}