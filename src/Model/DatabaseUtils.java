/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author usere
 */
public class DatabaseUtils {

    public static void logUserOperation(int userId, String operationType, String description) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO user_history (user_id, operation_type, description) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, operationType);
            stmt.setString(3, description);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
