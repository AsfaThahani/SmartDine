package classes;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class TableActionCellEditor extends DefaultCellEditor {

    private OrderActionPanel action;

    public TableActionCellEditor() {
        // Use a JCheckBox as the default editor component
        super(new JCheckBox());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof OrderActionPanel) {
            action = (OrderActionPanel) value;
            action.setBackground(new java.awt.Color(239, 244, 255)); // Selection color
        }
        return action;
    }

    @Override
    public Object getCellEditorValue() {
        // IMPORTANT: Return the OrderActionPanel object itself
        return action;
    }
}