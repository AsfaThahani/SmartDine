package forms;

import adminPanels.*; 
import db.DBconnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

public class AdminMain extends javax.swing.JFrame {

    private String username;
    private CardLayout cardLayout;
    private JPanel bodyPanel;
    private JLabel lblIncome, lblOrders, lblProducts;
    private JTable tableRecent;
    private DefaultPieDataset pieDataset;

    public AdminMain(String username) {
        setUndecorated(true); 
        setBackground(new Color(0, 0, 0, 0)); 
        this.username = username;
        initComponents();
        initCustomComponents();       
    }

    private void initCustomComponents() {
        titleBar1.initJFram(this);

      
        try {
            ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/resources/IconLogout.png"));
            btnLogout.setImage(logoutIcon);
        } catch (Exception e) {
            System.err.println("Error loading logout image: " + e.getMessage());
        }

        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int confirm = JOptionPane.showConfirmDialog(
                    AdminMain.this, 
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

        jLayeredPane1.setLayout(new BorderLayout());

        jPanel1.removeAll();
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(titleBar1, BorderLayout.LINE_END);
        jLayeredPane1.add(topWrapper, BorderLayout.PAGE_START);

        cardLayout = new CardLayout();
        bodyPanel = new JPanel(cardLayout);
        bodyPanel.setOpaque(false);
        jLayeredPane1.add(bodyPanel, BorderLayout.CENTER);

        buildDashboardUI();

        bodyPanel.add(jPanel1, "DASHBOARD");
        bodyPanel.add(new PanelMenuManagement(), "MENU");
        bodyPanel.add(new PanelReport(), "REPORT");
        bodyPanel.add(new PanelAdminSettings(this.username), "SETTINGS");

        loadDashboardData();
        cardLayout.show(bodyPanel, "DASHBOARD");
    }

    private void buildDashboardUI() {
        jPanel1.setLayout(new BorderLayout(20, 20)); 
        jPanel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel1.setOpaque(false);

     //cards
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 160));

        pnlCards.add(createCard("Today's Income", new Color(100, 141, 179), "Rs. 0.0", 1));
        pnlCards.add(createCard("Today's Orders", new Color(84, 89, 172), "0", 2));
        pnlCards.add(createCard("Menu Items", new Color(82, 53, 123), "0", 3));

        jPanel1.add(pnlCards, BorderLayout.NORTH);

      
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCenter.setOpaque(false);

      //recent order tbl
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setOpaque(false);

        JLabel lblTbl = new JLabel("Recent Orders");
        lblTbl.setFont(new Font("Afacad Flux", Font.BOLD, 18));
        pnlTable.add(lblTbl, BorderLayout.NORTH);

        tableRecent = new JTable();
        tableRecent.setRowHeight(30);
        tableRecent.setRowSelectionAllowed(false);
        tableRecent.setFocusable(false);

        tableRecent.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Order ID", "Time", "Total", "Method"}
        ) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        });

        tableRecent.setFont(new Font("Afacad Flux", Font.PLAIN, 12));
        tableRecent.getTableHeader().setFont(new Font("Afacad Flux", Font.BOLD, 14));
        
        tableRecent.setShowGrid(false);
        tableRecent.setIntercellSpacing(new Dimension(0, 0));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableRecent.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(tableRecent);
        pnlTable.add(scroll, BorderLayout.CENTER);
        pnlCenter.add(pnlTable);

      //piechart
        JPanel pnlChart = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); // White Background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded Corners
            }
        };
        pnlChart.setOpaque(false);
        
        pnlChart.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        JLabel lblChart = new JLabel("Payment Distribution", SwingConstants.CENTER);
        lblChart.setFont(new Font("Afacad Flux", Font.BOLD, 18));
        pnlChart.add(lblChart, BorderLayout.NORTH);

        pnlChart.add(createChartPanel(), BorderLayout.CENTER);
        pnlCenter.add(pnlChart);

        jPanel1.add(pnlCenter, BorderLayout.CENTER);
    }
    
    private JPanel createCard(String title, Color color, String value, int type) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };

        card.setOpaque(false);
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Afacad Flux", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Afacad Flux", Font.BOLD, 32));
        lblValue.setForeground(Color.WHITE);
      
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);
        if (type == 1) lblIncome = lblValue;
        if (type == 2) lblOrders = lblValue;
        if (type == 3) lblProducts = lblValue;

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private ChartPanel createChartPanel() {
        pieDataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createRingChart("", pieDataset, true, true, false);

        chart.setBackgroundPaint(null);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setSectionDepth(0.35);
        plot.setLabelGenerator(null);
        plot.setSectionPaint("Cash", new Color(196, 123, 228));
        plot.setSectionPaint("Card", new Color(109, 195, 187));

        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);
        return cp;
    }

   private void loadDashboardData() {
    try (Connection con = DBconnection.getConnection();
         Statement st = con.createStatement()) {

        // today's income
        ResultSet rs1 = st.executeQuery("SELECT SUM(TotalAmount) FROM orders WHERE DATE(OrderDate)=CURDATE()");
        if (rs1.next()) {
            double income = rs1.getObject(1) != null ? rs1.getDouble(1) : 0.0;
            lblIncome.setText("Rs. " + String.format("%.2f", income));
        }

        // today's order
        ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM orders WHERE DATE(OrderDate)=CURDATE()");
        if (rs2.next()) lblOrders.setText(String.valueOf(rs2.getInt(1)));

        //num of menu
        ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM products");
        if (rs3.next()) lblProducts.setText(String.valueOf(rs3.getInt(1)));

        
        updateChartData(con);
        updateTableData(con);

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void updateChartData(Connection con) throws SQLException {
        int cash = 0, card = 0;
        Statement st = con.createStatement();

        ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM orders WHERE PaymentType='Cash'");
        if (rs1.next()) cash = rs1.getInt(1);

        ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM orders WHERE PaymentType='Card'");
        if (rs2.next()) card = rs2.getInt(1);

        pieDataset.setValue("Cash", cash);
        pieDataset.setValue("Card", card);
    }

    private void updateTableData(Connection con) throws SQLException {
        DefaultTableModel model = (DefaultTableModel) tableRecent.getModel();
    model.setRowCount(0);

    Statement st = con.createStatement();
    ResultSet rs = st.executeQuery(
        "SELECT * FROM orders ORDER BY OrderID DESC LIMIT 15"
    );

    while (rs.next()) {
        Timestamp ts = rs.getTimestamp("OrderDate");
        String timeOnly = new java.text.SimpleDateFormat("HH:mm").format(ts);

        
        String totalFormatted = String.format("%.2f", rs.getDouble("TotalAmount"));

        
        String paymentType = rs.getString("PaymentType");
        if (paymentType != null) {
            paymentType = paymentType.substring(0, 1).toUpperCase()
                         + paymentType.substring(1).toLowerCase();
        }

        model.addRow(new Object[]{
            rs.getInt("OrderID"),
            timeOnly,
            totalFormatted,
            paymentType
        });
    }
    }

   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new panels.PanelBorder();
        PanelNav = new panels.PanelBorder();
        buttont2 = new classes.Buttont();
        buttont3 = new classes.Buttont();
        buttont4 = new classes.Buttont();
        buttont5 = new classes.Buttont();
        btnLogout = new classes.PictureBoxButton();
        pictureBox1 = new classes.PictureBox();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        titleBar1 = new classes.TitleBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelBorder1.setBackground(new java.awt.Color(211, 211, 211));

        PanelNav.setBackground(new java.awt.Color(51, 51, 51));
        PanelNav.setPreferredSize(new java.awt.Dimension(198, 179));
        PanelNav.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttont2.setBackground(new java.awt.Color(255, 204, 0));
        buttont2.setText("Dashboard");
        buttont2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont2ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 155, 170, 28));

        buttont3.setBackground(new java.awt.Color(255, 204, 0));
        buttont3.setText("Menu Management");
        buttont3.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont3ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont3, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 236, 170, 28));

        buttont4.setBackground(new java.awt.Color(255, 204, 0));
        buttont4.setText("Report");
        buttont4.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont4ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 170, 28));

        buttont5.setBackground(new java.awt.Color(255, 204, 0));
        buttont5.setText("Settings");
        buttont5.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        buttont5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttont5ActionPerformed(evt);
            }
        });
        PanelNav.add(buttont5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 170, 28));
        PanelNav.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 500, 30, 30));

        pictureBox1.setBackground(new java.awt.Color(255, 255, 255));
        pictureBox1.setImage(new javax.swing.ImageIcon(getClass().getResource("/resources/logo.png"))); // NOI18N
        pictureBox1.setPreferredSize(new java.awt.Dimension(150, 120));
        PanelNav.add(pictureBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 160, 80));

        jLayeredPane1.setBackground(new java.awt.Color(0, 204, 204));
        jLayeredPane1.setPreferredSize(new java.awt.Dimension(831, 605));
        jLayeredPane1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(51, 255, 51));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(831, 605));
        jPanel1.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(770, 770, 770)
                .addComponent(titleBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(titleBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(572, 572, 572))
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
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(PanelNav, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(15, 15, 15))))
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

    private void buttont2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont2ActionPerformed
            cardLayout.show(bodyPanel, "DASHBOARD");     // TODO add your handling code here:
    }//GEN-LAST:event_buttont2ActionPerformed

    private void buttont3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont3ActionPerformed
    cardLayout.show(bodyPanel, "MENU");      // TODO add your handling code here:
    }//GEN-LAST:event_buttont3ActionPerformed

    private void buttont4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont4ActionPerformed
cardLayout.show(bodyPanel, "REPORT");         
              // TODO add your handling code here:
    }//GEN-LAST:event_buttont4ActionPerformed

    private void buttont5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttont5ActionPerformed
cardLayout.show(bodyPanel, "SETTINGS");        // TODO add your handling code here:
    }//GEN-LAST:event_buttont5ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private panels.PanelBorder PanelNav;
    private classes.PictureBoxButton btnLogout;
    private classes.Buttont buttont2;
    private classes.Buttont buttont3;
    private classes.Buttont buttont4;
    private classes.Buttont buttont5;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private panels.PanelBorder panelBorder1;
    private classes.PictureBox pictureBox1;
    private classes.TitleBar titleBar1;
    // End of variables declaration//GEN-END:variables
}
