/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.kelompok11.notebookpbo;

// Import yang wajib ada
import com.kelompok11.notebookpbo.database.DatabaseManager;
import com.kelompok11.notebookpbo.service.NoteManager;
import com.kelompok11.notebookpbo.model.Note;
import com.kelompok11.notebookpbo.model.ReminderTask; // Import dari reminder exception 
import com.kelompok11.notebookpbo.utils.TxtExporter;
import com.kelompok11.notebookpbo.model.Category;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.List;

public class NotebookPBO { // Nama Class HARUS SAMA dengan Nama File
    
    // Global variables
    private static NoteManager noteManager;
    private static Scanner scanner = new Scanner(System.in);
    
    // menambah thread remindertask
    private static ReminderTask reminder;

    public static void main(String[] args) {
        // 1. SETUP KONEKSI DATABASE
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect(); 
        
        // Pasang Otak Aplikasi
        noteManager = new NoteManager(dbManager);

        // 2. NYALAIN SATPAM (REMINDER THREAD)
        reminder = new ReminderTask(noteManager); //berubah dari lokal variabel global karena sudah dideclare diatas
        reminder.start(); // Jalankan thread di background
        
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 3. LOOP MENU UTAMA
        boolean isRunning = true;
        
        while (isRunning) {
            showMenu();
            System.out.print("Pilih menu > ");
            String input = scanner.nextLine();

            // Perhatikan struktur kurung kurawal di switch ini
            switch (input) {
                case "1" -> menuAddNote();
                case "2" -> menuShowNotes();
                case "3" -> menuUpdateNote(); // <--- BARU
                case "4" -> menuDeleteNote(); // <--- BARU
                case "5" -> menuExportNotes();
                case "0" -> {
                    System.out.println("Sampai jumpa!");
                    reminder.stopReminder(); // Matikan satpam
                    isRunning = false;     // Matikan loop
                }
                default -> System.out.println(" Pilihan tidak ada. Coba lagi.");
            }
        }
    }

    // --- AREA METHOD TAMPILAN (DI LUAR MAIN, TAPI DI DALAM CLASS) ---

    private static void showMenu() {
            System.out.println("\n=== NOTEBOOK PBO KELOMPOK 11 ===");
            System.out.println("1. Tambah Catatan");
            System.out.println("2. Lihat Semua Catatan");
            System.out.println("3. Edit Catatan (Update)"); // <--- BARU
            System.out.println("4. Hapus Catatan (Delete)"); // <--- BARU
            System.out.println("5. Export ke File (TXT)");
            System.out.println("0. Keluar");
            System.out.println("===================================");
        }

    private static void menuAddNote() {
        System.out.println("\n--- Tambah Catatan ---");
        
        System.out.print("Judul    : ");
        String title = scanner.nextLine();
        
        System.out.print("Isi      : ");
        String content = scanner.nextLine();
        
        // --- MULAI PERUBAHAN INPUT KATEGORI ---
        String category = null;
        while (category == null) {
            Category.tampilkanPilihan();
            System.out.print("Pilih Nomor Kategori : ");
            try {
                int pilihan = Integer.parseInt(scanner.nextLine());
                category = Category.getKategori(pilihan);
                
                if (category == null) {
                    System.out.println("Nomor tidak valid! Pilih yang ada di list saja.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Masukkan Angka !");
            }
        }
        System.out.println(">>> Kategori terpilih: " + category);
        
        // Deadline diset 15 detik dari sekarang buat ngetes fitur Reminder
        System.out.println("(Deadline otomatis diset 30 Menit dari sekarang untuk testing Reminder)");
        LocalDateTime deadline = LocalDateTime.now().plusMinutes(30); 

        noteManager.addNote(title, content, category, deadline);
    }

    private static void menuShowNotes() {
        System.out.println("\n---  Daftar Catatan ---");
        List<Note> notes = noteManager.getNotes();
        
        if (notes.isEmpty()) {
            System.out.println("Belum ada catatan. Silahkan isi terlebih dahulu");
        } else {
            for (Note n : notes) {
                System.out.println("[" + n.getId() + "] " + n.getTitle() + " (" + n.getCategory() + ")");
                System.out.println("    Isi: " + n.getContent());
                System.out.println("    Deadline: " + n.getDeadline());
                System.out.println("-------------------------");
            }
        }
    }
    
    private static void menuExportNotes() {
        System.out.println("\n--- Export Data ke File ---");
        
        // 1. Ambil data dulu dari database
        List<Note> notes = noteManager.getNotes();
        
        if (notes.isEmpty()) {
            System.out.println(" Data masih kosong, tidak ada yang bisa diexport.");
            return;
        }

        // 2. Siapkan Exporter
        TxtExporter exporter = new TxtExporter();
        
        // 3. Eksekusi Export
        // File bakal muncul di folder project lu (sejajar sama pom.xml)
        exporter.export(notes, "backup_catatan.txt");
    }
    
    // --- FITUR UPDATE ---
    private static void menuUpdateNote() {
        // 1. Pause dulu
        if (reminder != null) reminder.pause();
        
        System.out.println("\n---️ Edit Catatan ---");
        menuShowNotes(); 
        
        System.out.print("Masukkan ID catatan yang mau diedit: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            
            // Cek ID (Bisa throw IllegalArgumentException)
            Note noteYangMauDiedit = noteManager.getNoteById(id); 

            System.out.println(">>> Mengedit Catatan: " + noteYangMauDiedit.getTitle());
            
            System.out.print("Judul Baru    : ");
            String title = scanner.nextLine();
            System.out.print("Isi Baru      : ");
            String content = scanner.nextLine();
             // --- MULAI PERUBAHAN INPUT KATEGORI (EDIT) ---
            String category = null;
            while (category == null) {
                System.out.println("\n[Ganti Kategori]");
                Category.tampilkanPilihan();
                System.out.print("Pilih Nomor Kategori Baru : ");
                try {
                    int pilihan = Integer.parseInt(scanner.nextLine());
                    category = Category.getKategori(pilihan);
                    
                    if (category == null) System.out.println("⚠️ Nomor salah!");
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Masukkan Angka bukan yang lain !");
                }
            } 
            
            LocalDateTime deadline = LocalDateTime.now().plusHours(24); 
            System.out.println("(Deadline direset jadi 24 jam dari sekarang)");

            noteManager.updateNote(id, title, content, category, deadline);
            
        } catch (NumberFormatException e) {
            System.out.println("ID harus berupa angka!");
        } catch (IllegalArgumentException e) {
            // INI YANG TADI HILANG DI MENU EDIT
            System.out.println("❌ ERROR: " + e.getMessage());
        } finally {
            // 2. WAJIB RESUME (Pake finally biar mau error atau sukses, satpam tetep bangun)
            if (reminder != null) reminder.resumeReminder();
        }
    }

    // --- FITUR DELETE ---
    private static void menuDeleteNote() {
        if (reminder != null) reminder.pause();
        
        System.out.println("\n--- 🗑️ Hapus Catatan ---");
        menuShowNotes();
        
        System.out.print("Masukkan ID catatan yang mau dihapus: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Yakin mau hapus ID " + id + "? (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                noteManager.deleteNote(id);
            } else {
                System.out.println("Batal hapus.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("ID harus berupa angka!");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            // 2. WAJIB RESUME DISINI JUGA
            if (reminder != null) reminder.resumeReminder();
        }
    }
}
