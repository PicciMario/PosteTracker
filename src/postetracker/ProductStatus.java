/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author m.piccinelli
 */

public class ProductStatus{
    
    private Date date;
    private String status;
    
    private final SimpleDateFormat dateFormatParse = new SimpleDateFormat("dd-MMM-yyyy");
    private final SimpleDateFormat dateFormatPrint = new SimpleDateFormat("dd/MM/yyyy");
    
    public ProductStatus(){
        
    }
    
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
    
    public static ProductStatus today(){
        ProductStatus today = new ProductStatus();
        today.date = new Date();
        today.status = "Oggi";
        return today;
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
    
    public boolean isDateAvailable(){
        if (date == null) return false;
        return true;
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
    
    public int daysPassedFrom(Date start){
        
        Date end = getDate();
        if (end == null || start == null) return 0;
        if (isDateAvailable() == false) return 0;
        
        return (int)((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
        
    }

    
    
}

class ProductStatusCompareByDate implements Comparator<ProductStatus> {
    @Override
    public int compare(ProductStatus o1, ProductStatus o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}