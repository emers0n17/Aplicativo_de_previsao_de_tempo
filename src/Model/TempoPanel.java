package Model;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class TempoPanel extends JPanel {
    private JTextField cityField;
    private JLabel cityNameLabel, tempLabel, humidityLabel, conditionLabel, windSpeedLabel;
    private JLabel weatherImageLabel, humidityImageLabel, conditionImageLabel, windSpeedImageLabel;
    private int userId;

    private ImageIcon coldImage = new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\nuvem.png");
    private ImageIcon mildImage = new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\normal.png");
    private ImageIcon hotImage = new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\sol.png");
    private ImageIcon humidityImage = new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\humidade.png");
    private ImageIcon conditionImage = new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\condicao.png");
    private ImageIcon windSpeedImage = new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\vento.png");

    public TempoPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(255, 255, 255));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(54, 57, 63));
        JLabel titleLabel = new JLabel("Tempo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // City Panel
        JPanel cityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        cityPanel.setBackground(new Color(255, 255, 255));
        add(cityPanel, BorderLayout.CENTER);

        cityNameLabel = new JLabel("Cidade: ");
        cityNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cityPanel.add(cityNameLabel);

        cityField = new JTextField(20);
        cityField.setFont(new Font("Arial", Font.PLAIN, 16));
        cityField.setText("Maputo");
        cityPanel.add(cityField);

        JButton searchButton = new JButton("Buscar");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setBackground(new Color(30, 144, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> buscarTempo());
        cityPanel.add(searchButton);

        // Content
        JPanel content = new JPanel(new GridLayout(2, 2, 20, 20));
        content.setBackground(new Color(255, 255, 255));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(content, BorderLayout.SOUTH);

        // Temperature Panel
        JPanel tempPanel = createInfoPanel("Temperatura:", tempLabel = new JLabel("-"), weatherImageLabel = new JLabel());
        content.add(tempPanel);

        // Humidity Panel
        JPanel humidityPanel = createInfoPanel("Humidade:", humidityLabel = new JLabel("-"), humidityImageLabel = new JLabel());
        content.add(humidityPanel);

        // Condition Panel
        JPanel conditionPanel = createInfoPanel("Condição:", conditionLabel = new JLabel("-"), conditionImageLabel = new JLabel());
        content.add(conditionPanel);

        // Wind Speed Panel
        JPanel windSpeedPanel = createInfoPanel("Velocidade do Vento:", windSpeedLabel = new JLabel("-"), windSpeedImageLabel = new JLabel());
        content.add(windSpeedPanel);

        buscarTempo(); // Carrega as informações iniciais
    }

    private JPanel createInfoPanel(String title, JLabel valueLabel, JLabel imageLabel) {
        JPanel panel = new RoundedPanel(10);
        panel.setBackground(new Color(173, 216, 230));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.setPreferredSize(new Dimension(200, 200));

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        valueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(valueLabel, gbc);

        imageLabel.setPreferredSize(new Dimension(128, 128));
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(imageLabel, gbc);

        return panel;
    }

    private void buscarTempo() {
        String cidade = cityField.getText();

        if (cidade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o nome da cidade", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/weather_system", "root", "entrar")) {
            String query = "SELECT temperature, humidity, wind_speed, `condition`, icon FROM weather WHERE city = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cidade);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                float temperatura = resultSet.getFloat("temperature");
                int humidade = resultSet.getInt("humidity");
                float windSpeed = resultSet.getFloat("wind_speed");
                String condition = resultSet.getString("condition");
                String iconPath = resultSet.getString("icon");

                tempLabel.setText(String.format("%.2f °C", temperatura));
                humidityLabel.setText(humidade + " %");
                windSpeedLabel.setText(String.format("%.2f km/h", windSpeed));
                conditionLabel.setText(condition);

                // Atualiza o rótulo com o nome da cidade
                cityNameLabel.setText("Cidade: " + cidade);

                // Define os ícones de acordo com a temperatura
                if (temperatura < 20) {
                    weatherImageLabel.setIcon(coldImage);
                } else if (temperatura < 35) {
                    weatherImageLabel.setIcon(mildImage);
                } else {
                    weatherImageLabel.setIcon(hotImage);
                }

                // Define os ícones para as outras informações
                humidityImageLabel.setIcon(humidityImage);
                conditionImageLabel.setIcon(conditionImage);
                windSpeedImageLabel.setIcon(windSpeedImage);
            } else {
                int option = JOptionPane.showConfirmDialog(this, "Cidade não encontrada. Deseja adicioná-la?", "Cidade não encontrada", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    adicionarCidade(cidade);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar dados do tempo", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarCidade(String cidade) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField tempField = new JTextField();
        JTextField humidityField = new JTextField();
        JTextField windField = new JTextField();
        JTextField conditionField = new JTextField();
        JTextField iconField = new JTextField();

        panel.add(new JLabel("Temperatura:"));
        panel.add(tempField);
        panel.add(new JLabel("Humidade:"));
        panel.add(humidityField);
        panel.add(new JLabel("Velocidade do Vento:"));
        panel.add(windField);
        panel.add(new JLabel("Condição:"));
        panel.add(conditionField);
        panel.add(new JLabel("Ícone:"));
        panel.add(iconField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Adicionar Nova Cidade", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/weather_system", "root", "entrar")) {
                String query = "INSERT INTO weather (city, temperature, humidity, wind_speed, `condition`, icon) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, cidade);
                statement.setFloat(2, Float.parseFloat(tempField.getText()));
                statement.setInt(3, Integer.parseInt(humidityField.getText()));
                statement.setFloat(4, Float.parseFloat(windField.getText()));
                statement.setString(5, conditionField.getText());
                statement.setString(6, iconField.getText());
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Cidade adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                buscarTempo(); // Atualiza os dados da nova cidade adicionada
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao adicionar cidade", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Painel de Tempo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setContentPane(new TempoPanel(1));
            frame.setVisible(true);
        });
    }
}

class RoundedPanel extends JPanel {
    private int radius;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        g2d.dispose();
    }
}
