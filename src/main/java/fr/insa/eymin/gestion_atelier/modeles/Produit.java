package fr.insa.eymin.gestion_atelier.modeles;

public class Produit {
    // ========================== Attributs ================================

    private String codeProduit; // Code du produit
    private String dProduit; // Désignation du produit

    // ========================== Constructeurs ============================

    public Produit() {
        this.codeProduit = null;
        this.dProduit = null;
    }

    public Produit(String codeProduit, String dProduit) {
        this.codeProduit = codeProduit;
        this.dProduit = dProduit;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations du produit
    public void afficherProduit() {
        System.out.println("codeProduit = " + codeProduit + ", dProduit = " + dProduit + "\n");
    }

    public String toStringForSave() {
        return "PR;" + codeProduit + ";" + dProduit;
    }

    // ---------------------------------------------------------------------
    // Supprime le produit
    public void supprimerProduit() {
        this.codeProduit = null;
        this.dProduit = null;
    }

    // ========================== Getters/Setters ==========================

    public String getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }

    public String getdProduit() {
        return dProduit;
    }

    public void setdProduit(String dProduit) {
        this.dProduit = dProduit;
    }
}