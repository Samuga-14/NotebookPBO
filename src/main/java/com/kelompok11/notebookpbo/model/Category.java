/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.model;

import java.util.Arrays;
import java.util.List;

public class Category {
    
    // Daftar Kategori Baku (Hardcoded)
    private static final List<String> LIST_KATEGORI = Arrays.asList(
        "Tugas Kuliah",
        "Pribadi",
        "Pekerjaan",
        "Belanja",
        "Keuangan",
        "Lain-lain"
    );

    // Method buat nampilin menu pilihan
    public static void tampilkanPilihan() {
        System.out.println("--- Pilihan Kategori ---");
        for (int i = 0; i < LIST_KATEGORI.size(); i++) {
            System.out.println((i + 1) + ". " + LIST_KATEGORI.get(i));
        }
    }

    // Method konversi dari Nomor (1) jadi Teks ("Tugas Kuliah")
    public static String getKategori(int index) {
        // Cek index valid (ingat user input mulai dari 1, list mulai dari 0)
        if (index >= 1 && index <= LIST_KATEGORI.size()) {
            return LIST_KATEGORI.get(index - 1);
        }
        return null; // Kalau user input nomor ngawur
    }
}
