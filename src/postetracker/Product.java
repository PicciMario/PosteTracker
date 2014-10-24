/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author m.piccinelli
 */
public class Product {
    
    private String code = "";
    private String desc = "";
    private List<ProductStatus> statusList = new ArrayList<>();
    private int archive = 0;
    
    private final SimpleDateFormat dateFormatParse = new SimpleDateFormat("dd-MMM-yyyy");
    private final SimpleDateFormat dateFormatPrint = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Empty constructor
     */
    public Product(){};
    
    /**
     * Set the product code.
     * @param code 
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
     * Adds a status to the product statuses. It parses the passed String to
     * retrieve the date (if present).
     * Before adding the new status it checks for its existence in the statuses
     * list and, if already present, does nothing.
     * @param newStatus The new status String
     * @return True if the status was new, false otherwise.
     */
    public boolean addStatus(String newStatus){
        
        boolean already = false;
        
        ProductStatus newStat = new ProductStatus(newStatus);
        for (ProductStatus stat : statusList){
            boolean thisAlready = stat.equals(newStat);
            if (thisAlready) {
                already = true;
                break;
            }
        }
        
        if (!already){
            statusList.add(new ProductStatus(newStatus));
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
    
    public void setDesc(String desc){
        this.desc = desc;
    }
    
    public String getDesc(){
        return desc;
    }
    
    /**
     * Returns the statuses list for this product.
     * @return The statuses list for this product.
     */
    public List<ProductStatus> getStatuses(){
        return statusList;
    }
    
    public int getArchiveStatus(){
        return archive;
    }
    
    public void setArchiveStatus(int newStatus){
        archive = newStatus;
    }
    
    public boolean isArchived(){
        if (archive == 0)
            return false;
        else
            return true;
    }
    
    public void setArchived(boolean newStatus){
        if (newStatus)
            archive = 1;
        else
            archive = 0;
    }
    
    @Override
    public String toString(){
        return dateFormatPrint.format(getDate()) + " " + getCode() + " " + getDesc() + " -> " + getStatus();
    }
    
}

class ProductCompareByDate implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        if (o1.getFirstDate() == null) return -1;
        if (o2.getFirstDate() == null) return 1;
        return o1.getFirstDate().compareTo(o2.getFirstDate());
    }
}

