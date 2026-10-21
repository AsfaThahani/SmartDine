package forms;

import classes.WrapLayout;
import db.DBconnection;
import java.awt.Color;
import panels.BillPanel;
import panels.ItemPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.event.*;
import classes.ViewOrderDialog;

public class CashierMain extends javax.swing.JFrame {

   
    private Map<Integer, BillPanel> billMap = new HashMap<>(); //hashmap used to track active cart items (int is productID, it checks will the billpanel)

    private String currentUsername;

    private java.awt.CardLayout cardLayout;

    private javax.swing.JPanel bodyPanel;
    
    private panels.PanelOrder panelOrder;

     public CashierMain(String username) {
        setUndecorated(true); 
        setBackground(new Color(0, 0, 0, 0)); //alpha transparent
        
        initComponents();
        
        this.currentUsername = username;
        
        initCustomComponents();
    }
     
     
 private void initCustomComponents() {
        titleBar1.initJFram(this);

        panelOrder = new panels.PanelOrder();

        cardLayout = new java.awt.CardLayout();
        bodyPanel = new javax.swing.JPanel(cardLayout);
        bodyPanel.setOpaque(false);

        jPanel1.remove(titleBar1);

        jScrollPane1.setBorder(null);
        jScrollPane1.getViewport().setBorder(null);
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane4.setOpaque(false);
        jScrollPane4.getViewport().setOpaque(false);
        jScrollPane4.setBorder(null);

        jPanel1.setOpaque(false);
        jPanel1.setBackground(new Color(0, 0, 0, 0));
        itemsPanel.setOpaque(false);

        // Adding Cards for the cardlayout
        bodyPanel.add(jPanel1, "POS");
        bodyPanel.add(panelOrder, "ORDER");
        bodyPanel.add(new panels.PanelSettings(currentUsername), "SETTINGS"); //passing current username to settings

       
        jLayeredPane1.setLayout(new java.awt.BorderLayout());

        javax.swing.JPanel topWrapper = new javax.swing.JPanel(new java.awt.BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(titleBar1, java.awt.BorderLayout.LINE_END);

        jLayeredPane1.add(topWrapper, java.awt.BorderLayout.PAGE_START);//north
        jLayeredPane1.add(bodyPanel, java.awt.BorderLayout.CENTER);

        itemsPanel.setLayout(new WrapLayout(WrapLayout.LEFT, 15, 15));

        panelPriceList2.setLayout(new javax.swing.BoxLayout(panelPriceList2, javax.swing.BoxLayout.Y_AXIS)); //y_axis stacks items vertically
        panelPriceList2.setOpaque(false);

     
        panelCategory2.setCategoryListener(categoryName -> {
            loadProducts(categoryName);
        });

      
        loadProducts("Burgers");

        try {
            java.net.URL logoutUrl = getClass().getResource("/resources/IconLogout.png");
            if(logoutUrl != null) {
                btnLogout.setImage(new ImageIcon(logoutUrl));
            }
        } catch (Exception e) {
            System.err.println("Error loading logout image: " + e.getMessage());
        }

        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int confirm = JOptionPane.showConfirmDialog(
                    CashierMain.this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginForm().setVisible(true);
                }
            }
        });
    }

   
private void loadProducts(String category) {
    itemsPanel.removeAll();
    
    try (Connection con = DBconnection.getConnection()) {
        
        
        String sql = "SELECT ProductID, ProductName, UnitPrice, ImagePath, Availability_Status FROM products WHERE Category = ?";
        
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, category); 

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("ProductID");
            String name = rs.getString("ProductName");
            double price = rs.getDouble("UnitPrice");
            String imagePath = rs.getString("ImagePath");
            String status = rs.getString("Availability_Status");

            Image img = null;
            if (imagePath != null) {
                java.net.URL imgURL = getClass().getClassLoader().getResource(imagePath);
                if (imgURL != null) img = new ImageIcon(imgURL).getImage();
            }

            ItemPanel item = new ItemPanel();

        
            if ("Sold Out".equalsIgnoreCase(status)) {
                
               
                String displayName = name + " (--X--)";
                
               
                item.setData(id, displayName, price, img, clicked -> {
                    JOptionPane.showMessageDialog(this, "Sorry, " + name + " is currently Sold Out.");
                });
                
                

            } else {
               
                item.setData(id, name, price, img, clicked -> addItemToBill(clicked));
            }

            item.setPreferredSize(new Dimension(150, 180));
            itemsPanel.add(item);
        }
        
        itemsPanel.revalidate();
        itemsPanel.repaint();
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
    }
}

   private void addItemToBill(ItemPanel item) {
        int productID = item.getProductID();

        if (billMap.containsKey(productID)) {
            JOptionPane.showMessageDialog(this, "Item is already in the bill!\nPlease use the spinner to adjust quantity.");
            return; 
        }

        BillPanel bill = new BillPanel();
        bill.setData(productID, item.getProductName(), item.getUnitPrice(), item.getProductImage());

        JPanel wrapper = new JPanel(new java.awt.BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(bill, java.awt.BorderLayout.CENTER);
        
        wrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 25));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, bill.getPreferredSize().height + 10));

        bill.setOnDelete(() -> {
            panelPriceList2.remove(wrapper);
            billMap.remove(productID);
            panelPriceList2.revalidate();
            panelPriceList2.repaint();
            calculateTotal();
        });

        bill.setOnQtyChange(() -> calculateTotal());

        panelPriceList2.add(wrapper);
        panelPriceList2.revalidate();
        panelPriceList2.repaint();
        billMap.put(productID, bill);
        calculateTotal();
    }

    private void calculateTotal() {
        double subtotal = billMap.values().stream().mapToDouble(BillPanel::getPanelTotal).sum();
        double tax = subtotal * 0.12; 
        double total = subtotal + tax;

        lblSubtotal.setText("Rs. " + String.format("%.2f", subtotal));
        lblTax.setText("Rs. " + String.format("%.2f", tax));
        lblTotal.setText("Rs. " + String.format("%.2f", total));
    }
   
   
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new panels.PanelBorder();
        PanelNav = new panels.PanelBorder();
        buttont2 = new classes.Buttont();
        buttont3 = new classes.Buttont();
        buttont4 = new classes.Buttont();
        btnLogout = new classes.PictureBoxButton();
        pictureBox1 = new classes.PictureBox();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemsPanel = new javax.swing.JPanel();
        panelBorder7 = new panels.PanelBorder();
        jScrollPane4 = new javax.swing.JScrollPane();
        panelPriceList2 = new javax.swing.JPanel();
        panelPaymentCategory = new panels.PanelBorder();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        buttont5 = new classes.Buttont();
        pictureBox2 = new classes.PictureBox();
        pictureBox3 = new classes.PictureBox();
        lblSubtotal = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        panelPaymentCategory1 = new panels.PanelPaymentCategory();
        jLabel10 = new javax.swing.JLabel();
        lblTax = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        titleBar1 = new classes.TitleBar();
        panelBorder2 = new panels.PanelBorder();
        panelCategory2 = new panels.PanelCategory();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelBorder1.setBackground(new java.awt.Color(211, 211, 211));

        PanelNav.setBackground(new java.awt.Color(51, 51, 51));
        PanelNav.setPreferredSize(new java.awt.Dimension(198, 179));
        PanelNav.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttont2.setBackground(new java.awt.Color(255, 204, 0));
        buttont2.setText("Menu");
        buttont2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont2ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 170, 28));

        buttont3.setBackground(new java.awt.Color(255, 204, 0));
        buttont3.setText("Orders");
        buttont3.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont3ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 170, 28));

        buttont4.setBackground(new java.awt.Color(255, 204, 0));
        buttont4.setText("Settings");
        buttont4.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont4ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 170, 28));
        PanelNav.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 500, 30, 30));

        pictureBox1.setBackground(new java.awt.Color(255, 255, 255));
        pictureBox1.setImage(new javax.swing.ImageIcon(getClass().getResource("/resources/logo.png"))); // NOI18N
        pictureBox1.setPreferredSize(new java.awt.Dimension(150, 120));
        PanelNav.add(pictureBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 160, 80));

        jLayeredPane1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 234));
        jPanel1.setOpaque(false);
        jPanel1.setRequestFocusEnabled(false);

        jScrollPane1.setBackground(new java.awt.Color(51, 51, 51));
        jScrollPane1.setBorder(null);

        itemsPanel.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(itemsPanel);

        panelBorder7.setBackground(new java.awt.Color(51, 51, 51));
        panelBorder7.setPreferredSize(new java.awt.Dimension(187, 577));

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelPriceList2.setBackground(new java.awt.Color(51, 51, 51));
        panelPriceList2.setLayout(new javax.swing.BoxLayout(panelPriceList2, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane4.setViewportView(panelPriceList2);

        panelPaymentCategory.setBackground(new java.awt.Color(51, 51, 51));
        panelPaymentCategory.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Afacad Flux", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Tax");
        panelPaymentCategory.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel12.setFont(new java.awt.Font("Afacad Flux", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total");
        panelPaymentCategory.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        buttont5.setBackground(new java.awt.Color(255, 204, 0));
        buttont5.setText("Pay");
        buttont5.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N
        buttont5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont5ActionPerformed(evt);
            }
        });
        panelPaymentCategory.add(buttont5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 180, 80, 28));
        panelPaymentCategory.add(pictureBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 134, 58, 35));
        panelPaymentCategory.add(pictureBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(212, 134, 62, 35));

        lblSubtotal.setFont(new java.awt.Font("Afacad Flux", 1, 14)); // NOI18N
        lblSubtotal.setForeground(new java.awt.Color(255, 255, 255));
        panelPaymentCategory.add(lblSubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 80, -1));

        lblTotal.setFont(new java.awt.Font("Afacad Flux", 1, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        panelPaymentCategory.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 80, -1));
        panelPaymentCategory.add(panelPaymentCategory1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        jLabel10.setFont(new java.awt.Font("Afacad Flux", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Subtotal");
        panelPaymentCategory.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        lblTax.setFont(new java.awt.Font("Afacad Flux", 1, 14)); // NOI18N
        lblTax.setForeground(new java.awt.Color(255, 255, 255));
        panelPaymentCategory.add(lblTax, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 80, -1));

        javax.swing.GroupLayout panelBorder7Layout = new javax.swing.GroupLayout(panelBorder7);
        panelBorder7.setLayout(panelBorder7Layout);
        panelBorder7Layout.setHorizontalGroup(
            panelBorder7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBorder7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBorder7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelPaymentCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelBorder7Layout.setVerticalGroup(
            panelBorder7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(panelPaymentCategory, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelBorder2.setBackground(new java.awt.Color(211, 211, 211));

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 125, Short.MAX_VALUE)
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 86, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(panelCategory2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)
                        .addGap(8, 8, 8)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(panelBorder7, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titleBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelCategory2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(titleBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelBorder7, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jLayeredPane1.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelNav, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(PanelNav, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void buttont3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont3ActionPerformed
cardLayout.show(bodyPanel, "ORDER");        // TODO add your handling code here:
    }//GEN-LAST:event_buttont3ActionPerformed

    private void buttont2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont2ActionPerformed
cardLayout.show(bodyPanel, "POS");        // TODO add your handling code here:
    }//GEN-LAST:event_buttont2ActionPerformed

    private void buttont4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont4ActionPerformed

    cardLayout.show(bodyPanel, "SETTINGS");        // TODO add your handling code here:
    }//GEN-LAST:event_buttont4ActionPerformed

    private void buttont5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont5ActionPerformed
                                            
   
    if (billMap.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No items in the bill!");
        return;
    }

   
    double subtotal = billMap.values().stream().mapToDouble(BillPanel::getPanelTotal).sum();
    double tax = subtotal * 0.12;
    double finalTotalAmount = subtotal + tax;

   
    String paymentType = panelPaymentCategory1.getPaymentMethod();

    try (Connection con = DBconnection.getConnection()) {
      
        con.setAutoCommit(false); //ensures all or nothing (to prevent db saving before action is completed)


        String sqlOrder = "INSERT INTO orders (TotalAmount, PaymentType) VALUES (?, ?)";
        PreparedStatement pstOrder = con.prepareStatement(sqlOrder, PreparedStatement.RETURN_GENERATED_KEYS);
        
        pstOrder.setDouble(1, finalTotalAmount);
        pstOrder.setString(2, paymentType);
        pstOrder.executeUpdate();

 
        ResultSet rs = pstOrder.getGeneratedKeys();
        int orderID = rs.next() ? rs.getInt(1) : 0;

       
        String sqlItem = "INSERT INTO order_items (OrderID, ProductID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        PreparedStatement pstItem = con.prepareStatement(sqlItem);

        for (BillPanel bill : billMap.values()) {
            pstItem.setInt(1, orderID);             
            pstItem.setInt(2, bill.getProductID());
            pstItem.setInt(3, bill.getQuantity());
            pstItem.setDouble(4, bill.getUnitPrice());
            pstItem.addBatch(); //to bundle all into one order
        }

        
        pstItem.executeBatch();
        
        
        con.commit(); //fianlly saves the order to db

        JOptionPane.showMessageDialog(this, "Payment Successful!\nPayment Method: " + paymentType);

        classes.ViewOrderDialog receipt = new classes.ViewOrderDialog(this, orderID);
        receipt.setVisible(true);
        panelPriceList2.removeAll();    
        panelPriceList2.revalidate();
        panelPriceList2.repaint();
        
        billMap.clear();               
        calculateTotal();              


        if (panelOrder != null) {
            panelOrder.loadOrders();
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error saving order: " + e.getMessage());
    }

    }//GEN-LAST:event_buttont5ActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private panels.PanelBorder PanelNav;
    private classes.PictureBoxButton btnLogout;
    private classes.Buttont buttont2;
    private classes.Buttont buttont3;
    private classes.Buttont buttont4;
    private classes.Buttont buttont5;
    private javax.swing.JPanel itemsPanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTax;
    private javax.swing.JLabel lblTotal;
    private panels.PanelBorder panelBorder1;
    private panels.PanelBorder panelBorder2;
    private panels.PanelBorder panelBorder7;
    private panels.PanelCategory panelCategory2;
    private panels.PanelBorder panelPaymentCategory;
    private panels.PanelPaymentCategory panelPaymentCategory1;
    private javax.swing.JPanel panelPriceList2;
    private classes.PictureBox pictureBox1;
    private classes.PictureBox pictureBox2;
    private classes.PictureBox pictureBox3;
    private classes.TitleBar titleBar1;
    // End of variables declaration//GEN-END:variables
}
