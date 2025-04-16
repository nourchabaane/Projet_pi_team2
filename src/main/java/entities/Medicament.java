package entities;

public class Medicament {
    private int id;
    private String nom;
    private String description;
    private String email;
    private String phone;
    private String dosage;
    private String schedule;

    // Constructors
    public Medicament() {}

    public Medicament(int id, String nom, String description, String email, String phone, String dosage, String schedule) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.dosage = dosage;
        this.schedule = schedule;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dosage='" + dosage + '\'' +
                ", schedule='" + schedule + '\'' +
                '}';
    }
}