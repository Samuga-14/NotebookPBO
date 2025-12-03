/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.database;

import com.kelompok11.notebookpbo.model.Note;
import java.util.List;

// Kontrak Interface: Semua method ini WAJIB ada di DatabaseManager
public interface NoteDAO {
    void addNote(Note note);
    List<Note> getAllNotes();
    void updateNote(Note note);
    void deleteNote(int id);
    Note getNoteById(int id);
}
