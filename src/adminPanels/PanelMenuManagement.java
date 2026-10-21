package adminPanels;

import db.DBconnection;
import java.io.File;
import java.sql.*;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class PanelMenuManagement extends javax.swing.JPanel {

    private String selectedImagePath = null; 
    private int selectedID = 0; 

    public PanelMenuManagement() {
        initComponents();
        initCustomComponents();
    }

    
    private void initCustomComponents() {
        
        
       
        jTable1.setRowHeight(30);
        jTable1.getTableHeader().setFont(new java.awt.Font("Afacad Flux", java.awt.Font.BOLD, 14));
        jTable1.setShowGrid(false);
       
        
        loadData();
        
         
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        
        btnBrowse.addActionListener(e -> selectImage());
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        
       
        cmbFilterCategory.addActionListener(e -> loadData());
        
        
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableClick();
            }
        });
    }

  
    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); 
        model.setColumnIdentifiers(new Object[]{"Product ID", "Product Name", "Category", "Price", "Status", "Image Path"});

        String selectedCategory = cmbFilterCategory.getSelectedItem().toString();
        String sql = "SELECT * FROM products";
        
        if (!selectedCategory.equals("All")) {
            sql = "SELECT * FROM products WHERE Category = '" + selectedCategory + "'";
        }
        
        try (Connection con = DBconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ProductID"),
                    rs.getString("ProductName"),
                    rs.getString("Category"),
                    String.format("%.2f", rs.getDouble("UnitPrice")),
                    rs.getString("Availability_Status"),
                    rs.getString("ImagePath")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
        
        

javax.swing.table.TableColumnModel cm = jTable1.getColumnModel();


cm.getColumn(0).setPreferredWidth(60);  // ID
cm.getColumn(1).setPreferredWidth(250); // Name
cm.getColumn(2).setPreferredWidth(100); // Category
cm.getColumn(3).setPreferredWidth(80);  // Price
cm.getColumn(4).setPreferredWidth(100); // Status


cm.getColumn(5).setMinWidth(0);
cm.getColumn(5).setMaxWidth(0);
cm.getColumn(5).setPreferredWidth(0);
    }

  
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            
           
            pictureBox1.setImage(new ImageIcon(selectedImagePath));
            pictureBox1.repaint();
        }
    }


    private void addProduct() {
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();

        
        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name and Price.");
            return;
        }

        
        if (selectedImagePath == null) {
            JOptionPane.showMessageDialog(this, "Please select an image for the product!");
            return;
        }

        
        if (!name.matches("^[a-zA-Z0-9 ]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid Name!");
            return;
        }

       
        double price = 0;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Price!");
            return;
        }

        
        try (Connection con = DBconnection.getConnection()) {
            String sql = "INSERT INTO products (ProductName, UnitPrice, Category, Availability_Status, ImagePath) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setString(3, cmbCategory.getSelectedItem().toString());
            pst.setString(4, cmbStatus.getSelectedItem().toString());
            pst.setString(5, selectedImagePath);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product Added Successfully!");
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

  
    private void updateProduct() {
        if (selectedID == 0) {
            JOptionPane.showMessageDialog(this, "Select an item to update.");
            return;
        }

        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();

       
        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name and Price.");
            return;
        }

       
        if (!name.matches("^[a-zA-Z0-9 ]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid Name! Only text, numbers, and spaces are allowed.");
            return;
        }

        
        double price = 0;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Price! Only numbers are allowed.");
            return;
        }

        
        try (Connection con = DBconnection.getConnection()) {
            String sql;
            if (selectedImagePath != null) {
                sql = "UPDATE products SET ProductName=?, UnitPrice=?, Category=?, Availability_Status=?, ImagePath=? WHERE ProductID=?";
            } else {
                sql = "UPDATE products SET ProductName=?, UnitPrice=?, Category=?, Availability_Status=? WHERE ProductID=?";
            }

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setString(3, cmbCategory.getSelectedItem().toString());
            pst.setString(4, cmbStatus.getSelectedItem().toString());

            if (selectedImagePath != null) {
                pst.setString(5, selectedImagePath);
                pst.setInt(6, selectedID);
            } else {
                pst.setInt(5, selectedID);
            }

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product Updated!");
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
   
    private void deleteProduct() {
        if(selectedID == 0) {
            JOptionPane.showMessageDialog(this, "Select an product to delete.");
            return;
        }
        
        int opt = JOptionPane.showConfirmDialog(this, "Delete this product permanently?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(opt == JOptionPane.YES_OPTION) {
            try (Connection con = DBconnection.getConnection()) {
                PreparedStatement pst = con.prepareStatement("DELETE FROM products WHERE ProductID=?");
                pst.setInt(1, selectedID);
                pst.executeUpdate();
                loadData();
                clearForm();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    private void tableClick() {
        int r = jTable1.getSelectedRow();
        if (r == -1) return;

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        selectedID = Integer.parseInt(model.getValueAt(r, 0).toString());
        txtName.setText(model.getValueAt(r, 1).toString());
        cmbCategory.setSelectedItem(model.getValueAt(r, 2).toString());
        txtPrice.setText(model.getValueAt(r, 3).toString());
        cmbStatus.setSelectedItem(model.getValueAt(r, 4).toString());
        
        Object imgObj = model.getValueAt(r, 5);
        String path = (imgObj != null) ? imgObj.toString() : null;
        
        selectedImagePath = path; 

        if (path != null && !path.isEmpty()) {
            ImageIcon ii = null;
            
            
            java.net.URL imgURL = getClass().getClassLoader().getResource(path);
            if (imgURL != null) {
                ii = new ImageIcon(imgURL);
            } else {
                // 
                ii = new ImageIcon(path);
            }

            
            if (ii != null && ii.getIconWidth() > 0) {
                 pictureBox1.setImage(ii); 
            } else {
                 pictureBox1.setImage(null);
            }
        } else {
            pictureBox1.setImage(null);
        }
        pictureBox1.repaint();
    }

    
    private void clearForm() {
        txtName.setText("");
        txtPrice.setText("");
        cmbCategory.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        pictureBox1.setImage(null);
        pictureBox1.repaint();
        selectedImagePath = null;
        selectedID = 0;
        jTable1.clearSelection();
    }

 
   
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlInput = new javax.swing.JPanel();
        btnBrowse = new classes.Buttont();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        cmbStatus = new javax.swing.JComboBox<>();
        btnAdd = new classes.Buttont();
        btnUpdate = new classes.Buttont();
        btnDelete = new classes.Buttont();
        btnClear = new classes.Buttont();
        panelBorder1 = new panels.PanelBorder();
        pictureBox1 = new classes.PictureBox();
        jPanel1 = new javax.swing.JPanel();
        pnlFilterBar = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cmbFilterCategory = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setBackground(new java.awt.Color(211, 211, 211));
        setLayout(new java.awt.BorderLayout());

        pnlInput.setBackground(new java.awt.Color(211, 211, 211));
        pnlInput.setMinimumSize(new java.awt.Dimension(636, 150));
        pnlInput.setPreferredSize(new java.awt.Dimension(0, 190));
        pnlInput.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnBrowse.setBackground(new java.awt.Color(255, 204, 0));
        btnBrowse.setText("Browse Image");
        btnBrowse.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        pnlInput.add(btnBrowse, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 120, 30));

        jLabel1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel1.setText("Product Name");
        pnlInput.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 100, -1));

        jLabel2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel2.setText("Price");
        pnlInput.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 50, -1));

        jLabel3.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel3.setText("Category");
        pnlInput.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, -1, 30));

        jLabel4.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel4.setText("Status");
        pnlInput.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 140, 50, 30));

        txtName.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        pnlInput.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 260, -1));

        txtPrice.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        pnlInput.add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 260, -1));

        cmbCategory.setEditable(true);
        cmbCategory.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Burgers", "Desserts", "Drinks" }));
        cmbCategory.setOpaque(true);
        pnlInput.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 100, 110, -1));

        cmbStatus.setEditable(true);
        cmbStatus.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Sold Out" }));
        cmbStatus.setOpaque(true);
        pnlInput.add(cmbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 140, 110, -1));

        btnAdd.setBackground(new java.awt.Color(255, 204, 0));
        btnAdd.setText("Add");
        btnAdd.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        pnlInput.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 20, 90, 30));

        btnUpdate.setBackground(new java.awt.Color(255, 204, 0));
        btnUpdate.setText("Update");
        btnUpdate.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        pnlInput.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, 90, 30));

        btnDelete.setBackground(new java.awt.Color(255, 204, 0));
        btnDelete.setText("Delete");
        btnDelete.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        pnlInput.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 100, 90, 30));

        btnClear.setBackground(new java.awt.Color(255, 204, 0));
        btnClear.setText("Clear");
        btnClear.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        pnlInput.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, 90, 30));

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        pictureBox1.setPreferredSize(new java.awt.Dimension(150, 120));

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlInput.add(panelBorder1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 160, 130));

        add(pnlInput, java.awt.BorderLayout.PAGE_START);

        jPanel1.setPreferredSize(new java.awt.Dimension(0, 100));
        jPanel1.setLayout(new java.awt.BorderLayout());

        pnlFilterBar.setBackground(new java.awt.Color(211, 211, 211));
        pnlFilterBar.setPreferredSize(new java.awt.Dimension(0, 60));
        pnlFilterBar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel5.setText("Filter By:");
        pnlFilterBar.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 100, 30));

        cmbFilterCategory.setEditable(true);
        cmbFilterCategory.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbFilterCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Burgers", "Desserts", "Drinks" }));
        cmbFilterCategory.setOpaque(true);
        pnlFilterBar.add(cmbFilterCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 110, -1));

        jSeparator1.setBackground(new java.awt.Color(255, 204, 0));
        pnlFilterBar.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, 5));

        jPanel1.add(pnlFilterBar, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setBackground(new java.awt.Color(211, 211, 211));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jScrollPane1.setFont(new java.awt.Font("Afacad Flux", 0, 12)); // NOI18N

        jTable1.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFocusable(false);
        jTable1.setSelectionBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.Buttont btnAdd;
    private classes.Buttont btnBrowse;
    private classes.Buttont btnClear;
    private classes.Buttont btnDelete;
    private classes.Buttont btnUpdate;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbFilterCategory;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private panels.PanelBorder panelBorder1;
    private classes.PictureBox pictureBox1;
    private javax.swing.JPanel pnlFilterBar;
    private javax.swing.JPanel pnlInput;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPrice;
    // End of variables declaration//GEN-END:variables
}
