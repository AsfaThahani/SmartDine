package classes;

import db.DBconnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants; 
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ViewOrderDialog extends JDialog {

    private final int orderId;
    private JTable table;
    private JLabel lbSubtotal, lbTax, lbTotal;
    
    
    private JLabel lblShopName, lblAddress, lblPhone, lblFooter;
    
    
    private classes.Buttont btnPrint; 

    public ViewOrderDialog(JFrame parent, int orderId) {
        super(parent, "Order Receipt", true);
        this.orderId = orderId;
        
        setSize(400, 720); 
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        initComponents();
        loadShopDetails(); 
        loadOrderItems();  
    }

    private void initComponents() {
      
        lblShopName = new JLabel("SHOP NAME");
        lblShopName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblShopName.setHorizontalAlignment(SwingConstants.CENTER);
        lblShopName.setBounds(10, 10, 365, 30);
        add(lblShopName);

        lblAddress = new JLabel("Address Line Here");
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAddress.setHorizontalAlignment(SwingConstants.CENTER);
        lblAddress.setBounds(10, 40, 365, 20);
        add(lblAddress);

        lblPhone = new JLabel("Tel: 000-0000000");
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhone.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhone.setBounds(10, 60, 365, 20);
        add(lblPhone);
        
        javax.swing.JSeparator headerSep = new javax.swing.JSeparator();
        headerSep.setBounds(20, 90, 345, 10);
        add(headerSep);

        
        JLabel lbTitle = new JLabel("Order #" + orderId);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbTitle.setForeground(new Color(50, 50, 50));
        lbTitle.setHorizontalAlignment(SwingConstants.LEFT);
        lbTitle.setBounds(20, 100, 200, 20); 
        add(lbTitle);

      
        table = new JTable();
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setEnabled(false); // Read-only
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setOpaque(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 130, 345, 300); 
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new EmptyBorder(0,0,0,0));
        add(scroll);

      
        int startY = 450; 
        int labelWidth = 100;
        int valueWidth = 120;
        int xLabel = 140;
        int xValue = 240;

     
        JLabel txtSub = new JLabel("Subtotal:");
        txtSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSub.setHorizontalAlignment(JLabel.RIGHT);
        txtSub.setBounds(xLabel, startY, labelWidth, 25);
        add(txtSub);

        lbSubtotal = new JLabel("Rs 0.00");
        lbSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbSubtotal.setHorizontalAlignment(JLabel.RIGHT);
        lbSubtotal.setBounds(xValue, startY, valueWidth, 25);
        add(lbSubtotal);

 
        JLabel txtTax = new JLabel("Tax (12%):");
        txtTax.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTax.setHorizontalAlignment(JLabel.RIGHT);
        txtTax.setBounds(xLabel, startY + 25, labelWidth, 25);
        add(txtTax);

        lbTax = new JLabel("Rs 0.00");
        lbTax.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbTax.setHorizontalAlignment(JLabel.RIGHT);
        lbTax.setBounds(xValue, startY + 25, valueWidth, 25);
        add(lbTax);

       
        javax.swing.JSeparator sep = new javax.swing.JSeparator();
        sep.setBounds(xLabel + 20, startY + 55, 200, 10);
        add(sep);

    
        JLabel txtTot = new JLabel("Total:");
        txtTot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtTot.setHorizontalAlignment(JLabel.RIGHT);
        txtTot.setBounds(xLabel, startY + 60, labelWidth, 30);
        add(txtTot);

      
        lbTotal = new JLabel("Rs 0.00");
        lbTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        lbTotal.setForeground(Color.BLACK); 
        lbTotal.setHorizontalAlignment(JLabel.RIGHT);
        lbTotal.setBounds(xValue, startY + 60, valueWidth, 30);
        add(lbTotal);
        
    
        lblFooter = new JLabel("Thank you! Come Again.");
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBounds(20, startY + 110, 345, 20);
        add(lblFooter);

     
        btnPrint = new classes.Buttont();
        btnPrint.setText("Print Receipt");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        

        btnPrint.setBackground(new Color(255, 204, 0)); 
        btnPrint.setForeground(Color.BLACK);            
        
        
        btnPrint.setHoverColor(new Color(255, 225, 100)); 
        btnPrint.setRound(35);
        
        btnPrint.setBounds(100, startY + 150, 200, 35); 
       
        btnPrint.addActionListener(e -> printReceipt());
        
        add(btnPrint);
    }
    
  
    private void loadShopDetails() {
        try (Connection con = DBconnection.getConnection()) {
            String sql = "SELECT ShopName, Address, Phone, FooterMessage FROM shop_details WHERE ShopID = 1";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String name = rs.getString("ShopName");
                String addr = rs.getString("Address");
                String phone = rs.getString("Phone");
                String footer = rs.getString("FooterMessage");

                lblShopName.setText(name != null ? name.toUpperCase() : "SHOP NAME");
                lblAddress.setText(addr != null ? addr : "");
                lblPhone.setText(phone != null ? "Tel: " + phone : "");
                lblFooter.setText(footer != null ? footer : "Thank you!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    private void loadOrderItems() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Item", "Qty", "Price"}, 0);
        double calculatedSubtotal = 0;

        try {
            Connection con = DBconnection.getConnection();
            String sql = "SELECT p.ProductName, d.Quantity, d.UnitPrice " +
                         "FROM order_items d " +
                         "JOIN products p ON d.ProductID = p.ProductID " +
                         "WHERE d.OrderID = ?";
            
            PreparedStatement p = con.prepareStatement(sql);
            p.setInt(1, orderId);
            ResultSet rs = p.executeQuery();

            while (rs.next()) {
                String name = rs.getString("ProductName");
                int qty = rs.getInt("Quantity");
                double price = rs.getDouble("UnitPrice");
                
                double rowTotal = price * qty;
                calculatedSubtotal += rowTotal;
                
                model.addRow(new Object[]{name, qty, "Rs " + String.format("%.2f", price)});
            }
            
            table.setModel(model);
            
            table.getColumnModel().getColumn(0).setPreferredWidth(180);
            table.getColumnModel().getColumn(1).setPreferredWidth(50); 
            table.getColumnModel().getColumn(2).setPreferredWidth(80); 

            double tax = calculatedSubtotal * 0.12;
            double finalTotal = calculatedSubtotal + tax;

            lbSubtotal.setText("Rs " + String.format("%.2f", calculatedSubtotal));
            lbTax.setText("Rs " + String.format("%.2f", tax));
            lbTotal.setText("Rs " + String.format("%.2f", finalTotal));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private void printReceipt() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print Receipt Order #" + orderId);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                g2.scale(0.8, 0.8); 

               
                ViewOrderDialog.this.getContentPane().printAll(g2);

                return Printable.PAGE_EXISTS;
            }
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Printing Failed: " + e.getMessage());
            }
        }
    }
}