package classes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Table extends JTable {

    public Table() {
        setShowHorizontalLines(true);
        setGridColor(new Color(230, 230, 230));
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);
        
        // Transparent background so the rounded corners show
        setOpaque(false); 
        setBackground(Color.WHITE);

        // 1. Header Styling
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean selected, boolean focus, int row, int column) {
                TableHeader header = new TableHeader(o + ""); 
                if (column == 0 || column == 2) {
                    header.setHorizontalAlignment(JLabel.LEFT);
                } else {
                    header.setHorizontalAlignment(JLabel.LEFT);
                }
                return header;
            }
        });

        // 2. Cell Styling
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                comp.setForeground(new Color(50, 50, 50));
                
                if (isSelected) {
                    comp.setBackground(new Color(239, 244, 255));
                } else {
                    comp.setBackground(Color.WHITE);
                }
                
                // Add padding so text isn't stuck to the edge
                setBorder(new EmptyBorder(0, 10, 0, 10));
                
                if (column == 0 || column == 2) {
                    setHorizontalAlignment(JLabel.LEFT);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                return comp;
            }
        });
    }

    // 🔹 CALL THIS METHOD IN YOUR PANEL TO APPLY THE ROUNDED LOOK
    public void fixTable(JScrollPane scroll) {
        scroll.getViewport().setBackground(Color.WHITE);
        
        // Match this color to your main panel background (PanelOrder background)
        // If your main background is gray (242, 242, 242), use that here.
        scroll.setBackground(new Color(242, 242, 242)); 
        
        // Remove default borders
        scroll.setBorder(null);
        scroll.setViewportBorder(null);

        // Apply our custom Rounded Border (Radius = 20)
        scroll.setBorder(new RoundedBorder(20));

        // Fix corner
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
    }

    public void addRow(Object[] row) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.addRow(row);
    }

    // -------------------------------------------------------------------------
    // INTERNAL CLASSES FOR STYLING
    // -------------------------------------------------------------------------

    // Custom Header to paint simple clean look
    private class TableHeader extends JLabel {
        public TableHeader(String text) {
            super(text);
            setOpaque(false); 
            setFont(new java.awt.Font("sansserif", 1, 12));
            setForeground(new Color(102, 102, 102));
            setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint Header Background
            g2.setColor(new Color(245, 245, 245)); 
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0); 
            
            // Draw bottom line
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            
            super.paintComponent(g);
        }
    }

    // Custom Border that creates the "Rounded Card" effect
    private static class RoundedBorder extends EmptyBorder {
        private final int radius;
        private final Color cardColor = Color.WHITE;

        public RoundedBorder(int radius) {
            super(10, 10, 10, 10); // Padding around the table
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint the white rounded rectangle background
            g2.setColor(cardColor);
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            
            g2.dispose();
        }
    }
}