package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class DBconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/smartdine";
    private static final String USER = "root"; 
    private static final String PASSWORD = "Thahani7357"; 

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // convet pass to BCrypt hash
    public static void updatePassword(String username, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE users SET PasswordHash=? WHERE username=?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, hashedPassword);
            pst.setString(2, username);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("Password updated successfully for user: " + username);
            } else {
                System.out.println("User not found: " + username);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    
    
}
