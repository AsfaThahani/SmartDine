package panels;

import classes.OrderActionPanel;
import classes.TableActionCellEditor;
import classes.TableActionCellRenderer;
import classes.ViewOrderDialog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import db.DBconnection;

public class PanelOrder extends javax.swing.JPanel {

    public PanelOrder() {
        initComponents();
        setupTable();
        styleTable();
        loadOrders();
    }

    private void setupTable() {
        
        DefaultTableModel model = new DefaultTableModel(       //upgrading normal table model for custom (db)
            new Object[][]{},
            new String[]{"Order No", "Total Amount", "Payment Type", "Action"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Action column is editable
            }
        };

        jTable1.setModel(model); 

      
        if (jTable1.getColumnModel().getColumnCount() > 3) {
            jTable1.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRenderer());
            jTable1.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor());
        }
    }

    
    private void styleTable() {
       
        jTable1.fixTable(jScrollPane2); 
        
        
        jTable1.setRowHeight(45);
        jTable1.setShowGrid(false);
        jTable1.setIntercellSpacing(new Dimension(0, 0));

       
        jTable1.setSelectionBackground(new Color(239, 244, 255));
        jTable1.setSelectionForeground(Color.BLACK);

       
        jTable1.setFont(new Font("Afacad Flux", Font.PLAIN, 15));
        jTable1.getTableHeader().setFont(new Font("Afacad Flux", Font.BOLD, 15));

        
    }
    
    
    public void loadOrders() {  //for tday only
        try {
            Connection con = DBconnection.getConnection(); 
            Statement st = con.createStatement();
            
            String sql = "SELECT OrderID, TotalAmount, PaymentType " +
                         "FROM orders " +
                         "WHERE DATE(OrderDate) = CURDATE() " + //currentdadte - curdate
                         "ORDER BY OrderID DESC";

            ResultSet rs = st.executeQuery(sql);

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {        //ResultSet(results of query as a list like)
                int orderId = rs.getInt("OrderID");
                double total = rs.getDouble("TotalAmount");
                String payment = rs.getString("PaymentType");
                
                String formattedTotal = "Rs. " + String.format("%.2f", total); 

                
                OrderActionPanel actionPanel = new OrderActionPanel(orderId,
                    e -> viewOrderDetails(orderId)  //a lambda expression - represents an event handler
                );

                model.addRow(new Object[]{
                    orderId,
                    formattedTotal,
                    payment,
                    actionPanel
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewOrderDetails(int orderId) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ViewOrderDialog dialog = new ViewOrderDialog(parentFrame, orderId);
        dialog.setVisible(true); 
    }

   
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new classes.Table();

        setOpaque(false);

        jScrollPane2.setBackground(new java.awt.Color(102, 102, 102));
        jScrollPane2.setOpaque(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private classes.Table jTable1;
    // End of variables declaration//GEN-END:variables
}
