package Model;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class GerenciarPanel extends JPanel {
    private JTable weatherTable;
    private DefaultTableModel tableModel;
    private int userId;  // Assumindo que temos o ID do usuário logado

    public GerenciarPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // Header com botões
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(45, 45, 45));

        JButton btnAdd = new JButton("Adicionar");
        JButton btnUpdate = new JButton("Atualizar");
        JButton btnDelete = new JButton("Apagar");

        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(30, 144, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(255, 165, 0));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(220, 20, 60));

        btnAdd.setFocusPainted(false);
        btnUpdate.setFocusPainted(false);
        btnDelete.setFocusPainted(false);

        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 14));
        btnDelete.setFont(new Font("Arial", Font.BOLD, 14));

        headerPanel.add(btnAdd);
        headerPanel.add(btnUpdate);
        headerPanel.add(btnDelete);

        add(headerPanel, BorderLayout.NORTH);

        // Seção para a tabela
        tableModel = new DefaultTableModel();
        weatherTable = new JTable(tableModel);
        weatherTable.setFillsViewportHeight(true);

        // Estilização da tabela
        weatherTable.setFont(new Font("Arial", Font.PLAIN, 14));
        weatherTable.setRowHeight(25);
        weatherTable.setSelectionBackground(new Color(30, 144, 255));
        weatherTable.setSelectionForeground(Color.WHITE);
        weatherTable.setGridColor(Color.LIGHT_GRAY);

        JTableHeader tableHeader = weatherTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        tableHeader.setBackground(new Color(45, 45, 45));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        weatherTable.setDefaultRenderer(Object.class, centerRenderer);

        tableModel.addColumn("ID");
        tableModel.addColumn("Cidade");
        tableModel.addColumn("Temperatura");
        tableModel.addColumn("Humidade");
        tableModel.addColumn("Velocidade do Vento");
        tableModel.addColumn("Condição");
        tableModel.addColumn("Ícone");

        JScrollPane scrollPane = new JScrollPane(weatherTable);
        add(scrollPane, BorderLayout.CENTER);

        // Carregar os dados na tabela
        loadWeatherData();

        // Adicionar funcionalidades aos botões
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddWeatherDialog();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpdateWeatherDialog();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedWeatherData();
            }
        });
    }

    private void updateWeatherData(int id, String city, float temperature, int humidity, float windSpeed, String condition, String icon) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE weather SET city = ?, temperature = ?, humidity = ?, wind_speed = ?, condition = ?, icon = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, city);
            stmt.setFloat(2, temperature);
            stmt.setInt(3, humidity);
            stmt.setFloat(4, windSpeed);
            stmt.setString(5, condition);
            stmt.setString(6, icon);
            stmt.setInt(7, id);
            stmt.executeUpdate();

            // Log the operation
            DatabaseUtils.logUserOperation(userId, "UPDATE", "Updated weather data for city: " + city);

            loadWeatherData(); // Recarrega os dados na tabela
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar dados do tempo", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedWeatherData() {
        int selectedRow = weatherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma linha para apagar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String city = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja apagar este registro?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM weather WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();

                // Log the operation
                DatabaseUtils.logUserOperation(userId, "DELETE", "Deleted weather data for city: " + city);

                loadWeatherData(); // Recarrega os dados na tabela
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao apagar dados do tempo", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadWeatherData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM weather";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0); // Limpa a tabela antes de recarregar os dados

            while (rs.next()) {
                int id = rs.getInt("id");
                String city = rs.getString("city");
                float temperature = rs.getFloat("temperature");
                int humidity = rs.getInt("humidity");
                float windSpeed = rs.getFloat("wind_speed");
                String condition = rs.getString("condition");
                String icon = rs.getString("icon");

                tableModel.addRow(new Object[]{id, city, temperature, humidity, windSpeed, condition, icon});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do tempo", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddWeatherDialog() {
        JDialog dialog = createWeatherDialog("Adicionar Tempo", null);
        dialog.setVisible(true);
    }

    private void showUpdateWeatherDialog() {
        int selectedRow = weatherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma linha para atualizar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String city = (String) tableModel.getValueAt(selectedRow, 1);
        float temperature = (float) tableModel.getValueAt(selectedRow, 2);
        int humidity = (int) tableModel.getValueAt(selectedRow, 3);
        float windSpeed = (float) tableModel.getValueAt(selectedRow, 4);
        String condition = (String) tableModel.getValueAt(selectedRow, 5);
        String icon = (String) tableModel.getValueAt(selectedRow, 6);

        JDialog dialog = createWeatherDialog("Atualizar Tempo", new WeatherData(id, city, temperature, humidity, windSpeed, condition, icon));
        dialog.setVisible(true);
    }

    private JDialog createWeatherDialog(String title, WeatherData weatherData) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.getContentPane().setBackground(new Color(240, 240, 240));

        JTextField cityField = new JTextField();
        JTextField temperatureField = new JTextField();
        JTextField humidityField = new JTextField();
        JTextField windSpeedField = new JTextField();
        JTextField conditionField = new JTextField();
        JLabel iconPathLabel = new JLabel();
        JButton selectIconButton = new JButton("Selecionar Ícone");

        if (weatherData != null) {
            cityField.setText(weatherData.getCity());
            temperatureField.setText(String.valueOf(weatherData.getTemperature()));
            humidityField.setText(String.valueOf(weatherData.getHumidity()));
            windSpeedField.setText(String.valueOf(weatherData.getWindSpeed()));
            conditionField.setText(weatherData.getCondition());
            iconPathLabel.setText(weatherData.getIcon());
        }

        selectIconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    iconPathLabel.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        dialog.add(new JLabel("Cidade:", JLabel.RIGHT));
        dialog.add(cityField);
        dialog.add(new JLabel("Temperatura:", JLabel.RIGHT));
        dialog.add(temperatureField);
        dialog.add(new JLabel("Humidade:", JLabel.RIGHT));
        dialog.add(humidityField);
        dialog.add(new JLabel("Velocidade do Vento:", JLabel.RIGHT));
        dialog.add(windSpeedField);
        dialog.add(new JLabel("Condição:", JLabel.RIGHT));
        dialog.add(conditionField);
        dialog.add(new JLabel("Ícone:", JLabel.RIGHT));
        dialog.add(selectIconButton);
        dialog.add(new JLabel("Caminho do Ícone:", JLabel.RIGHT));
        dialog.add(iconPathLabel);

        JButton actionButton = new JButton(title);
        actionButton.setForeground(Color.WHITE);
        actionButton.setBackground(new Color(34, 139, 34));
        actionButton.setFont(new Font("Arial", Font.BOLD, 14));
        actionButton.setFocusPainted(false);

        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText();
                float temperature = Float.parseFloat(temperatureField.getText());
                int humidity = Integer.parseInt(humidityField.getText());
                float windSpeed = Float.parseFloat(windSpeedField.getText());
                String condition = conditionField.getText();
                String iconPath = iconPathLabel.getText();

                if (weatherData == null) {
                    addWeatherData(city, temperature, humidity, windSpeed, condition, iconPath);
                } else {
                    updateWeatherData(weatherData.getId(), city, temperature, humidity, windSpeed, condition, iconPath);
                }
                dialog.dispose();
            }
        });

        dialog.add(new JLabel());
        dialog.add(actionButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private void addWeatherData(String city, float temperature, int humidity, float windSpeed, String condition, String icon) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO weather (city, temperature, humidity, wind_speed, condition, icon) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, city);
            stmt.setFloat(2, temperature);
            stmt.setInt(3, humidity);
            stmt.setFloat(4, windSpeed);
            stmt.setString(5, condition);
            stmt.setString(6, icon);
            stmt.executeUpdate();

            // Log the operation
            DatabaseUtils.logUserOperation(userId, "ADD", "Added weather data for city: " + city);

            loadWeatherData(); // Recarrega os dados na tabela
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao adicionar dados do tempo", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
class WeatherData {
    private int id;
    private String city;
    private float temperature;
    private int humidity;
    private float windSpeed;
    private String condition;
    private String icon;

    public WeatherData(int id, String city, float temperature, int humidity, float windSpeed, String condition, String icon) {
        this.id = id;
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.icon = icon;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public String getIcon() {
        return icon;
    }
}
