package sn.groupeisi.etudiantappjavafx;

public class Classe {
    private int id;
    private String nom;
    private int effectif;

    public Classe() {
    }

    public Classe(String nom) {
        this.nom = nom;
    }

    public Classe(int id) {
        this.id = id;
    }

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

    public int getEffectif() {
        return effectif;
    }

    public void setEffectif(int effectif) {
        this.effectif = effectif;
    }

    @Override
    public String toString() {
        return "Classe [id=" + id + ", nom=" + nom + ", effectif=" + effectif + "]";
    }
}