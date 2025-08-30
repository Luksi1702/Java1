/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.model; 

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 

public class DateAdapter extends XmlAdapter<String, LocalDateTime> { 

  
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(String vt) throws Exception {
        if (vt == null || vt.trim().isEmpty()) {
            return null; 
        }
        return LocalDateTime.parse(vt, FORMATTER);
    }

    @Override
    public String marshal(LocalDateTime bt) throws Exception { 
        if (bt == null) {
            return null; 
        }
        return bt.format(FORMATTER);
    }
}
