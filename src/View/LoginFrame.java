package View;

import Model.DatabaseConnection;
import Model.RegisterFrame;
import com.mysql.jdbc.CommunicationsException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public LoginFrame() {
        setTitle("Login");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(45, 45, 45));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel profilePicture = new JLabel(new ImageIcon("C:\\Users\\usere\\OneDrive\\Desktop\\Wheater\\src\\images\\man.png"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(profilePicture, gbc);

        JLabel usernameLabel = new JLabel("Usuário:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(passwordField, gbc);

        showPasswordCheckBox = new JCheckBox("Mostrar Senha");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckBox.setBackground(new Color(45, 45, 45));
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);  // Mostra a senha
                } else {
                    passwordField.setEchoChar('●');  // Oculta a senha
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(showPasswordCheckBox, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBackground(new Color(30, 144, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        JLabel registerLabel = new JLabel("Não tem uma conta? Cadastre-se");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainPanel.add(registerLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new RegisterFrame().setVisible(true);
                dispose();
            }
        });

        setVisible(true);
    }

    private void loginUser() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String hashedPassword = hashPassword(new String(password));
            String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Bem vindo");
                new MainFrame(userId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciais inválidas", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (CommunicationsException e) {
            System.out.println("ATENÇÃO: FALHA NA COMUNICAÇÃO COM O mySQL");
            System.out.println("Erro: " + e.getMessage());
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
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
