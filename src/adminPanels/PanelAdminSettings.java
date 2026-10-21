package adminPanels;

import db.DBconnection;
import java.awt.Font;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import org.mindrot.jbcrypt.BCrypt;

public class PanelAdminSettings extends javax.swing.JPanel {

    private int selectedUserID = 0;
    private String currentUsername = ""; // stores logged user

    public PanelAdminSettings(String username) {
        this.currentUsername = username;
        initComponents();
        initCustomLogic();
    }
    
    public PanelAdminSettings() {
        initComponents();
        initCustomLogic();
    }

    private void initCustomLogic() {
      
        tableUsers.setRowHeight(30);
        tableUsers.getTableHeader().setFont(new java.awt.Font("Afacad Flux", java.awt.Font.BOLD, 14));
        tableUsers.setFont(new Font("Afacad Flux", Font.PLAIN, 12));
        tableUsers.setShowGrid(false);
        
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cashier", "Admin" }));
        cmbFilterUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Cashier", "Admin" }));
        
        loadUsers();
        
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearUserForm());
        cmbFilterUser.addActionListener(e -> loadUsers());
        
        tableUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableClick();
            }
        });

        loadShopDetails();
        btnSaveShop.addActionListener(e -> saveShopDetails());
        btnClear2.addActionListener(e -> clearShopForm());

        loadUserProfile();
    }

    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) tableUsers.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(new Object[]{"User ID", "Username", "Full Name", "Role"});

        String selectedRole = cmbFilterUser.getSelectedItem().toString();
        String sql = "SELECT * FROM users";
        
        if (!selectedRole.equals("All")) {
            sql = "SELECT * FROM users WHERE Role = '" + selectedRole + "'";
        }

        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("UserID"), rs.getString("Username"), rs.getString("FullName"), rs.getString("Role")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addUser() {
        String plainPass = new String(txtPassword.getPassword());

        if (txtUsername.getText().isEmpty() || plainPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password required.");
            return;
        }
        try (Connection con = DBconnection.getConnection()) {
            String hashedPass = BCrypt.hashpw(plainPass, BCrypt.gensalt());

            String sql = "INSERT INTO users (Username, FullName, PasswordHash, Role) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtUsername.getText());
            pst.setString(2, txtFullName.getText());
            pst.setString(3, hashedPass); 
            pst.setString(4, cmbRole.getSelectedItem().toString());
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "User Added!");
            cmbFilterUser.setSelectedIndex(0); 
            loadUsers();
            clearUserForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateUser() {
        if (selectedUserID == 0) return;
        try (Connection con = DBconnection.getConnection()) {
            String sql;
            String newPass = new String(txtPassword.getPassword());
            if (!newPass.isEmpty()) {
                sql = "UPDATE users SET Username=?, FullName=?, PasswordHash=?, Role=? WHERE UserID=?";
            } else {
                sql = "UPDATE users SET Username=?, FullName=?, Role=? WHERE UserID=?";
            }
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtUsername.getText());
            pst.setString(2, txtFullName.getText());
            if (!newPass.isEmpty()) {
                String hashedPass = BCrypt.hashpw(newPass, BCrypt.gensalt());
                pst.setString(3, hashedPass);
                pst.setString(4, cmbRole.getSelectedItem().toString());
                pst.setInt(5, selectedUserID);
            } else {
                pst.setString(3, cmbRole.getSelectedItem().toString());
                pst.setInt(4, selectedUserID);
            }
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Updated!");
            loadUsers();
            clearUserForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteUser() {
        if (selectedUserID == 0) return;
        int opt = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try (Connection con = DBconnection.getConnection()) {
                PreparedStatement pst = con.prepareStatement("DELETE FROM users WHERE UserID=?");
                pst.setInt(1, selectedUserID);
                pst.executeUpdate();
                loadUsers();
                clearUserForm();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tableClick() {
        int r = tableUsers.getSelectedRow();
        if (r == -1) return;
        DefaultTableModel model = (DefaultTableModel) tableUsers.getModel();
        selectedUserID = Integer.parseInt(model.getValueAt(r, 0).toString());
        txtUsername.setText(model.getValueAt(r, 1).toString());
        Object fn = model.getValueAt(r, 2);
        txtFullName.setText(fn != null ? fn.toString() : "");
        cmbRole.setSelectedItem(model.getValueAt(r, 3).toString());
        txtPassword.setText("");
    }

    private void clearUserForm() {
        txtUsername.setText(""); txtFullName.setText(""); txtPassword.setText("");
        cmbRole.setSelectedIndex(0); selectedUserID = 0; tableUsers.clearSelection();
    }


    private void loadShopDetails() {
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM shop_details WHERE ShopID = 1")) {
            if (rs.next()) {
                txtShopName.setText(rs.getString("ShopName"));
                txtAddress.setText(rs.getString("Address"));
                txtPhone.setText(rs.getString("Phone"));
                txtFooter.setText(rs.getString("FooterMessage"));
            }
        } catch (Exception e) { }
    }

    private void saveShopDetails() {
        if (txtShopName.getText().trim().isEmpty() || txtAddress.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shop Name and Address are required.");
            return;
        }
        try (Connection con = DBconnection.getConnection()) {
            String sql = "UPDATE shop_details SET ShopName=?, Address=?, Phone=?, FooterMessage=? WHERE ShopID=1";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtShopName.getText());
            pst.setString(2, txtAddress.getText());
            pst.setString(3, txtPhone.getText());
            pst.setString(4, txtFooter.getText());
            int rows = pst.executeUpdate();
            if(rows == 0) insertInitialShop(); 
            else JOptionPane.showMessageDialog(this, "Shop Details Saved!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void insertInitialShop() {
        try (Connection con = DBconnection.getConnection()) {
             PreparedStatement pst = con.prepareStatement("INSERT INTO shop_details VALUES (1, ?, ?, ?, ?)");
             pst.setString(1, txtShopName.getText());
             pst.setString(2, txtAddress.getText());
             pst.setString(3, txtPhone.getText());
             pst.setString(4, txtFooter.getText());
             pst.executeUpdate();
        } catch (Exception e) {}
    }

    private void clearShopForm() {
        txtShopName.setText(""); txtAddress.setText(""); txtPhone.setText(""); txtFooter.setText("");
    }

   
    private void loadUserProfile() {
        if(currentUsername == null || currentUsername.isEmpty()) return;
        
        try (Connection con = DBconnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE Username = ?");
            pst.setString(1, currentUsername);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) {
                txtUsername1.setText(rs.getString("Username"));
                txtFullName1.setText(rs.getString("FullName"));
                txtRole.setText(rs.getString("Role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private void updatePassword() {
        String oldPass = new String(pfOldPass.getPassword());
        String newPass = new String(pfNewPass.getPassword());
        String confirmPass = new String(pfConfirmPass.getPassword());
        
        if(oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Old and New Password.");
            return;
        }
        
        if(!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New Password and Confirm Password do not match.");
            return;
        }

        try (Connection con = DBconnection.getConnection()) {
            //verify Old Password
            PreparedStatement checkStmt = con.prepareStatement("SELECT PasswordHash FROM users WHERE Username = ?");
            checkStmt.setString(1, currentUsername);
            ResultSet rs = checkStmt.executeQuery();
            
            if(rs.next()) {
                String dbPass = rs.getString("PasswordHash");
                
                if(!BCrypt.checkpw(oldPass, dbPass)) {
                    JOptionPane.showMessageDialog(this, "Incorrect Old Password!");
                    return;
                }
            }
            
           
            String hashedNewPass = BCrypt.hashpw(newPass, BCrypt.gensalt());

            PreparedStatement updateStmt = con.prepareStatement("UPDATE users SET PasswordHash = ? WHERE Username = ?");
            updateStmt.setString(1, hashedNewPass);
            updateStmt.setString(2, currentUsername);
            updateStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Password Changed Successfully!");
            
           
            pfOldPass.setText("");
            pfNewPass.setText("");
            pfConfirmPass.setText("");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

   
   
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabUsers = new javax.swing.JPanel();
        pnlUserTop = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        cmbRole = new javax.swing.JComboBox<>();
        btnAdd = new classes.Buttont();
        btnUpdate = new classes.Buttont();
        btnDelete = new classes.Buttont();
        btnClear = new classes.Buttont();
        jLabel4 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        cmbFilterUser = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUsers = new javax.swing.JTable();
        tabShopInfo = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtShopName = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        txtFooter = new javax.swing.JTextField();
        btnSaveShop = new classes.Buttont();
        btnClear2 = new classes.Buttont();
        tabSecurity = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        buttont1 = new classes.Buttont();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtUsername1 = new javax.swing.JTextField();
        txtFullName1 = new javax.swing.JTextField();
        txtRole = new javax.swing.JTextField();
        pfOldPass = new javax.swing.JPasswordField();
        pfNewPass = new javax.swing.JPasswordField();
        pfConfirmPass = new javax.swing.JPasswordField();

        setBackground(new java.awt.Color(211, 211, 211));
        setOpaque(false);

        jTabbedPane1.setBackground(new java.awt.Color(102, 102, 102));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTabbedPane1.setFont(new java.awt.Font("Afacad Flux", 0, 18)); // NOI18N
        jTabbedPane1.setOpaque(true);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(845, 512));

        tabUsers.setBackground(new java.awt.Color(211, 211, 211));
        tabUsers.setLayout(new java.awt.BorderLayout());

        pnlUserTop.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlUserTop.setPreferredSize(new java.awt.Dimension(845, 160));

        jLabel1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel1.setText("Username");

        jLabel2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel2.setText("Password");

        jLabel3.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel3.setText("Role");

        txtUsername.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        txtPassword.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        cmbRole.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Cashier" }));

        btnAdd.setBackground(new java.awt.Color(255, 204, 0));
        btnAdd.setText("Add");
        btnAdd.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N

        btnUpdate.setBackground(new java.awt.Color(255, 204, 0));
        btnUpdate.setText("Update");
        btnUpdate.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N

        btnDelete.setBackground(new java.awt.Color(255, 204, 0));
        btnDelete.setText("Delete");
        btnDelete.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N

        btnClear.setBackground(new java.awt.Color(255, 204, 0));
        btnClear.setText("Clear");
        btnClear.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel4.setText("Full Name");

        txtFullName.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        javax.swing.GroupLayout pnlUserTopLayout = new javax.swing.GroupLayout(pnlUserTop);
        pnlUserTop.setLayout(pnlUserTopLayout);
        pnlUserTopLayout.setHorizontalGroup(
            pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserTopLayout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(68, 68, 68)
                .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtFullName))
                        .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(190, 190, 190)
                .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        pnlUserTopLayout.setVerticalGroup(
            pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUserTopLayout.createSequentialGroup()
                .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlUserTopLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlUserTopLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlUserTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbRole)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabUsers.add(pnlUserTop, java.awt.BorderLayout.PAGE_START);

        cmbFilterUser.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbFilterUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Admin", "Cashier" }));

        jLabel5.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel5.setText("Filter By:");

        jSeparator1.setBackground(new java.awt.Color(255, 204, 0));
        jSeparator1.setPreferredSize(new java.awt.Dimension(0, 5));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "User ID", "Username", "Full Name", "Role"
            }
        ));
        jScrollPane1.setViewportView(tableUsers);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmbFilterUser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbFilterUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        tabUsers.add(jPanel1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Manage Users", tabUsers);

        tabShopInfo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel6.setText("Shop Name");
        tabShopInfo.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, -1, -1));

        jLabel7.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel7.setText("Address");
        tabShopInfo.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 130, -1, -1));

        txtShopName.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        tabShopInfo.add(txtShopName, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 70, 210, -1));

        txtAddress.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        tabShopInfo.add(txtAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 130, 210, -1));

        jLabel8.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel8.setText("Phone Number");
        tabShopInfo.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 190, -1, -1));

        jLabel9.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel9.setText("Footer Note");
        tabShopInfo.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 250, -1, -1));

        txtPhone.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        tabShopInfo.add(txtPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 190, 210, -1));

        txtFooter.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        tabShopInfo.add(txtFooter, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 250, 210, -1));

        btnSaveShop.setBackground(new java.awt.Color(255, 204, 0));
        btnSaveShop.setText("Update");
        btnSaveShop.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        tabShopInfo.add(btnSaveShop, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 350, 90, 30));

        btnClear2.setBackground(new java.awt.Color(255, 204, 0));
        btnClear2.setText("Clear");
        btnClear2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        tabShopInfo.add(btnClear2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 350, 90, 30));

        jTabbedPane1.addTab("Shop Info", tabShopInfo);

        jLabel10.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel10.setText("Username");

        buttont1.setBackground(new java.awt.Color(255, 204, 0));
        buttont1.setText("Update");
        buttont1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont1ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel11.setText("Full Name");

        jLabel12.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel12.setText("Role");

        jSeparator2.setBackground(new java.awt.Color(255, 204, 0));

        jLabel13.setFont(new java.awt.Font("Afacad Flux", 1, 18)); // NOI18N
        jLabel13.setText("User Profile");

        jLabel14.setFont(new java.awt.Font("Afacad Flux", 1, 18)); // NOI18N
        jLabel14.setText("Change Password");

        jLabel15.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel15.setText("Old Password");

        jLabel16.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel16.setText("New Password");

        jLabel17.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel17.setText("Confirm Password");

        txtUsername1.setEditable(false);
        txtUsername1.setBackground(new java.awt.Color(255, 255, 255));
        txtUsername1.setFont(new java.awt.Font("Afacad Flux", 0, 15)); // NOI18N

        txtFullName1.setEditable(false);
        txtFullName1.setBackground(new java.awt.Color(255, 255, 255));
        txtFullName1.setFont(new java.awt.Font("Afacad Flux", 0, 15)); // NOI18N

        txtRole.setEditable(false);
        txtRole.setBackground(new java.awt.Color(255, 255, 255));
        txtRole.setFont(new java.awt.Font("Afacad Flux", 0, 15)); // NOI18N

        javax.swing.GroupLayout tabSecurityLayout = new javax.swing.GroupLayout(tabSecurity);
        tabSecurity.setLayout(tabSecurityLayout);
        tabSecurityLayout.setHorizontalGroup(
            tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabSecurityLayout.createSequentialGroup()
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabSecurityLayout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13)
                            .addGroup(tabSecurityLayout.createSequentialGroup()
                                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtUsername1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFullName1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRole, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel14)
                            .addGroup(tabSecurityLayout.createSequentialGroup()
                                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel17))
                                .addGap(33, 33, 33)
                                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(pfOldPass, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pfNewPass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                    .addComponent(pfConfirmPass)))
                            .addComponent(jSeparator2)))
                    .addGroup(tabSecurityLayout.createSequentialGroup()
                        .addGap(375, 375, 375)
                        .addComponent(buttont1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(256, Short.MAX_VALUE))
        );
        tabSecurityLayout.setVerticalGroup(
            tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabSecurityLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel13)
                .addGap(26, 26, 26)
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtFullName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(pfOldPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(pfNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabSecurityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(pfConfirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(buttont1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        jTabbedPane1.addTab("Security", tabSecurity);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("tab1\n");
    }// </editor-fold>//GEN-END:initComponents

    private void buttont1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont1ActionPerformed
        updatePassword();  // TODO add your handling code here:
    }//GEN-LAST:event_buttont1ActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.Buttont btnAdd;
    private classes.Buttont btnClear;
    private classes.Buttont btnClear2;
    private classes.Buttont btnDelete;
    private classes.Buttont btnSaveShop;
    private classes.Buttont btnUpdate;
    private classes.Buttont buttont1;
    private javax.swing.JComboBox<String> cmbFilterUser;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPasswordField pfConfirmPass;
    private javax.swing.JPasswordField pfNewPass;
    private javax.swing.JPasswordField pfOldPass;
    private javax.swing.JPanel pnlUserTop;
    private javax.swing.JPanel tabSecurity;
    private javax.swing.JPanel tabShopInfo;
    private javax.swing.JPanel tabUsers;
    private javax.swing.JTable tableUsers;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtFooter;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtFullName1;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtRole;
    private javax.swing.JTextField txtShopName;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtUsername1;
    // End of variables declaration//GEN-END:variables
}
