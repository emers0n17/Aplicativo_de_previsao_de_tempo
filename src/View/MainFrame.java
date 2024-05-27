package View;

import Model.RelatorioPanel;
import Model.GerenciarPanel;
import Model.TempoPanel;
import Model.PerfilPanel;
import Model.OnlinePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private final JPanel mainPanel;
    private final int userId;

    public MainFrame(int userId) {
        this.userId = userId;
        setTitle("Weather System");
        setSize(1250, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel de navegação lateral
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(45, 45, 45));
        sidePanel.setPreferredSize(new Dimension(200, getHeight()));

        String[] buttonLabels = {"Tempo", "Gerenciar", "Perfil", "Relatório", "Tempo Online", "Sair", "Voltar"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setMaximumSize(new Dimension(200, 50));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setFocusPainted(false);
            button.setBackground(new Color(30, 30, 30));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Consolas", Font.PLAIN, 18));
            button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            button.addActionListener(new MenuActionListener(label));
            sidePanel.add(button);
            sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // Painel principal onde as telas serão exibidas
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Adicionando os painéis ao mainPanel
        mainPanel.add(new TempoPanel(userId), "Tempo");
        mainPanel.add(new GerenciarPanel(userId), "Gerenciar");
        mainPanel.add(new PerfilPanel(userId), "Perfil");
        mainPanel.add(new RelatorioPanel(userId), "Relatório");
        mainPanel.add(new OnlinePanel(), "Tempo Online"); // Adiciona o novo painel OnlinePanel

        add(sidePanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private class MenuActionListener implements ActionListener {
        private String panelName;

        public MenuActionListener(String panelName) {
            this.panelName = panelName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (panelName) {
                case "Sair":
                    System.exit(0);
                case "Voltar":
                    new LoginFrame().setVisible(true);
                    dispose();
                    break;
                default:
                    CardLayout cl = (CardLayout) (mainPanel.getLayout());
                    cl.show(mainPanel, panelName);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(2).setVisible(true));  // Use a test user ID
    }
}
