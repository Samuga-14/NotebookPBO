/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.service;

import com.kelompok11.notebookpbo.database.NoteDAO;
import com.kelompok11.notebookpbo.model.Note;
import java.util.List;

public class NoteManager {
    private final NoteDAO noteDAO; // Kita ngomong sama Interface, bukan class langsung (Loose Coupling)

    // Constructor Injection (Disuntik DatabaseManager pas bikin objek ini)
    public NoteManager(NoteDAO noteDAO) {
        this.noteDAO = noteDAO;
    }

    public void addNote(String title, String content, String category, java.time.LocalDateTime deadline) {
        // Disini bisa validasi dulu, misal: Judul gak boleh kosong
        if (title == null || title.isEmpty()) {
            System.out.println("Judul catatan tidak boleh kosong!");
            return;
        }
        
        Note newNote = new Note(title, content, category, deadline);
        noteDAO.addNote(newNote); // Oper ke tukang database
    }

    public List<Note> getNotes() {
        return noteDAO.getAllNotes();
    }
    
    // Nanti kita tambah method update/delete/export disini// Method buat Edit
    public void updateNote(int id, String title, String content, String category, java.time.LocalDateTime deadline) {
        Note note = new Note(title, content, category, deadline);
        note.setId(id); // Penting! Kita harus kasih tau ID mana yang mau diedit
        noteDAO.updateNote(note);
    }

    // Method buat Hapus
    public void deleteNote(int id) {
        noteDAO.deleteNote(id);
    }
    
    // Method buat validasi ID ada atau nggak
    public Note getNoteById(int id) {
        Note note = noteDAO.getNoteById(id);
        
        // INI LOGIC THROW EXCEPTION-NYA
        if (note == null) {
            throw new IllegalArgumentException("ID " + id + " tidak ditemukan di database!");
        }
        
        return note;
    }
}