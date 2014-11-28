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

package postetracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Represent a single package, with a tracking code, a description and a 
 * list of statuses.
 * @author m.piccinelli
 */
public class Product {
    
    /** The tracking code */
    private String code = "";
    
    /** A user-defined description */
    private String desc = "";
    
    /** The list of statuses (instances of {@link ProductStatus() ProductStatus()})*/
    private List<ProductStatus> statusList = new ArrayList<>();
    
    /** Archive flag (1: product archived) */
    private int archive = 0;
    
    /** product URL */
    private String url = "";
    
    private final SimpleDateFormat dateFormatParse = new SimpleDateFormat("dd-MMM-yyyy");
    private final SimpleDateFormat dateFormatPrint = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Empty constructor
     */
    public Product(){};
    
    /**
     * Set the product code.
     * @param code The new code.
     */
    public void setCode(String code){
        this.code = code;
    }
    
    /** 
     * Returns the product code.
     * @return The product code.
     */
    public String getCode(){
        return code;
    }
    
    /**
     * Set the product description. 
     * @param desc The new description.
     */    
    public void setDesc(String desc){
        this.desc = desc;
    }
    
    /** 
     * Returns the product description.
     * @return The product description.
     */    
    public String getDesc(){
        return desc;
    }    
    
    /**
     * Retrieves the archive status.
     * @return The archive status (1: archived).
     */
    public int getArchiveStatus(){
        return archive;
    }
    
    /** 
     * Sets the archive status.
     * @param newStatus The new status (1: archived).
     */
    public void setArchiveStatus(int newStatus){
        archive = newStatus;
    }
    
    /**
     * Retrieves the archive status.
     * @return True if archived, otherwise false.
     */
    public boolean isArchived(){
        if (archive == 0)
            return false;
        else
            return true;
    }
    
    /**
     * Sets the archive status.
     * @param newStatus New status (true: archived).
     */
    public void setArchived(boolean newStatus){
        if (newStatus)
            archive = 1;
        else
            archive = 0;
    }
    
    /**
     * Sets the product URL
     * @param newUrl the new URL
     */
    public void setUrl(String newUrl){
        url = newUrl;
    }
    
    /**
     * Returns the product url.
     * @return The actual product url
     */
    public String getUrl(){
        return url;
    }
    
    /**
     * Adds a status to the product statuses.
     * Before adding the new status it checks for its existence in the statuses
     * list and, if already present, does nothing.
     * @param newStat The new status
     * @return True if the status was new, false otherwise.
     */
    public boolean addStatus(ProductStatus newStat){
        
        boolean already = false;
        
        for (ProductStatus stat : statusList){
            boolean thisAlready = stat.equals(newStat);
            if (thisAlready) {
                already = true;
                break;
            }
        }
        
        if (!already){
            statusList.add(newStat);
            Collections.sort(statusList, new ProductStatusCompareByDate());
        }
        
        return !already;
    }        
    
    /**
     * Retrieves the most recent status ("unknown" if no status available).
     * @return The most recent status for the product.
     */
    public String getStatus(){
        if (statusList.size()>0){
            return statusList.get(statusList.size()-1).getStatus();
        }
        else {
            return "unknown";
        }
    }
    
    /**
     * Deletes a status.
     * @param status The status to delete.
     * @return True if status was found into product and thus removed.
     */
    public boolean deleteStatus(ProductStatus status){
        boolean removed = statusList.remove(status);
        return removed;
    }
    
    /** 
     * Retrieves the date of the most recent status (Date(0) if no status
     * available).
     * @return The date of the most recent status.
     */
    public Date getDate(){
        if (statusList.size() > 0){
            return statusList.get(statusList.size()-1).getDate();
        }
        else {
            return new Date(0);
        }
    }

    /** 
     * Retrieves the date of the oldest status (Date(0) if no status
     * available).
     * @return The date of the oldest status.
     */    
    public Date getFirstDate(){
        if (statusList.size() > 0){
            return statusList.get(0).getDate();
        }
        else {
            return null;
        }
    }    
    
    /**
     * Retrieves the date of the most recent status for this product as a 
     * String.
     * @return The date of the most recent status for this product as a String. 
     */
    public String getDateString(){
        return dateFormatPrint.format(getDate());
    }

    /**
     * Retrieves the date of the oldest status for this product as a String.
     * @return The date of the oldest status for this product as a String. 
     */
    public String getFirstDateString(){
        Date first = getFirstDate();
        if (first == null){
            return null;
        }
        return dateFormatPrint.format(getFirstDate());
    }    
    
    /**
     * Returns the statuses list for this product.
     * @return The statuses list for this product.
     */
    public List<ProductStatus> getStatuses(){
        return statusList;
    }
    
    @Override
    public String toString(){
        return dateFormatPrint.format(getDate()) + " " + getCode() + " " + getDesc() + " -> " + getStatus();
    }
    
}

/**
 * An utility class used to define the sort order of a list of Products
 * (by the first date available among their statuses, in chronological order).
 * @author m.piccinelli
 */
class ProductCompareByDate implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        
        // entrambi hanno date valide
        if (o1.getFirstDate() != null && o2.getFirstDate() != null){
            int confrontoDate = o1.getFirstDate().compareTo(o2.getFirstDate());
            if (confrontoDate != 0) 
                return confrontoDate;            
        }
        
        // uno solo dei due ha data valida
        if (o1.getFirstDate() == null && o2.getFirstDate() != null) return 1;
        if (o1.getFirstDate() != null && o2.getFirstDate() == null) return -1;

        // nessuno dei due ha data valida
        // oppure hanno la stessa data
        return o1.getDesc().compareTo(o2.getDesc());

    }
}

