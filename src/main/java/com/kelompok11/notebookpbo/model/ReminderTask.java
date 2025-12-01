/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.model;

import com.kelompok11.notebookpbo.service.NoteManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderTask extends Thread {
    private final NoteManager noteManager;
    private boolean isRunning = true;
    
    // Format tanggal biar enak dibaca
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public ReminderTask(NoteManager manager) {
        this.noteManager = manager;
    }

    @Override
    public void run() {
        System.out.println(">>> [SYSTEM] Satpam Reminder Aktif di Background...");
        
        while (isRunning) {
            try {
                // 1. Ambil semua catatan terbaru dari database
                List<Note> notes = noteManager.getNotes();
                LocalDateTime now = LocalDateTime.now();
                
                // 2. Cek satu-satu
                for (Note n : notes) {
                    // Logic: Kalau deadline SUDAH LEWAT dan SELISIHNYA kurang dari 1 menit (biar gak spam notif lama)
                    if (n.getDeadline() != null && n.getDeadline().isBefore(now)) {
                        
                        // Cek biar notifikasinya cuma muncul buat yang baru aja lewat (misal range 1 menit terakhir)
                        // Jadi tugas tahun lalu gak bakal dimunculin notifnya.
                        if (n.getDeadline().isAfter(now.minusSeconds(10))) {
                            System.out.println("\n\n========================================");
                            System.out.println("🔔 PENGINGAT TUGAS! WAKTU HABIS! 🔔");
                            System.out.println("Judul    : " + n.getTitle().toUpperCase());
                            System.out.println("Deadline : " + n.getDeadline().format(formatter));
                            System.out.println("Segera kerjakan atau nilai lu C!");
                            System.out.println("========================================\n");
                            System.out.print("Pilih menu > "); // Biar kursor input rapi lagi
                        }
                    }
                }
                
                // 3. Tidur 5 detik biar CPU gak panas
                Thread.sleep(5000); 
                
            } catch (InterruptedException e) {
                System.out.println(">>> [SYSTEM] Satpam Reminder Pulang.");
                break;
            }
        }
    }
    
    public void stopReminder() {
        this.isRunning = false;
    }
}
