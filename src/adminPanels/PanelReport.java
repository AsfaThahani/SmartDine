package adminPanels;

import classes.Buttont;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.toedter.calendar.JDateChooser;
import db.DBconnection;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelReport extends javax.swing.JPanel {

    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat viewFormat = new SimpleDateFormat("dd-MM-yyyy");

    public PanelReport() {
        initComponents();
        initCustomLogic();
    }

    private void initCustomLogic() {
       
        tableReport.setRowHeight(30);
        tableReport.getTableHeader().setFont(new java.awt.Font("Afacad Flux", java.awt.Font.BOLD, 14));
        tableReport.setFont(new Font("Afacad Flux", Font.PLAIN, 12));

        
        tableReport.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] { "Order ID", "Date", "Payment Type", "Amount" }
        ));
        
       
        tableReport.setShowGrid(false);
        tableReport.setIntercellSpacing(new Dimension(0, 0));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableReport.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

     
        setupComboBoxes();
        
        //Initialize Date Choosers to Today
        if (jDateChooser1 != null) {
            jDateChooser1.setDate(new Date());
            jDateChooser2.setDate(new Date());
        }

        // Default Selection (Monthly)
        rbtnMonthly.setSelected(true);
        CardLayout cl = (CardLayout) pnlDateInputs.getLayout();
        cl.show(pnlDateInputs, "card3"); // Show Monthly Card

     
        rbtnMonthly.addActionListener(e -> cl.show(pnlDateInputs, "card3"));
        rbtnCustom.addActionListener(e -> cl.show(pnlDateInputs, "card2"));
        
      
        btnGenerate.addActionListener(e -> generateReport());
        btnPrint.addActionListener(e -> printReport());
        
        
        lblTitleTotal.setText("Total Sales");
        lblTitleOrders.setText("Total Orders");
        lblTitleCash.setText("Cash Sales");
        lblTitleCard.setText("Card Sales");
        resetSummaryValues();

        generateReport();
    }

    private void setupComboBoxes() {
      
        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        cmbMonth.setModel(new DefaultComboBoxModel<>(months));
        
       
        Calendar cal = Calendar.getInstance();
        cmbMonth.setSelectedIndex(cal.get(Calendar.MONTH));

        // automatically creating the list of years (Current Year - 2 to Current Year + 2)
        int currentYear = cal.get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        cmbYear.setModel(new DefaultComboBoxModel<>(years));
        cmbYear.setSelectedItem(String.valueOf(currentYear));
    }

    private void generateReport() {
        String fromDate = "";
        String toDate = "";

       
        try {
            if (rbtnMonthly.isSelected()) {
                // Monthly Logic
                int monthIndex = cmbMonth.getSelectedIndex(); 
                int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
                
                Calendar cal = Calendar.getInstance();
                cal.set(year, monthIndex, 1);
                fromDate = dbFormat.format(cal.getTime()); // First day of month
                
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                toDate = dbFormat.format(cal.getTime()) + " 23:59:59"; // Last day of month
                
            } else {
                // Custom Logic
                if (jDateChooser1.getDate() != null && jDateChooser2.getDate() != null) {
                    fromDate = dbFormat.format(jDateChooser1.getDate());
                    toDate = dbFormat.format(jDateChooser2.getDate()) + " 23:59:59";
                } else {
                    return; 
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing dates: " + e.getMessage());
            return;
        }

       
        DefaultTableModel model = (DefaultTableModel) tableReport.getModel();
        model.setRowCount(0); 
        
        double totalSales = 0;
        int totalOrders = 0;
        double cashSales = 0;
        double cardSales = 0;

        String sql = "SELECT * FROM orders WHERE OrderDate >= ? AND OrderDate <= ?";

        try (Connection con = DBconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, fromDate);
            pst.setString(2, toDate);
            
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("OrderID");
                java.sql.Timestamp dbDate = rs.getTimestamp("OrderDate");
                String dateStr = (dbDate != null) ? viewFormat.format(dbDate) : "";
                
                String type = rs.getString("PaymentType");
                double amount = rs.getDouble("TotalAmount");

               
                model.addRow(new Object[]{id, dateStr, type, amount});

                
                totalSales += amount;
                totalOrders++;
                
                if (type != null) {
                    if (type.equalsIgnoreCase("Cash")) cashSales += amount;
                    else if (type.equalsIgnoreCase("Card")) cardSales += amount;
                }
            }
            
           
            lblValTotal.setText("Rs. " + String.format("%.2f", totalSales));
            lblValOrders.setText(String.valueOf(totalOrders));
            lblValCash.setText("Rs. " + String.format("%.2f", cashSales));
            lblValCard.setText("Rs. " + String.format("%.2f", cardSales));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void printReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report As");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // fonts
                com.itextpdf.text.Font fontShopName = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fontShopDetails = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fontBody = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font fontSubtitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);

                //header
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.setWidths(new int[]{1, 5}); 

               
                PdfPCell logoCell = new PdfPCell();
                try {
                    Image logo = Image.getInstance(getClass().getResource("/resources/logo.png"));
                    logo.scaleToFit(150, 120);
                    logoCell.addElement(logo);
                } catch (Exception e) {
                    logoCell.addElement(new Paragraph("LOGO"));
                }
                logoCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerTable.addCell(logoCell);

               
                PdfPCell textCell = new PdfPCell();
                textCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                Paragraph pName = new Paragraph("THE BURGER SHOP", fontShopName);
                pName.setAlignment(Element.ALIGN_CENTER);
                textCell.addElement(pName); 

                Paragraph pAddress = new Paragraph("99/1, Quarry Road, Dehiwala, Colombo | 9477-1199991 | 9477-8916404", fontShopDetails);
                pAddress.setAlignment(Element.ALIGN_CENTER);
                pAddress.setSpacingBefore(5f); 
                textCell.addElement(pAddress); 

                headerTable.addCell(textCell);
                document.add(headerTable);
                
                document.add(new Paragraph(" ")); 
                document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------"));
                document.add(new Paragraph(" ")); 

                
                Paragraph title = new Paragraph("SALES REPORT", fontTitle);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                
                String dateSubText = "";
                if (rbtnMonthly.isSelected()) {
                    String m = cmbMonth.getSelectedItem().toString();
                    String y = cmbYear.getSelectedItem().toString();
                    dateSubText = "Period: " + m + " " + y;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String d1 = (jDateChooser1.getDate() != null) ? sdf.format(jDateChooser1.getDate()) : "-";
                    String d2 = (jDateChooser2.getDate() != null) ? sdf.format(jDateChooser2.getDate()) : "-";
                    dateSubText = "Date Range: " + d1 + " to " + d2;
                }
                Paragraph subTitle = new Paragraph(dateSubText, fontSubtitle);
                subTitle.setAlignment(Element.ALIGN_CENTER);
                subTitle.setSpacingAfter(10f);
                document.add(subTitle);

                
                PdfPTable summaryTable = new PdfPTable(2);
                summaryTable.setWidthPercentage(50); 
                summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                addSummaryRow(summaryTable, "Total Sales:", lblValTotal.getText(), fontHeader, fontBody);
                addSummaryRow(summaryTable, "Total Orders:", lblValOrders.getText(), fontHeader, fontBody);
                addSummaryRow(summaryTable, "Cash Sales:", lblValCash.getText(), fontHeader, fontBody);
                addSummaryRow(summaryTable, "Card Sales:", lblValCard.getText(), fontHeader, fontBody);
                document.add(summaryTable);
                document.add(new Paragraph(" ")); 

             
                PdfPTable pdfTable = new PdfPTable(4); 
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(10f);
                float[] colWidths = {1.5f, 2f, 2f, 2f}; 
                pdfTable.setWidths(colWidths);

                String[] headers = {"Order ID", "Date", "Type", "Amount"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Paragraph(h, fontHeader));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setPadding(8);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                for (int i = 0; i < tableReport.getRowCount(); i++) {
                    String id = tableReport.getValueAt(i, 0).toString();
                    String date = tableReport.getValueAt(i, 1).toString();
                    String type = tableReport.getValueAt(i, 2).toString();
                    String amount = tableReport.getValueAt(i, 3).toString();

                    PdfPCell c1 = new PdfPCell(new Paragraph(id, fontBody));
                    PdfPCell c2 = new PdfPCell(new Paragraph(date, fontBody));
                    PdfPCell c3 = new PdfPCell(new Paragraph(type, fontBody));
                    PdfPCell c4 = new PdfPCell(new Paragraph(amount, fontBody));

                    c1.setPadding(6); c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c2.setPadding(6); c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c3.setPadding(6); c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c4.setPadding(6); c4.setHorizontalAlignment(Element.ALIGN_CENTER);

                    pdfTable.addCell(c1);
                    pdfTable.addCell(c2);
                    pdfTable.addCell(c3);
                    pdfTable.addCell(c4);
                }
                document.add(pdfTable);
                
                //footer
                Paragraph footer = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), fontBody);
                footer.setAlignment(Element.ALIGN_RIGHT);
                document.add(footer);

                document.close();
                JOptionPane.showMessageDialog(this, "Report Saved Successfully!\n" + filePath);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage());
            }
        }
    }

    private void addSummaryRow(PdfPTable table, String label, String value, com.itextpdf.text.Font f1, com.itextpdf.text.Font f2) {
        PdfPCell c1 = new PdfPCell(new Paragraph(label, f1));
        c1.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Paragraph(value, f2));
        c2.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(c2);
    }

    private void resetSummaryValues() {
        lblValTotal.setText("Rs. 0.00");
        lblValOrders.setText("0");
        lblValCash.setText("Rs. 0.00");
        lblValCard.setText("Rs. 0.00");
    }


   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReport = new javax.swing.JTable();
        pnlTop = new javax.swing.JPanel();
        pnlFilters = new javax.swing.JPanel();
        rbtnMonthly = new javax.swing.JRadioButton();
        rbtnCustom = new javax.swing.JRadioButton();
        pnlDateInputs = new javax.swing.JPanel();
        cardCustom = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        cardMonthly = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbMonth = new javax.swing.JComboBox<>();
        cmbYear = new javax.swing.JComboBox<>();
        btnGenerate = new classes.Buttont();
        btnPrint = new classes.Buttont();
        pnlSummary = new javax.swing.JPanel();
        panelBorder2 = new panels.PanelBorder();
        lblTitleTotal = new javax.swing.JLabel();
        lblValTotal = new javax.swing.JLabel();
        panelBorder4 = new panels.PanelBorder();
        lblValOrders = new javax.swing.JLabel();
        lblTitleOrders = new javax.swing.JLabel();
        panelBorder3 = new panels.PanelBorder();
        lblValCash = new javax.swing.JLabel();
        lblTitleCash = new javax.swing.JLabel();
        panelBorder1 = new panels.PanelBorder();
        lblValCard = new javax.swing.JLabel();
        lblTitleCard = new javax.swing.JLabel();

        setBackground(new java.awt.Color(211, 211, 211));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(211, 211, 211));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 220));

        tableReport.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableReport);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlTop.setBackground(new java.awt.Color(211, 211, 211));
        pnlTop.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 20, 1));
        pnlTop.setMinimumSize(new java.awt.Dimension(845, 200));
        pnlTop.setPreferredSize(new java.awt.Dimension(0, 220));
        pnlTop.setLayout(new javax.swing.BoxLayout(pnlTop, javax.swing.BoxLayout.Y_AXIS));

        pnlFilters.setBackground(new java.awt.Color(211, 211, 211));
        pnlFilters.setMinimumSize(new java.awt.Dimension(845, 66));
        pnlFilters.setPreferredSize(new java.awt.Dimension(0, 220));

        rbtnMonthly.setBackground(new java.awt.Color(211, 211, 211));
        buttonGroup1.add(rbtnMonthly);
        rbtnMonthly.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        rbtnMonthly.setText("Monthly");

        rbtnCustom.setBackground(new java.awt.Color(211, 211, 211));
        buttonGroup1.add(rbtnCustom);
        rbtnCustom.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        rbtnCustom.setText("Custom");

        pnlDateInputs.setLayout(new java.awt.CardLayout());

        cardCustom.setBackground(new java.awt.Color(211, 211, 211));

        jLabel1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel1.setText("From");

        jLabel2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel2.setText("To");

        jDateChooser1.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        jDateChooser2.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N

        javax.swing.GroupLayout cardCustomLayout = new javax.swing.GroupLayout(cardCustom);
        cardCustom.setLayout(cardCustomLayout);
        cardCustomLayout.setHorizontalGroup(
            cardCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardCustomLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addContainerGap())
        );
        cardCustomLayout.setVerticalGroup(
            cardCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardCustomLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(cardCustomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pnlDateInputs.add(cardCustom, "card2");

        cardMonthly.setBackground(new java.awt.Color(211, 211, 211));

        jLabel3.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel3.setText("Month");

        jLabel4.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        jLabel4.setText("Year");

        cmbMonth.setEditable(true);
        cmbMonth.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMonth.setOpaque(true);

        cmbYear.setEditable(true);
        cmbYear.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        cmbYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbYear.setOpaque(true);

        javax.swing.GroupLayout cardMonthlyLayout = new javax.swing.GroupLayout(cardMonthly);
        cardMonthly.setLayout(cardMonthlyLayout);
        cardMonthlyLayout.setHorizontalGroup(
            cardMonthlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardMonthlyLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbYear, 0, 103, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        cardMonthlyLayout.setVerticalGroup(
            cardMonthlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardMonthlyLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(cardMonthlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cmbYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        pnlDateInputs.add(cardMonthly, "card3");

        btnGenerate.setBackground(new java.awt.Color(255, 204, 0));
        btnGenerate.setText("Search");
        btnGenerate.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        btnGenerate.setPreferredSize(new java.awt.Dimension(90, 30));
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        btnPrint.setBackground(new java.awt.Color(255, 204, 0));
        btnPrint.setText("Print");
        btnPrint.setFont(new java.awt.Font("Afacad Flux", 0, 16)); // NOI18N
        btnPrint.setPreferredSize(new java.awt.Dimension(90, 30));

        javax.swing.GroupLayout pnlFiltersLayout = new javax.swing.GroupLayout(pnlFilters);
        pnlFilters.setLayout(pnlFiltersLayout);
        pnlFiltersLayout.setHorizontalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(rbtnMonthly, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtnCustom, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(pnlDateInputs, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        pnlFiltersLayout.setVerticalGroup(
            pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltersLayout.createSequentialGroup()
                .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlDateInputs, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlFiltersLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(pnlFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtnMonthly)
                            .addComponent(rbtnCustom))))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pnlTop.add(pnlFilters);

        pnlSummary.setBackground(new java.awt.Color(211, 211, 211));
        pnlSummary.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        pnlSummary.setMinimumSize(new java.awt.Dimension(845, 100));
        pnlSummary.setPreferredSize(new java.awt.Dimension(0, 220));
        pnlSummary.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        panelBorder2.setBackground(new java.awt.Color(148, 180, 193));

        lblTitleTotal.setFont(new java.awt.Font("Afacad Flux", 0, 18)); // NOI18N
        lblTitleTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleTotal.setText("jLabel5");

        lblValTotal.setFont(new java.awt.Font("Afacad Flux", 0, 24)); // NOI18N
        lblValTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblValTotal.setText("jLabel6");

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitleTotal))
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(lblValTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleTotal)
                .addGap(18, 18, 18)
                .addComponent(lblValTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlSummary.add(panelBorder2);

        panelBorder4.setBackground(new java.awt.Color(100, 141, 179));

        lblValOrders.setFont(new java.awt.Font("Afacad Flux", 0, 24)); // NOI18N
        lblValOrders.setForeground(new java.awt.Color(255, 255, 255));
        lblValOrders.setText("jLabel6");

        lblTitleOrders.setFont(new java.awt.Font("Afacad Flux", 0, 18)); // NOI18N
        lblTitleOrders.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleOrders.setText("jLabel5");

        javax.swing.GroupLayout panelBorder4Layout = new javax.swing.GroupLayout(panelBorder4);
        panelBorder4.setLayout(panelBorder4Layout);
        panelBorder4Layout.setHorizontalGroup(
            panelBorder4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleOrders)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder4Layout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addComponent(lblValOrders)
                .addGap(46, 46, 46))
        );
        panelBorder4Layout.setVerticalGroup(
            panelBorder4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleOrders)
                .addGap(18, 18, 18)
                .addComponent(lblValOrders, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlSummary.add(panelBorder4);

        panelBorder3.setBackground(new java.awt.Color(84, 89, 172));

        lblValCash.setFont(new java.awt.Font("Afacad Flux", 0, 24)); // NOI18N
        lblValCash.setForeground(new java.awt.Color(255, 255, 255));
        lblValCash.setText("jLabel6");

        lblTitleCash.setFont(new java.awt.Font("Afacad Flux", 0, 18)); // NOI18N
        lblTitleCash.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCash.setText("jLabel5");

        javax.swing.GroupLayout panelBorder3Layout = new javax.swing.GroupLayout(panelBorder3);
        panelBorder3.setLayout(panelBorder3Layout);
        panelBorder3Layout.setHorizontalGroup(
            panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTitleCash)
                .addContainerGap(130, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblValCash, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        panelBorder3Layout.setVerticalGroup(
            panelBorder3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleCash)
                .addGap(18, 18, 18)
                .addComponent(lblValCash, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlSummary.add(panelBorder3);

        panelBorder1.setBackground(new java.awt.Color(82, 53, 123));

        lblValCard.setFont(new java.awt.Font("Afacad Flux", 0, 24)); // NOI18N
        lblValCard.setForeground(new java.awt.Color(255, 255, 255));
        lblValCard.setText("jLabel6");

        lblTitleCard.setFont(new java.awt.Font("Afacad Flux", 0, 18)); // NOI18N
        lblTitleCard.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCard.setText("jLabel5");

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitleCard))
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(lblValCard, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleCard)
                .addGap(18, 18, 18)
                .addComponent(lblValCard, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlSummary.add(panelBorder1);

        pnlTop.add(pnlSummary);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.Buttont btnGenerate;
    private classes.Buttont btnPrint;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel cardCustom;
    private javax.swing.JPanel cardMonthly;
    private javax.swing.JComboBox<String> cmbMonth;
    private javax.swing.JComboBox<String> cmbYear;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTitleCard;
    private javax.swing.JLabel lblTitleCash;
    private javax.swing.JLabel lblTitleOrders;
    private javax.swing.JLabel lblTitleTotal;
    private javax.swing.JLabel lblValCard;
    private javax.swing.JLabel lblValCash;
    private javax.swing.JLabel lblValOrders;
    private javax.swing.JLabel lblValTotal;
    private panels.PanelBorder panelBorder1;
    private panels.PanelBorder panelBorder2;
    private panels.PanelBorder panelBorder3;
    private panels.PanelBorder panelBorder4;
    private javax.swing.JPanel pnlDateInputs;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlSummary;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JRadioButton rbtnCustom;
    private javax.swing.JRadioButton rbtnMonthly;
    private javax.swing.JTable tableReport;
    // End of variables declaration//GEN-END:variables
}
