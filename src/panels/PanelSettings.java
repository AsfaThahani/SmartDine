package panels;

import org.mindrot.jbcrypt.BCrypt; 
import javax.swing.*;
import java.sql.*;
import db.DBconnection;

public class PanelSettings extends JPanel {

    private String username;

  public PanelSettings(String username) {
        this.username = username;
        initComponents();
        loadUserDetails();
    }


 private void loadUserDetails() {

    if (username == null || username.isEmpty()) {
        return;
    }

    String sql = "SELECT Username, FullName, Role FROM users WHERE Username = ?";

    try (Connection con = DBconnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            txtUsername.setText(rs.getString("Username"));
            txtFullName.setText(rs.getString("FullName"));
            txtRole.setText(rs.getString("Role"));
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Failed to load user details:\n" + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

  
private void updatePassword() {

    String oldPass = new String(pfOldPass.getPassword());
    String newPass = new String(pfNewPass.getPassword());
    String confirmPass = new String(pfConfirmPass.getPassword());

    if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return;
    }

    if (!newPass.equals(confirmPass)) {          //for string comparison can't use ==       (! not)
        JOptionPane.showMessageDialog(this, "Passwords do not match.");
        return;
    }

    try (Connection con = DBconnection.getConnection()) {

        String sql = "SELECT PasswordHash FROM users WHERE username=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {

            String storedHash = rs.getString("PasswordHash");

            if (!BCrypt.checkpw(oldPass, storedHash)) {
                JOptionPane.showMessageDialog(this, "Old password is incorrect.");
                return;
            }

            String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt(12));

            String updateSql = "UPDATE users SET PasswordHash=? WHERE username=?";
            PreparedStatement up = con.prepareStatement(updateSql);
            up.setString(1, newHash);
            up.setString(2, username);
            up.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password updated successfully!");

            pfOldPass.setText("");
            pfNewPass.setText("");
            pfConfirmPass.setText("");

        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new panels.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        buttont1 = new classes.Buttont();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtFullName = new javax.swing.JTextField();
        txtRole = new javax.swing.JTextField();
        pfOldPass = new javax.swing.JPasswordField();
        pfNewPass = new javax.swing.JPasswordField();
        pfConfirmPass = new javax.swing.JPasswordField();

        setOpaque(false);

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel1.setText("Username");

        buttont1.setBackground(new java.awt.Color(255, 204, 0));
        buttont1.setText("Update");
        buttont1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel2.setText("Full Name");

        jLabel3.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel3.setText("Role");

        jLabel4.setFont(new java.awt.Font("Afacad Flux", 1, 18)); // NOI18N
        jLabel4.setText("User Profile");

        jLabel5.setFont(new java.awt.Font("Afacad Flux", 1, 18)); // NOI18N
        jLabel5.setText("Change Password");

        jLabel6.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel6.setText("Old Password");

        jLabel7.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel7.setText("New Password");

        jLabel8.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel8.setText("Confirm Password");

        txtUsername.setEditable(false);
        txtUsername.setBackground(new java.awt.Color(255, 255, 255));
        txtUsername.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        txtFullName.setEditable(false);
        txtFullName.setBackground(new java.awt.Color(255, 255, 255));
        txtFullName.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        txtRole.setEditable(false);
        txtRole.setBackground(new java.awt.Color(255, 255, 255));
        txtRole.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFullName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRole, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel5)
                            .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8))
                                .addGap(33, 33, 33)
                                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(pfOldPass, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pfNewPass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                    .addComponent(pfConfirmPass)))
                            .addComponent(jSeparator1)))
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addGap(367, 367, 367)
                        .addComponent(buttont1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(252, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel4)
                .addGap(26, 26, 26)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(pfOldPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(pfNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(pfConfirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(buttont1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttont1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont1ActionPerformed
         updatePassword(); // TODO add your handling code here:
    }//GEN-LAST:event_buttont1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.Buttont buttont1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private panels.PanelBorder panelBorder1;
    private javax.swing.JPasswordField pfConfirmPass;
    private javax.swing.JPasswordField pfNewPass;
    private javax.swing.JPasswordField pfOldPass;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtRole;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
