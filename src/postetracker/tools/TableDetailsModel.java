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
    
    public ProductStatus getStatusByRow(int row){
        return data[row];
    }

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
