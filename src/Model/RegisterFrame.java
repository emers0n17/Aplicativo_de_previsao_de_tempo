package Model;

import View.LoginFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JLabel profilePicLabel;
    private File profilePicFile;

    public RegisterFrame() {
        setTitle("Registrar");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(45, 45, 45));
        mainPanel.setSize(400, 400);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Cadastro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        profilePicLabel = new JLabel(new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\View\\man.png"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(profilePicLabel, gbc);

        JButton uploadButton = new JButton("Carregar Foto");
        uploadButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(uploadButton, gbc);

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                profilePicFile = fileChooser.getSelectedFile();
                profilePicLabel.setIcon(new ImageIcon(profilePicFile.getAbsolutePath()));
            }
        });

        JLabel usernameLabel = new JLabel("Usuário:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(usernameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirmar Senha:");
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 100;
        mainPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 18));
        confirmPasswordField.setSize(500, 10);
        gbc.gridx = 1;
        gbc.gridy = 6;

        mainPanel.add(confirmPasswordField, gbc);

        JButton registerButton = new JButton("Cadastrar");
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        mainPanel.add(registerButton, gbc);

        JButton backButton = new JButton("Voltar");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        mainPanel.add(backButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();
        String email = emailField.getText();

        if (username.isEmpty() || password.length == 0 || confirmPassword.length == 0 || email.isEmpty() || profilePicFile == null) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Arrays.equals(password, confirmPassword)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String hashedPassword = hashPassword(new String(password));
            String sql = "INSERT INTO users (username, password, email, profile_pic) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            byte[] profilePicBytes = Files.readAllBytes(profilePicFile.toPath());
            stmt.setBytes(4, profilePicBytes);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registro bem sucedido");
            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}
