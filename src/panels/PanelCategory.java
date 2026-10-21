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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelCategory extends JPanel {

    private Consumer<String> categoryListener;
    private List<ImageButton> buttons = new ArrayList<>(); // To track all buttons

    public PanelCategory() {
        initComponents();
        setOpaque(false);
        setLayout(new java.awt.GridLayout(1, 0, 20, 0)); 

       
        addButton("/resources/IconBurger.png", "Burgers");
        addButton("/resources/IconDessert.png", "Desserts");
        addButton("/resources/IconDrink.png", "Drinks");
        
        
        if (!buttons.isEmpty()) {
            selectButton(buttons.get(0));
        }
    }
    
    
    private void addButton(String iconPath, String name) {
        ImageButton btn = new ImageButton(iconPath, name);
        
        
        btn.addActionListener(e -> {
            selectButton(btn); 
            if (categoryListener != null) {
                categoryListener.accept(name); 
            }
        });
        
        buttons.add(btn); 
        add(btn);        
    }
    
  
    private void selectButton(ImageButton target) {
        for (ImageButton btn : buttons) {
            btn.setSelected(btn == target);
        }
    }

    public void setCategoryListener(Consumer<String> listener) {
        this.categoryListener = listener;
    }

  
    private class ImageButton extends JButton {

        private Image image;
        private String categoryName;
        private boolean isSelected = false; // Selection State
        private boolean isPressed = false;
        private boolean isHovered = false;

        public ImageButton(String imagePath, String name) {
            this.categoryName = name;
            
            setContentAreaFilled(false);
            setBorderPainted(false); 
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(80, 80));

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
                if (isPressed) {
                    int w = getWidth();
                    int h = getHeight();
                    int newW = (int)(w * 0.90);
                    int newH = (int)(h * 0.90);
                    int x = (w - newW) / 2;
                    int y = (h - newH) / 2;
                    g2.drawImage(image, x, y, newW, newH, null);
                } else {
                    int padding = 15; 
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

        setBackground(new java.awt.Color(102, 102, 102));
        setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 92, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
