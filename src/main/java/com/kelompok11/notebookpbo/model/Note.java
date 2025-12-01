/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.model;

import java.time.LocalDateTime;

public class Note {
    private int id;
    private String title;
    private String content;
    private String category;
    private LocalDateTime deadline; // Penting buat Reminder
    
    // Constructor Kosong (Wajib buat mapping database nanti)
    public Note() {}

    // Constructor Isi
    public Note(String title, String content, String category, LocalDateTime deadline) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.deadline = deadline;
    }

    // Getter Setter (PENTING buat akses data)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}
