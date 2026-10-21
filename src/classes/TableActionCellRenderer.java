package classes;

import java.awt.Component;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableActionCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof OrderActionPanel) {
            OrderActionPanel action = (OrderActionPanel) value;
            if (isSelected) {
                action.setBackground(new Color(239, 244, 255));
            } else {
                action.setBackground(Color.WHITE);
            }
            return action;
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}