package services;

import entities.Medicament;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;

public class MedicamentService {

    public void ajouter(Medicament med) throws SQLException {
        String req = "INSERT INTO medicament (nom, description, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, med.getNom());
            ps.setString(2, med.getDescription());
            ps.setString(3, med.getEmail());
            ps.setString(4, med.getPhone());
            ps.executeUpdate();

            // Retrieve the auto-generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                med.setId(rs.getInt(1));
            }
        }
    }

    public void modifier(Medicament med) throws SQLException {
        String req = "UPDATE medicament SET nom = ?, description = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setString(1, med.getNom());
            ps.setString(2, med.getDescription());
            ps.setString(3, med.getEmail());
            ps.setString(4, med.getPhone());
            ps.setInt(5, med.getId());
            ps.executeUpdate();
        }
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM medicament WHERE id = ?";
        try (Connection conn = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public ArrayList<Medicament> afficherTous() throws SQLException {
        ArrayList<Medicament> medicaments = new ArrayList<>();
        String req = "SELECT * FROM medicament";
        try (Connection conn = MyDatabase.getInstance().getCnx();
             Statement ste = conn.createStatement();
             ResultSet rs = ste.executeQuery(req)) {
            while (rs.next()) {
                Medicament med = new Medicament();
                med.setId(rs.getInt("id"));
                med.setNom(rs.getString("nom"));
                med.setDescription(rs.getString("description"));
                med.setEmail(rs.getString("email"));
                med.setPhone(rs.getString("phone"));
                medicaments.add(med);
            }
        }
        return medicaments;
    }
}