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
    private String status = "";
    private Date date = new Date(0);
    private String desc = "";
    private List<ProductStatus> statusList = new ArrayList<>();
    
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
            return new Date(0);
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
    
    @Override
    public String toString(){
        return dateFormatPrint.format(getDate()) + " " + getCode() + " " + getDesc() + " -> " + getStatus();
    }
    
}

class ProductCompareByDate implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o1.getFirstDate().compareTo(o2.getFirstDate());
    }
}

class ProductStatusCompareByDate implements Comparator<ProductStatus> {
    @Override
    public int compare(ProductStatus o1, ProductStatus o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}

class ProductStatus{
    
    private Date date;
    private String status;
    
    private final SimpleDateFormat dateFormatParse = new SimpleDateFormat("dd-MMM-yyyy");
    private final SimpleDateFormat dateFormatPrint = new SimpleDateFormat("dd/MM/yyyy");
    
    public ProductStatus(String newStatus) {

        status = newStatus;
        
        Pattern p = Pattern.compile("[0-9]+-[A-Z]+-[0-9]+");
        Matcher m = p.matcher(newStatus);

        if (m.find()) {

            String dateString = m.group();

            try {
                date = dateFormatParse.parse(dateString);
            } catch (ParseException ex) {
                date = null;
            }

        }
        else{
            date = null;
        }
    }
    
    public Date getDate(){
        if (date == null){
            Calendar cal = Calendar.getInstance();
            cal.set(3000,0,1);
            return cal.getTime();
        }
        else{
            return date;
        }
    }
    
    public String getDateString(){
        if (date == null){
            return "---";
        }
        else{
            return dateFormatPrint.format(getDate());
        }
    }
    
    public String getStatus(){
        return status;
    }

    public boolean equals(ProductStatus stat) {
        
        if (stat.getDate().equals(date) && stat.getStatus().equals(getStatus()))
            return true;
        else
            return false;
        
    }
    

    
    
}
