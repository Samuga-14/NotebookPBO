/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.kelompok11.notebookpbo;

import com.kelompok11.notebookpbo.database.DatabaseManager;
import com.kelompok11.notebookpbo.service.NoteManager;
import com.kelompok11.notebookpbo.model.Note;
import com.kelompok11.notebookpbo.model.ReminderTask;
import com.kelompok11.notebookpbo.utils.TxtExporter;

// Ganti Scanner dengan Swing
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotebookPBO { 
    
    private static NoteManager noteManager;
    // Scanner kita pensiunkan
    
    // Satpam Thread
    private static ReminderTask reminder;

    public static void main(String[] args) {
        // 1. SETUP DATABASE
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect(); 
        
        noteManager = new NoteManager(dbManager);

        // 2. NYALAIN SATPAM
        reminder = new ReminderTask(noteManager); 
        reminder.start(); 
        
        // 3. LOOP MENU UTAMA (Versi GUI)
        boolean isRunning = true;
        
        while (isRunning) {
            // Pilihan Menu pake Tombol
            String[] options = {"Tambah", "Lihat", "Edit", "Hapus", "Export", "Keluar"};
            
            int choice = JOptionPane.showOptionDialog(
                null, 
                "Selamat Datang di Notebook PBO Kelompok 11\nSilakan pilih menu:", 
                "Menu Utama", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.PLAIN_MESSAGE, 
                null, 
                options, 
                options[0]
            );

            // Handle logic berdasarkan urutan tombol (array index)
            switch (choice) {
                case 0 -> menuAddNote();    // Tambah
                case 1 -> menuShowNotes();  // Lihat
                case 2 -> menuUpdateNote(); // Edit
                case 3 -> menuDeleteNote(); // Hapus
                case 4 -> menuExportNotes();// Export
                case 5, -1 -> {             // Keluar atau tombol X (Close)
                    JOptionPane.showMessageDialog(null, "Sampai jumpa! 👋");
                    if (reminder != null) reminder.stopReminder();
                    isRunning = false; 
                }
            }
        }
    }

    // --- FITUR 1: TAMBAH CATATAN (GUI) ---
    private static void menuAddNote() {
        // Input Judul
        String title = JOptionPane.showInputDialog(null, "Masukkan Judul Catatan:");
        if (title == null || title.trim().isEmpty()) return; // Cek kalau user klik Cancel
        
        // Input Isi
        String content = JOptionPane.showInputDialog(null, "Masukkan Isi Catatan:");
        if (content == null) content = "-"; // Default kalau kosong
        
        // Input Kategori (Pake Dropdown biar keren)
        String[] kategories = {"Tugas Kuliah", "Pribadi", "Pekerjaan", "Ide Proyek", "Lain-lain"};
        String category = (String) JOptionPane.showInputDialog(
            null, 
            "Pilih Kategori:", 
            "Kategori", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            kategories, 
            kategories[0]
        );
        if (category == null) return; // Kalau cancel
        
        // Deadline
        JOptionPane.showMessageDialog(null, "Info: Deadline otomatis diset 1 Jam dari sekarang.");
        LocalDateTime deadline = LocalDateTime.now().plusSeconds(10); 

        noteManager.addNote(title, content, category, deadline);
        JOptionPane.showMessageDialog(null, "✅ Berhasil! Catatan disimpan.");
    }

    // --- FITUR 2: LIHAT CATATAN (GUI Scrollable) ---
    private static void menuShowNotes() {
        List<Note> notes = noteManager.getNotes();
        
        if (notes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "📭 Belum ada catatan nih.");
            return;
        }

        // Kita rakit string panjang buat ditampilin
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        
        for (Note n : notes) {
            String deadlineStr = (n.getDeadline() != null) ? n.getDeadline().format(fmt) : "-";
            sb.append("[").append(n.getId()).append("] ").append(n.getTitle())
              .append(" (").append(n.getCategory()).append(")\n")
              .append("    Isi      : ").append(n.getContent()).append("\n")
              .append("    Deadline : ").append(deadlineStr).append("\n")
              .append("-------------------------\n");
        }

        // Tampilkan pake TextArea biar bisa discroll kalau panjang
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
        
        JOptionPane.showMessageDialog(null, scrollPane, "Daftar Catatan", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- FITUR 3: EDIT CATATAN (GUI) ---
    private static void menuUpdateNote() {
        if (reminder != null) reminder.pause(); // Tetap pause thread biar aman
        
        try {
            // Minta ID
            String idStr = JOptionPane.showInputDialog("Masukkan ID Catatan yang mau diedit:");
            if (idStr == null) return; // Cancel
            
            int id = Integer.parseInt(idStr);
            
            // Cek ID ada gak (Pake logic yang udah kita buat)
            Note noteLama = noteManager.getNoteById(id); 
            
            // Kalau ketemu, minta data baru
            String title = JOptionPane.showInputDialog("Judul Baru:", noteLama.getTitle());
            if (title == null) return;

            String content = JOptionPane.showInputDialog("Isi Baru:", noteLama.getContent());
            if (content == null) content = "-";
            
            // Dropdown lagi buat edit
            String[] kategories = {"Tugas Kuliah", "Pribadi", "Pekerjaan", "Ide Proyek", "Lain-lain"};
            String category = (String) JOptionPane.showInputDialog(
                null, "Pilih Kategori Baru:", "Edit Kategori", 
                JOptionPane.QUESTION_MESSAGE, null, kategories, noteLama.getCategory()
            );
            if (category == null) return;

            LocalDateTime deadline = LocalDateTime.now().plusHours(24); 
            
            noteManager.updateNote(id, title, content, category, deadline);
            JOptionPane.showMessageDialog(null, "✅ Data berhasil diupdate!");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "⚠ ID harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "❌ " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (reminder != null) reminder.resumeReminder(); // Resume
        }
    }

    // --- FITUR 4: HAPUS CATATAN (GUI) ---
    private static void menuDeleteNote() {
        if (reminder != null) reminder.pause();
        
        try {
            String idStr = JOptionPane.showInputDialog("Masukkan ID Catatan yang mau dihapus:");
            if (idStr == null) return;
            
            int id = Integer.parseInt(idStr);
            noteManager.getNoteById(id); // Cek eksistensi
            
            // Konfirmasi Yes/No
            int confirm = JOptionPane.showConfirmDialog(null, 
                    "Yakin mau hapus ID " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                noteManager.deleteNote(id);
                JOptionPane.showMessageDialog(null, "🗑 Data dihapus.");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "⚠ ID harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "❌ " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (reminder != null) reminder.resumeReminder();
        }
    }
    
    // --- FITUR 5: EXPORT (GUI) ---
    private static void menuExportNotes() {
        List<Note> notes = noteManager.getNotes();
        if (notes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "📭 Data kosong, gak bisa export.");
            return;
        }

        TxtExporter exporter = new TxtExporter();
        exporter.export(notes, "backup_catatan.txt");
        JOptionPane.showMessageDialog(null, "💾 Sukses! File tersimpan sebagai 'backup_catatan.txt'");
    }
}