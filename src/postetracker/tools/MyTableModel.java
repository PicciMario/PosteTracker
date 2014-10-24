/*
 * Poste Tracker
 * Copyright 2014 Mario Piccinelli <mario.piccinelli@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
    
    public Product getProductByRow(int row){
        return data[row];
    }

    public void setData(Product[] lista){
        this.data = lista;
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        if (data == null) return 0;
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0){
            String firstDate = data[row].getFirstDateString();
            return data[row].getCode() + "\n" + (firstDate == null ? "---" : firstDate);
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
