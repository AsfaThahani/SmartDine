package classes;

import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class OrderActionPanel extends JPanel {

   
    public OrderActionPanel(int orderId, ActionListener viewEvent) {
        initComponents();
        setBackground(Color.WHITE);
       
        cmdView.addActionListener(viewEvent);
    }

    private void initComponents() {
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        cmdView = new JButton("View");

       
        styleButton(cmdView, new Color(220, 180, 0));
        add(cmdView);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    }

    private JButton cmdView;
}