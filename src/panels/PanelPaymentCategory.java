package panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelPaymentCategory extends JPanel {

    private String selectedPayment = "CASH"; // Default selection
    private ImageButton btnCash;
    private ImageButton btnCard;

    public PanelPaymentCategory() {
        initComponents();
        setOpaque(false);
        setLayout(new java.awt.GridLayout(1, 0, 30, 0)); 

       
        btnCash = new ImageButton("/resources/IconCash.png", "CASH");
        btnCard = new ImageButton("/resources/IconCard.png", "CARD");

      
        btnCash.setSelected(true);

      
        btnCash.addActionListener(e -> selectCash());
        btnCard.addActionListener(e -> selectCard());

       
        add(btnCash);
        add(btnCard);
    }

  
    private void selectCash() {
        selectedPayment = "CASH";
        btnCash.setSelected(true);
        btnCard.setSelected(false);
        repaint();
    }

    private void selectCard() {
        selectedPayment = "CARD";
        btnCard.setSelected(true);
        btnCash.setSelected(false);
        repaint();
    }

   
    public String getPaymentMethod() {
        return selectedPayment;
    }

   
    private class ImageButton extends JButton {

        private Image image;
        private boolean isSelected = false; 
        private boolean isHovered = false;
        private boolean isPressed = false;

        public ImageButton(String imagePath, String name) {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(80, 60)); 

            URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                this.image = new ImageIcon(imgURL).getImage();
            } else {
                setText(name); 
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    repaint();
                }
            });
        }

        public void setSelected(boolean b) {
            this.isSelected = b;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

           
            if (isSelected) {
               
                g2.setColor(new Color(255, 245, 200)); 
            } else if (isHovered) {
                g2.setColor(new Color(245, 245, 245)); 
            } else {
                g2.setColor(Color.WHITE); 
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

           
            if (image != null) {
                int padding = 10;
                if (isPressed) {
                    int w = getWidth(); 
                    int h = getHeight();
                    int newW = (int)(w * 0.90);
                    int newH = (int)(h * 0.90);
                    int x = (w - newW) / 2;
                    int y = (h - newH) / 2;
                    g2.drawImage(image, x, y, newW, newH, null);
                } else {
                    g2.drawImage(image, padding, padding, getWidth() - (padding*2), getHeight() - (padding*2), null);
                }
            }

           
            if (isSelected) {
                
                g2.setColor(new Color(255, 204, 0)); 
                g2.setStroke(new java.awt.BasicStroke(2)); 
            } else {
                g2.setColor(new Color(220, 220, 220)); 
                g2.setStroke(new java.awt.BasicStroke(1));
            }
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
        }
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
