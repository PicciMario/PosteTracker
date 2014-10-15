/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker.tools;

import javax.swing.table.AbstractTableModel;
import postetracker.Product;

/**
 *
 * @author m.piccinelli
 */
public class MyTableModel extends AbstractTableModel {
    private final String[] columnNames = new String[]{"Codice", "Descrizione/stato"};
    private Product[] data = new Product[]{};

    public void setData(Product[] lista){
        this.data = lista;
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0){
            return data[row].getCode() + "\n" + data[row].getFirstDateString();
        }
        else if (col == 1){
            return data[row].getDesc() + "\n" + data[row].getStatus();
        }
        return "";
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }


    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
//    @Override
//    public boolean isCellEditable(int row, int col) {
//        //Note that the data/cell address is constant,
//        //no matter where the cell appears onscreen.
//        if (col < 2) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
//    public void setValueAt(Object value, int row, int col) {
//        data[row][col] = value;
//        fireTableCellUpdated(row, col);
//    }

}
