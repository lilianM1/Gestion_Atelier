package fr.insa.eymin.gestion_atelier.modeles;

import java.util.ArrayList;

public class Operateur {
    // ========================== Attributs ================================

    
    private String codeOp;
    private String nom;
    private String prenom;
    private ArrayList<Machine> competences; // Liste des machines sur lesquelles l'opérateur est compétent
    private boolean etatOp; // Disponibilité de l'opérateur

    // ========================== Constructeurs ============================

    public Operateur() {
        this.codeOp = null;
        this.nom = null;
        this.prenom = null;
        this.competences = new ArrayList<Machine>();
        this.etatOp = false;
    }

    public Operateur(String codeOp, String nom, String prenom, ArrayList<Machine> competences, boolean etatOp) {
        this.codeOp = codeOp;
        this.nom = nom;
        this.prenom = prenom;
        this.competences = competences;
        this.etatOp = etatOp;
    }

    // ========================== Méthodes =================================

    // ========================== Getters/Setters ==========================

    public String getCodeOp() {
        return codeOp;
    }

    public void setCodeOp(String codeOp) {
        this.codeOp = codeOp;
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

    public ArrayList<Machine> getCompetences() {
        return competences;
    }

    public void setCompetences(ArrayList<Machine> competences) {
        this.competences = competences;
    }

    public boolean isEtatOp() {
        return etatOp;
    }

    public void setEtatOp(boolean etatOp) {
        this.etatOp = etatOp;
    }

}
