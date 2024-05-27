package Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.imageio.ImageIO;

public class PerfilPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField nameField;
    private JLabel profilePicLabel;
    private int userId;
    private JButton changePicButton;
    private JButton updateButton;

    public PerfilPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout(20, 20));  // Utiliza BorderLayout com gaps
        setBackground(new Color(238, 238, 238));  // Define um fundo claro

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(54, 57, 63));
        JLabel titleLabel = new JLabel("Perfil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(content, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel profilePicTitleLabel = new JLabel("Foto de Perfil:");
        profilePicTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        content.add(profilePicTitleLabel, gbc);

        profilePicLabel = new JLabel();
        profilePicLabel.setPreferredSize(new Dimension(100, 100));  // Define um tamanho padrão
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 1;
        gbc.gridy = 0;
        content.add(profilePicLabel, gbc);

        changePicButton = new JButton("Alterar Foto");
        changePicButton.setFont(new Font("Arial", Font.PLAIN, 14));
        changePicButton.setBackground(new Color(30, 144, 255));
        changePicButton.setForeground(Color.WHITE);
        changePicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alterarFotoDePerfil();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        content.add(changePicButton, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        content.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        content.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        content.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 3;
        content.add(passwordField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        content.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 4;
        content.add(emailField, gbc);


        updateButton = new JButton("Atualizar");
        updateButton.setFont(new Font("Arial", Font.BOLD, 16));
        updateButton.setBackground(new Color(34, 139, 34));
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarInformacoes();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 6;
        content.add(updateButton, gbc);

        carregarInformacoes();
    }

    private void carregarInformacoes() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT username, password, email, profile_pic FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                passwordField.setText(rs.getString("password"));
                emailField.setText(rs.getString("email"));
                byte[] profilePicBytes = rs.getBytes("profile_pic");
                if (profilePicBytes != null) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(profilePicBytes));
                    Image dimg = img.getScaledInstance(profilePicLabel.getPreferredSize().width, profilePicLabel.getPreferredSize().height, Image.SCALE_SMOOTH);
                    profilePicLabel.setIcon(new ImageIcon(dimg));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void alterarFotoDePerfil() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File profilePicFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(profilePicFile);
                Image dimg = img.getScaledInstance(profilePicLabel.getPreferredSize().width, profilePicLabel.getPreferredSize().height, Image.SCALE_SMOOTH);
                profilePicLabel.setIcon(new ImageIcon(dimg));

                byte[] profilePicBytes = Files.readAllBytes(profilePicFile.toPath());
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "UPDATE users SET profile_pic = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setBytes(1, profilePicBytes);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void atualizarInformacoes() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String name = nameField.getText();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setInt(4, userId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Informações atualizadas com sucesso");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        System.out.println("Teste perfil.");
    }
}
