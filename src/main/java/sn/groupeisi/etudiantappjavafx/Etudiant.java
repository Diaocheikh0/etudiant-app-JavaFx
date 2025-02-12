package sn.groupeisi.etudiantappjavafx;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Etudiant {

    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private Double moyenne;
    private Classe classe;

    public Etudiant() {
    }

    public Etudiant(String nom, String prenom, Double moyenne, Classe classe) {
        this.nom = nom;
        this.prenom = prenom;
        this.moyenne = moyenne;
        this.classe = classe;

        // Vérification si la classe est non nulle avant de générer le matricule
        if (classe != null && classe.getNom() != null) {
            this.matricule = generateMatricule(classe.getNom());
        } else {
            this.matricule = "ET@UNKNOWN#";
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(Double moyenne) {
        this.moyenne = moyenne;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public String generateMatricule(String nomClasse) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = now.format(formatter);

        String cleanedNomClasse = nomClasse.replaceAll("\\s+", "").toUpperCase();

        String matricule = "ET@" + dateTime + cleanedNomClasse + "#";
        return matricule;
    }


    @Override
    public String toString() {
        return "Etudiant{" +
                "matricule='" + matricule + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", moyenne=" + moyenne +
                ", classe=" + classe +
                '}';
    }
}