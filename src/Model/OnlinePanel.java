package Model;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class OnlinePanel extends JPanel {
    private JTextField cityField;
    private JLabel tempLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private JLabel conditionLabel;
    private JLabel weatherImageLabel;
    private String apiKey = "89b87aef95d01c01e62919c562637648"; // Substitua pela sua chave de API do OpenWeatherMap

    public OnlinePanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(255, 235, 153));  // Fundo amarelo claro

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(11, 94, 218));
        JLabel titleLabel = new JLabel("Tempo Online");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(255, 235, 153));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(content, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel cityPanel = new JPanel(new GridBagLayout());
        cityPanel.setBackground(new Color(11, 94, 218));
        cityPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Cidade", 0, 0, new Font("Consolas", Font.BOLD, 16), Color.WHITE));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        content.add(cityPanel, gbc);

        GridBagConstraints cityGbc = new GridBagConstraints();
        cityGbc.insets = new Insets(5, 5, 5, 5);
        cityGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel cityLabel = new JLabel("Cidade:");
        cityLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        cityLabel.setForeground(Color.WHITE);
        cityGbc.gridx = 0;
        cityGbc.gridy = 0;
        cityPanel.add(cityLabel, cityGbc);

        cityField = new JTextField(20);
        cityField.setFont(new Font("Consolas", Font.PLAIN, 16));
        cityGbc.gridx = 1;
        cityGbc.gridy = 0;
        cityPanel.add(cityField, cityGbc);

        JButton searchButton = new JButton("Buscar");
        searchButton.setFont(new Font("Consolas", Font.PLAIN, 14));
        searchButton.setBackground(new Color(11, 94, 218));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> buscarTempoOnline());
        cityGbc.gridx = 2;
        cityGbc.gridy = 0;
        cityPanel.add(searchButton, cityGbc);

        JPanel tempPanel = new JPanel(new GridBagLayout());
        tempPanel.setBackground(new Color(11, 94, 218));
        tempPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Dados Meteorológicos", 0, 0, new Font("Consolas", Font.BOLD, 16), Color.WHITE));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        content.add(tempPanel, gbc);

        GridBagConstraints tempGbc = new GridBagConstraints();
        tempGbc.insets = new Insets(5, 5, 5, 5);
        tempGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel tempTitleLabel = new JLabel("Temperatura:");
        tempTitleLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        tempTitleLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 0;
        tempGbc.gridy = 0;
        tempPanel.add(tempTitleLabel, tempGbc);

        tempLabel = new JLabel("-");
        tempLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        tempLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 1;
        tempGbc.gridy = 0;
        tempPanel.add(tempLabel, tempGbc);

        JLabel humidityTitleLabel = new JLabel("Umidade:");
        humidityTitleLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        humidityTitleLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 0;
        tempGbc.gridy = 1;
        tempPanel.add(humidityTitleLabel, tempGbc);

        humidityLabel = new JLabel("-");
        humidityLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        humidityLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 1;
        tempGbc.gridy = 1;
        tempPanel.add(humidityLabel, tempGbc);

        JLabel windTitleLabel = new JLabel("Vento:");
        windTitleLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        windTitleLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 0;
        tempGbc.gridy = 2;
        tempPanel.add(windTitleLabel, tempGbc);

        windLabel = new JLabel("-");
        windLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        windLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 1;
        tempGbc.gridy = 2;
        tempPanel.add(windLabel, tempGbc);

        JLabel conditionTitleLabel = new JLabel("Condição:");
        conditionTitleLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        conditionTitleLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 0;
        tempGbc.gridy = 3;
        tempPanel.add(conditionTitleLabel, tempGbc);

        conditionLabel = new JLabel("-");
        conditionLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        conditionLabel.setForeground(Color.WHITE);
        tempGbc.gridx = 1;
        tempGbc.gridy = 3;
        tempPanel.add(conditionLabel, tempGbc);

        weatherImageLabel = new JLabel();
        weatherImageLabel.setPreferredSize(new Dimension(100, 100));
        tempGbc.gridx = 1;
        tempGbc.gridy = 4;
        tempPanel.add(weatherImageLabel, tempGbc);
    }

    private void buscarTempoOnline() {
        String cidade = cityField.getText().trim();

        if (cidade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o nome da cidade", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cidade + "&appid=" + apiKey + "&units=metric&lang=pt";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // OK
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject main = jsonResponse.getJSONObject("main");
                double temperature = main.getDouble("temp");
                int humidity = main.getInt("humidity");

                JSONObject wind = jsonResponse.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");

                String weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("icon");

                // Update labels
                tempLabel.setText(temperature + " °C");
                humidityLabel.setText(humidity + " %");
                windLabel.setText(windSpeed + " m/s");
                conditionLabel.setText(weatherDescription);

                // Update weather icon
                String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                ImageIcon icon = new ImageIcon(new URL(iconUrl));
                weatherImageLabel.setIcon(icon);

            } else {
                JOptionPane.showMessageDialog(this, "Cidade não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar dados online!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Painel Online de Tempo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new OnlinePanel());
        frame.setVisible(true);
    }
}
