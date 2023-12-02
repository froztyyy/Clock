/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clock;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author jcarl
 */
public class database {
     public static Connection getConnection(){
        Connection conn;
        try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clock", "root", "");
           return conn;
        } catch (Exception e) {
            
            return null;
        }
    }
}
