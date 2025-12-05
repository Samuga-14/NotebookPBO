/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kelompok11.notebookpbo.database;

import com.kelompok11.notebookpbo.model.Note;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// Class ini mengimplementasikan kontrak NoteDAO
public class DatabaseManager implements NoteDAO {

    // 1. Konfigurasi Database (Sesuaikan kalau ada password XAMPP)
    private static final String URL = "jdbc:mysql://localhost:3306/db_notebook_pbo";
    private static final String USER = "root";
    private static final String PASSWORD = "samuga123"; 

    private Connection connection;

    // 2. Method untuk Buka Koneksi (Connect)
    public void connect() {
        try {
            // Load Driver MySQL (Wajib untuk JDBC modern)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Buka Pintu Koneksi
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" KONEKSI SUKSES: Berhasil terhubung ke Database!");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(" KONEKSI GAGAL: " + e.getMessage());
        }
    }
    
    // 3. Getter Connection (Biar bisa dipake buat query nanti)
    public Connection getConnection() {
        return connection;
    }

    // --- IMPLEMENTASI METHOD INTERFACE (NoteDAO) ---
    // (Sementara kita kosongin dulu isinya biar gak error, 
    // fokus kita sekarang ngetes koneksi dulu)

    @Override
    public void addNote(Note note) {
        // Query SQL untuk memasukkan data
        String sql = "INSERT INTO notes (title, content, category, deadline) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Mengisi tanda tanya (?) dengan data dari objek Note
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getCategory());
            
            // Konversi LocalDateTime java ke Timestamp database
            if (note.getDeadline() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(note.getDeadline()));
            } else {
                pstmt.setTimestamp(4, null);
            }

            pstmt.executeUpdate(); // Eksekusi kirim ke database
            System.out.println(" BERHASIL: Catatan '" + note.getTitle() + "' tersimpan!");
            
        } catch (SQLException e) {
            System.err.println(" GAGAL SIMPAN: " + e.getMessage());
        }
    }

@Override
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop setiap baris data yang ada di tabel database
            while (rs.next()) {
                Note note = new Note();
                
                // Ambil data dari kolom database -> Masukin ke objek Note
                note.setId(rs.getInt("id"));
                note.setTitle(rs.getString("title"));
                note.setContent(rs.getString("content"));
                note.setCategory(rs.getString("category"));
                
                // Konversi Timestamp DB ke LocalDateTime Java (Agak tricky disini)
                Timestamp ts = rs.getTimestamp("deadline");
                if (ts != null) {
                    note.setDeadline(ts.toLocalDateTime());
                }

                // Masukin ke list
                notes.add(note);
            }
        } catch (SQLException e) {
            System.err.println(" GAGAL LOAD DATA: " + e.getMessage());
        }
        
        return notes; // Balikin daftar catatan ke pemanggil
    }

@Override
    public void updateNote(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ?, category = ?, deadline = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getCategory());
            
            if (note.getDeadline() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(note.getDeadline()));
            } else {
                pstmt.setTimestamp(4, null);
            }
            
            pstmt.setInt(5, note.getId()); // WHERE id = ?

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" BERHASIL: Catatan ID " + note.getId() + " berhasil diupdate!");
            } else {
                System.out.println(" GAGAL: ID tidak ditemukan.");
            }
            
        } catch (SQLException e) {
            System.err.println(" ERROR UPDATE: " + e.getMessage());
        }
    }

    @Override
    public void deleteNote(int id) {
        String sql = "DELETE FROM notes WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(" BERHASIL: Catatan ID " + id + " berhasil dihapus!");
            } else {
                System.out.println(" GAGAL: ID tidak ditemukan.");
            }
            
        } catch (SQLException e) {
            System.err.println(" ERROR DELETE: " + e.getMessage());
        }
    }
    
    @Override
    public Note getNoteById(int id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        Note note = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    note = new Note();
                    note.setId(rs.getInt("id"));
                    note.setTitle(rs.getString("title"));
                    note.setContent(rs.getString("content"));
                    note.setCategory(rs.getString("category"));
                    
                    Timestamp ts = rs.getTimestamp("deadline");
                    if (ts != null) note.setDeadline(ts.toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            System.err.println(">>> ERROR GET ID: " + e.getMessage());
        }
        return note; // Bisa return null kalau gak ketemu
    }
           
// --- MAIN METHOD UNTUK TEST BACA DATA ---
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.connect();
        
        // 1. Coba ambil semua data
        System.out.println("Sedang mengambil data dari database...");
        List<Note> listCatatan = db.getAllNotes();
        
        // 2. Tampilkan di layar
        if (listCatatan.isEmpty()) {
            System.out.println("Belum ada catatan.");
        } else {
            for (Note n : listCatatan) {
                System.out.println("---------------------------------");
                System.out.println("ID       : " + n.getId());
                System.out.println("Judul    : " + n.getTitle());
                System.out.println("Kategori : " + n.getCategory());
                System.out.println("Deadline : " + n.getDeadline());
            }
            System.out.println("---------------------------------");
        }
    }
}