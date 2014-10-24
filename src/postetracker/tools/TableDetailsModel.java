/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import postetracker.Product;
import postetracker.ProductStatus;

/**
 *
 * @author m.piccinelli
 */
public class TableDetailsModel extends AbstractTableModel {
    private final String[] columnNames = new String[]{"GG", "Data", "Status"};
    private ProductStatus[] data = null;
    Date firstDate = null;

    public void setData(Product prod){
        if (prod == null) {
            this.data = null;
            this.firstDate = null;
            return;
        }
        
        List<ProductStatus> newLista = new ArrayList<>(prod.getStatuses());
        newLista.add(ProductStatus.today());
        this.data = newLista.toArray(new ProductStatus[0]);
        
        this.firstDate = prod.getFirstDate();
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
        if (data == null) return "";
        if (col == 0){
            int days = 0;
            if (firstDate != null){
                days = data[row].daysPassedFrom(firstDate);
            }
            if (days == 0) return "";
            else return String.valueOf(days);
        }
        else if (col == 1){
            return data[row].getDateString();
        }
        else if (col == 2){
            return data[row].getStatus();
        }        
        return "";
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

}
