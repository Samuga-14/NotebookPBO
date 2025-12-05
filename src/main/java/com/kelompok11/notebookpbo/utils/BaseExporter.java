/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.utils;

import com.kelompok11.notebookpbo.model.Note;
import java.util.List;

// Syarat: ABSTRACT CLASS
public abstract class BaseExporter {
    
    // Method abstract (harus di-override sama anaknya)
    public abstract void export(List<Note> notes, String fileName);
    
    // Method biasa (bisa langsung dipake)
    protected void logSuccess(String fileName) {
        System.out.println("[FILE I/O] Sukses melakukan export data ke file: " + fileName);
    }
}
