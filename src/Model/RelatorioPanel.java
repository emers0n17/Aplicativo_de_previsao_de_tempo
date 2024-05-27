package Model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class RelatorioPanel extends JPanel {
    private JTable reportTable;
    private int userId;

    public RelatorioPanel(int userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Título
        JLabel titleLabel = new JLabel("Histórico de Operações do Usuário", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Tabela de relatório
        reportTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Carregar os dados do relatório
        loadReportData();
    }

    private void loadReportData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT operation_type, description, timestamp FROM user_history WHERE user_id = ? ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = buildTableModel(rs);
            reportTable.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws Exception {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Tipo de Operação");
        columnNames.add("Descrição");
        columnNames.add("Data e Hora");

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            vector.add(rs.getString("operation_type"));
            vector.add(rs.getString("description"));
            vector.add(rs.getTimestamp("timestamp"));
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }
}
