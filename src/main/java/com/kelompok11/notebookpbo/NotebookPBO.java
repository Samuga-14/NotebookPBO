/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.kelompok11.notebookpbo;

// Import semua pasukan kita
import com.kelompok11.notebookpbo.database.DatabaseManager;
import com.kelompok11.notebookpbo.service.NoteManager;
import com.kelompok11.notebookpbo.model.Note;
import com.kelompok11.notebookpbo.model.ReminderTask;
import com.kelompok11.notebookpbo.model.Category; // Import Category Penjaga
import com.kelompok11.notebookpbo.utils.TxtExporter;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Biar print tanggal rapi
import java.util.List;

public class NotebookPBO { 
    
    // --- GLOBAL VARIABLES ---
    private static NoteManager noteManager;
    private static Scanner scanner = new Scanner(System.in);
    
    // Satpam Thread (Static biar bisa diakses semua method)
    private static ReminderTask reminder;

    public static void main(String[] args) {
        // 1. SETUP KONEKSI DATABASE
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect(); 
        
        // Pasang Otak Aplikasi
        noteManager = new NoteManager(dbManager);

        // 2. NYALAIN SATPAM (REMINDER THREAD)
        // Inisialisasi variabel global (JANGAN pake 'ReminderTask reminder =' lagi)
        reminder = new ReminderTask(noteManager); 
        reminder.start(); 
        
        // Trik biar output gak tabrakan sama Thread Satpam
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 3. LOOP MENU UTAMA
        boolean isRunning = true;
        
        while (isRunning) {
            showMenu();
            System.out.print("Pilih menu : ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> menuAddNote();
                case "2" -> menuShowNotes();
                case "3" -> menuUpdateNote();
                case "4" -> menuDeleteNote();
                case "5" -> menuExportNotes();
                case "0" -> {
                    System.out.println("Sampai jumpa! ");
                    if (reminder != null) reminder.stopReminder(); // Matikan satpam
                    isRunning = false; 
                }
                default -> System.out.println("Pilihan Tidak Tersedia, silahkan Coba lagi.");
            }
        }
    }

    // --- TAMPILAN MENU ---
    private static void showMenu() {
        System.out.println("\n NOTEBOOK PBO KELOMPOK 11");
        System.out.println("1. Tambah Catatan");
        System.out.println("2. Lihat Semua Catatan");
        System.out.println("3. Edit Catatan (Update)");
        System.out.println("4. Hapus Catatan (Delete)");
        System.out.println("5. Export ke File (TXT)");
        System.out.println("0. Keluar");
        System.out.println("");
    }

    // --- FITUR 1: TAMBAH CATATAN (CREATE) ---
    private static void menuAddNote() {
        System.out.println("\n--- Tambah Catatan ---");
        
        System.out.print("Judul    : ");
        String title = scanner.nextLine();
        
        System.out.print("Isi      : ");
        String content = scanner.nextLine();
        
        // Validasi Input Kategori (Paksa User Milih)
        String category = null;
        while (category == null) {
            Category.tampilkanPilihan();
            System.out.print("Pilih Nomor Kategori : ");
            try {
                int pilihan = Integer.parseInt(scanner.nextLine());
                category = Category.getKategori(pilihan);
                
                if (category == null) {
                    System.out.println(" Nomor tidak valid! Silahkan memilih yang tersedia pada List. ");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Input yang diterima hanya berupa angka !");
            }
        }
        
        // Set Deadline (Default 1 Jam dari sekarang)
        System.out.println("(Deadline otomatis diset 1 Jam dari sekarang)");
        LocalDateTime deadline = LocalDateTime.now().plusHours(1); 

        noteManager.addNote(title, content, category, deadline);
    }

    // --- FITUR 2: LIHAT CATATAN (READ) ---
    private static void menuShowNotes() {
        System.out.println("\n--- Daftar Catatan ---");
        List<Note> notes = noteManager.getNotes();
        
        if (notes.isEmpty()) {
            System.out.println(" Catatan Masih Kosong. ");
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (Note n : notes) {
                String deadlineStr = (n.getDeadline() != null) ? n.getDeadline().format(fmt) : "-";
                
                System.out.println("[" + n.getId() + "] " + n.getTitle() + " (" + n.getCategory() + ")");
                System.out.println("    Isi      : " + n.getContent());
                System.out.println("    Deadline : " + deadlineStr);
                System.out.println("-------------------------");
            }
        }
    }

    // --- FITUR 3: EDIT CATATAN (UPDATE) ---
    private static void menuUpdateNote() {
        // PAUSE SATPAM (Biar gak ganggu pas ngedit)
        if (reminder != null) reminder.pause();
        
        System.out.println("\n---️ Edit Catatan ---");
        menuShowNotes(); 
        
        System.out.print("Masukkan ID catatan yang ingin diedit: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            
            // Cek ID dulu (Bisa throw Exception kalo gak ketemu)
            Note noteLama = noteManager.getNoteById(id); 

            System.out.println(">>> Mengedit Catatan: " + noteLama.getTitle());
            
            System.out.print("Judul Baru    : ");
            String title = scanner.nextLine();
            
            System.out.print("Isi Baru      : ");
            String content = scanner.nextLine();
            
            // Validasi Kategori Baru
            String category = null;
            while (category == null) {
                System.out.println("[Ganti Kategori]");
                Category.tampilkanPilihan();
                System.out.print("Pilih Nomor Kategori Baru : ");
                try {
                    int pilihan = Integer.parseInt(scanner.nextLine());
                    category = Category.getKategori(pilihan);
                    if (category == null) System.out.println(" Nomor salah!");
                } catch (NumberFormatException e) {
                    System.out.println("Inputan yang diterima hanyalah berupa angka !");
                }
            }
            
            // Reset deadline
            LocalDateTime deadline = LocalDateTime.now().plusHours(24); 
            System.out.println("(Deadline diperpanjang 24 jam)");

            noteManager.updateNote(id, title, content, category, deadline);
            
        } catch (NumberFormatException e) {
            System.out.println("ID harus berupa angka!");
        } catch (IllegalArgumentException e) {
            System.out.println(" ERROR: " + e.getMessage());
        } finally {
            // WAJIB RESUME (Mau error atau sukses, satpam harus kerja lagi)
            if (reminder != null) reminder.resumeReminder();
        }
    }

    // --- FITUR 4: HAPUS CATATAN (DELETE) ---
    private static void menuDeleteNote() {
        if (reminder != null) reminder.pause();
        
        System.out.println("\n--- Hapus Catatan ---");
        menuShowNotes();
        
        System.out.print("Masukkan ID catatan yang mau dihapus: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            
            // Validasi ID ada atau nggak
            noteManager.getNoteById(id); // Cuma buat cek, gak perlu disimpen variabelnya
            
            System.out.print("Yakin ingin menghapus ID " + id + "? (y/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("y")) {
                noteManager.deleteNote(id);
            } else {
                System.out.println("Batal hapus.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println(" ID harus berupa angka!");
        } catch (IllegalArgumentException e) {
            System.out.println(" ERROR: " + e.getMessage());
        } finally {
            if (reminder != null) reminder.resumeReminder();
        }
    }
    
    // --- FITUR 5: EXPORT FILE (I/O) ---
    private static void menuExportNotes() {
        System.out.println("\n--- Export Data ke File ---");
        List<Note> notes = noteManager.getNotes();
        
        if (notes.isEmpty()) {
            System.out.println(" Data kosong, tidak ada catatan yang bisa diexport.");
            return;
        }

        TxtExporter exporter = new TxtExporter();
        exporter.export(notes, "catatanPBO.txt");
    }
}
