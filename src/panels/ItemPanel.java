package panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.function.Consumer;
import javax.swing.ImageIcon;

public class ItemPanel extends javax.swing.JPanel {

    private int radius = 20; 
    
    private int productId;
    private String productName;
    private double unitPrice;
    private java.awt.Image productImage;

   
    private java.util.function.Consumer<ItemPanel> onAddClick;

    public ItemPanel() {
        initComponents();
        setOpaque(false); 
        
        
        try {
            java.awt.Font fontName = new java.awt.Font("Afacad Flux", java.awt.Font.PLAIN, 12);
            java.awt.Font fontPrice = new java.awt.Font("Afacad Flux", java.awt.Font.BOLD, 12);
            
            jLabel1.setFont(fontName);
            jLabel2.setFont(fontPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  
    public void setData(
            int productId,
            String name,
            double price,
            Image image,
            Consumer<ItemPanel> onAddClick
    ) {
        this.productId = productId;
        this.productName = name;
        this.unitPrice = price;
        this.productImage = image;
        this.onAddClick = onAddClick;

        jLabel1.setText(name);
        
      
        jLabel2.setText("Rs. " + String.format("%.2f", price));
        
        if (image != null) {
            pictureBox1.setImage(new ImageIcon(image)); 
        }
        
        btnAddItem.setText("Add");
    }

   
    public int getProductID() { return productId; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public Image getProductImage() { return productImage; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pictureBox1 = new classes.PictureBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnAddItem = new classes.Buttont();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(150, 180));
        setMinimumSize(new java.awt.Dimension(150, 180));
        setPreferredSize(new java.awt.Dimension(150, 180));

        jLabel2.setFont(new java.awt.Font("Afacad Flux", 1, 12)); // NOI18N
        jLabel2.setText("jLabel1");

        jLabel1.setText("jLabel1");

        btnAddItem.setBackground(new java.awt.Color(255, 204, 0));
        btnAddItem.setText("Add");
        btnAddItem.setFont(new java.awt.Font("Afacad Flux", 0, 14)); // NOI18N
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(btnAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed

    if (onAddClick != null) {
        onAddClick.accept(this);
    }

        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private classes.Buttont btnAddItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private classes.PictureBox pictureBox1;
    // End of variables declaration//GEN-END:variables
}
