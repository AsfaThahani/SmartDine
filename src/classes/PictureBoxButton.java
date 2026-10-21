/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;

/**
 *
 * @author Umer Farook
 */
public class PictureBoxButton extends JLayeredPane {

    private Icon image;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public PictureBoxButton() {
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
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
        });
    }

    public Icon getImage() {
        return image;
    }

    public void setImage(Icon image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 1. Hover Background
        if (isHovered) {
            g2.setColor(new Color(245, 245, 245)); // Light Gray
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        }

        // 2. Draw Image
        if (image != null) {
            Rectangle size = getAutoSize(image);

            if (isPressed) {
                // Shrink effect
                int w = size.width;
                int h = size.height;
                int newW = (int)(w * 0.90);
                int newH = (int)(h * 0.90);
                int x = size.x + (w - newW) / 2;
                int y = size.y + (h - newH) / 2;
                
                g2.drawImage(toImage(image), x, y, newW, newH, null);
            } else {
                g2.drawImage(toImage(image), size.x, size.y, size.width, size.height, null);
            }
        }
    }

    private Rectangle getAutoSize(Icon image) {
        int w = getWidth();
        int h = getHeight();
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        
        // --- FIX IS HERE ---
        // Calculate padding dynamically. 
        // Use 15% of the button size, or 3 pixels, whichever is larger.
        // This prevents the image from disappearing on small buttons (like 30x30).
        int padding = Math.max(3, Math.min(w, h) / 6); 
        
        int paddedW = w - (padding * 2);
        int paddedH = h - (padding * 2);
        
        // Prevent negative size if button is tiny
        if (paddedW < 1) paddedW = 1;
        if (paddedH < 1) paddedH = 1;

        double xScale = (double) paddedW / iw;
        double yScale = (double) paddedH / ih;
        double scale = Math.min(xScale, yScale);

        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        int x = getWidth() / 2 - (width / 2);
        int y = getHeight() / 2 - (height / 2);
        
        return new Rectangle(new Point(x, y), new Dimension(width, height));
    }

    private Image toImage(Icon icon) {
        return ((ImageIcon) icon).getImage();
    }
}