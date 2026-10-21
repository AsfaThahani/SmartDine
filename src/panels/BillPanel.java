package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

public class BillPanel extends JPanel {

    private int productID;
    private double unitPrice;
    
  
    private Runnable onDelete;
    private Runnable onQtyChange; 

    public BillPanel() {
        initComponents();
        setBackground(Color.WHITE);
        setOpaque(false);
        
        // Setup Spinner
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        jSpinner1.setModel(model);
       
        txtPrice.setEditable(false);
        txtPrice.setBackground(Color.WHITE);
        txtPrice.setFocusable(false);

       
        jSpinner1.addChangeListener(e -> {
            updatePriceDisplay();
           
            if (onQtyChange != null) onQtyChange.run();
        });
    }

  
    public int getProductID() { return productID; }
    
    public int getQuantity() { 
        return (int) jSpinner1.getValue(); 
    }
    
    public double getUnitPrice() { return unitPrice; }
    
    public double getPanelTotal() { 
        return getQuantity() * unitPrice; 
    }

    
    public void setOnQtyChange(Runnable event) { this.onQtyChange = event; }
    public void setOnDelete(Runnable event) { this.onDelete = event; }

    public void setData(int productID, String name, double price, Image image) {
        this.productID = productID;
        this.unitPrice = price;

      
        if (image != null) {
            pictureBox1.setImage(new ImageIcon(image));
        }
        
       
        jSpinner1.setValue(1);
        
       
        updatePriceDisplay();
    }
    
   
    public void increaseQty() {
        int current = (int) jSpinner1.getValue();
        jSpinner1.setValue(current + 1);
        
    }

    private void updatePriceDisplay() {
        int qty = (int) jSpinner1.getValue();
        double total = qty * unitPrice;
        txtPrice.setText("Rs. " + String.format("%.2f", total));
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
    }



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pictureBox1 = new classes.PictureBox();
        jSpinner1 = new javax.swing.JSpinner();
        txtPrice = new javax.swing.JTextField();
        btnRemove = new classes.Buttont();

        setBackground(new java.awt.Color(211, 211, 211));

        jSpinner1.setFont(new java.awt.Font("Afacad Flux", 1, 11)); // NOI18N

        txtPrice.setEditable(false);
        txtPrice.setBackground(new java.awt.Color(255, 255, 255));
        txtPrice.setFont(new java.awt.Font("Afacad Flux", 1, 12)); // NOI18N
        txtPrice.setBorder(null);

        btnRemove.setBackground(new java.awt.Color(255, 0, 0));
        btnRemove.setText("X");
        btnRemove.setFont(new java.awt.Font("Afacad Flux", 0, 10)); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3)
                .addComponent(jSpinner1)
                .addGap(3, 3, 3))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlusActionPerformed
       
    }//GEN-LAST:event_btnPlusActionPerformed

    private void btnPlus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlus1ActionPerformed
 
    }//GEN-LAST:event_btnPlus1ActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
     if(onDelete != null) {
            onDelete.run();
        }   // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.Buttont btnRemove;
    private javax.swing.JSpinner jSpinner1;
    private classes.PictureBox pictureBox1;
    private javax.swing.JTextField txtPrice;
    // End of variables declaration//GEN-END:variables
}
