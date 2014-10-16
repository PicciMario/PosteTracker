/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import postetracker.Product;

/**
 * SQLite3 DB manager for Poste Tracker.
 * @author m.piccinelli
 */
public class DBManager {
    
    Connection conn = null;
    final String filename = "postetracker.db";

    public DBManager() {
        
        File f = new File(filename);
        boolean exist = f.exists();
        
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
            if (!exist) initStructure();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
        }
            
    }
    
    final void initStructure(){
        try{
            try (Statement stmt = conn.createStatement()) {
                String sql;
                sql = "CREATE TABLE products ("
                        + " code TEXT PRIMARY KEY NOT NULL,"
                        + " desc TEXT NOT NULL,"
                        + " archivestatus INTEGER DEFAULT 0"
                        + ");";
                stmt.executeUpdate(sql);
                sql = "CREATE TABLE statuses ("
                        + " code TEXT NOT NULL,"
                        + " status TEXT NOT NULL"
                        + ");";
                stmt.executeUpdate(sql);                
            }
        } catch (SQLException e){
            System.out.println(e.toString());
        }
    }
    
    public final List<Product> retrieveProductList(){
        
        List<Product> productList = new ArrayList<>();
        
        try{
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM products;");
                while (rs.next()) {
                    Product prod = new Product();
                    prod.setCode(rs.getString("code"));
                    prod.setDesc(rs.getString("desc"));
                    prod.setArchiveStatus(rs.getInt("archivestatus"));
                    productList.add(prod);
                }
            }
        } 
        catch (SQLException e){
            System.out.println(e.toString());
        }
        
        for (Product prod : productList){
            try{
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM statuses WHERE code=?");
                stmt.setString(1, prod.getCode());
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    prod.addStatus(rs.getString("status"));
                }
                
            }
            catch (SQLException e){
                System.out.println(e.toString());
            }
        }
        
        return productList;
    }
    
    public void storeNewStatus(String code, String status){
        try{
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO statuses(code, status) VALUES (?,?);");
            stmt.setString(1, code);
            stmt.setString(2, status);
            stmt.execute();
        }
        catch (SQLException e){
            System.out.println(e.toString());
        }        
    }
    
    public int storeNewProduct(Product product){
        
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT count(*) FROM products WHERE code=?;");
            stmt.setString(1, product.getCode());
            ResultSet rs = stmt.executeQuery();
            if (rs.getInt(1) > 0){
                stmt.close();
                return 2;
            }
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e.toString());
            return 1;
        }
        
        try{
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO products(code, desc) VALUES (?,?);");
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getDesc());
            stmt.execute();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e.toString());
            return 1;
        }  
        
        return 0;
    }
    
    public void deleteProduct(Product product){
        try{
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE code=?;");
            stmt.setString(1, product.getCode());
            stmt.execute();
            
            stmt = conn.prepareStatement("DELETE FROM statuses WHERE code=?;");
            stmt.setString(1, product.getCode());
            stmt.execute();            
            
            conn.commit();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e.toString());
        }         
    }
    
    public void archiveProduct(Product product){
        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT archivestatus FROM products WHERE code = ?;");
            stmt.setString(1, product.getCode());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()){
                int actualstatus = rs.getInt("archivestatus");
                int newstatus = 0;
                if (actualstatus == 0){
                    newstatus = 1;
                }
                else {
                    newstatus = 0;
                }
                
                stmt = conn.prepareStatement("UPDATE products SET archivestatus=? WHERE code=?;");
                stmt.setInt(1, newstatus);
                stmt.setString(2, product.getCode());
                stmt.executeUpdate();
            }

            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e.toString());
        }          
    }
    
}
