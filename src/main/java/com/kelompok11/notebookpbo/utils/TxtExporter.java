/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.utils;

import com.kelompok11.notebookpbo.model.Note;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TxtExporter extends BaseExporter {

    @Override
    public void export(List<Note> notes, String fileName) {
        // Pake try-with-resources (biar file otomatis ditutup kalau udah kelar/error)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            
            writer.write("=== LAPORAN CATATAN HARIAN ===");
            writer.newLine();
            writer.write("Total Catatan: " + notes.size());
            writer.newLine();
            writer.write("==============================");
            writer.newLine();
            
            for (Note n : notes) {
                writer.write("ID       : " + n.getId());
                writer.newLine();
                writer.write("Judul    : " + n.getTitle());
                writer.newLine();
                writer.write("Kategori : " + n.getCategory());
                writer.newLine();
                writer.write("Isi      : " + n.getContent());
                writer.newLine();
                writer.write("Deadline : " + n.getDeadline());
                writer.newLine();
                writer.write("------------------------------");
                writer.newLine();
            }
            
            // Panggil method dari bapaknya (Abstract Class)
            super.logSuccess(fileName);
            
        } catch (IOException e) {
            System.err.println(">>> GAGAL EXPORT FILE: " + e.getMessage());
        }
    }
}
