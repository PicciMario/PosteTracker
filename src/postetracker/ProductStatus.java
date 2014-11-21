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
    private boolean manual = false;
    
    private final SimpleDateFormat dateFormatParse = new SimpleDateFormat("dd-MMM-yyyy");
    private final SimpleDateFormat dateFormatPrint = new SimpleDateFormat("dd/MM/yyyy");
    
    public ProductStatus(){
        
    }
    
    public ProductStatus(String newStatus, Date newDate) {

        status = newStatus;
        
        // if date not passed by constructor, tries to parse it from status text
        if (newDate == null){
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
        
        // otherwise jsut use the passed date
        else {
            date = newDate;
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
    
    public void setManual(boolean newManual){
        manual = newManual;
    }
    
    public boolean isManual(){
        return manual;
    }

    
    
}

class ProductStatusCompareByDate implements Comparator<ProductStatus> {
    @Override
    public int compare(ProductStatus o1, ProductStatus o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}