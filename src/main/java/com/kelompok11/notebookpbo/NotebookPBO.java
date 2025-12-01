/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.kelompok11.notebookpbo;

// Import yang wajib ada
import com.kelompok11.notebookpbo.database.DatabaseManager;
import com.kelompok11.notebookpbo.service.NoteManager;
import com.kelompok11.notebookpbo.model.Note;
import com.kelompok11.notebookpbo.model.ReminderTask; // Import Satpam

import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.List;

public class NotebookPBO { // Nama Class HARUS SAMA dengan Nama File
    
    // Global variables
    private static NoteManager noteManager;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 1. SETUP KONEKSI DATABASE
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect(); 
        
        // Pasang Otak Aplikasi
        noteManager = new NoteManager(dbManager);

        // 2. NYALAIN SATPAM (REMINDER THREAD)
        ReminderTask reminder = new ReminderTask(noteManager);
        reminder.start(); // Jalankan thread di background

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
        System.out.println("1. Tambah Catatan Baru");
        System.out.println("2. Lihat Semua Catatan");
        System.out.println("0. Keluar");
        System.out.println("===================================");
    }

    private static void menuAddNote() {
        System.out.println("\n--- Tambah Catatan ---");
        
        System.out.print("Judul    : ");
        String title = scanner.nextLine();
        
        System.out.print("Isi      : ");
        String content = scanner.nextLine();
        
        System.out.print("Kategori : ");
        String category = scanner.nextLine();
        
        // Deadline diset 15 detik dari sekarang buat ngetes fitur Reminder
        System.out.println("(Deadline otomatis diset 15 DETIK dari sekarang untuk testing Reminder)");
        LocalDateTime deadline = LocalDateTime.now().plusSeconds(15); 

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
}
