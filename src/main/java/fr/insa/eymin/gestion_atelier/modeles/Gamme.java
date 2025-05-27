package fr.insa.eymin.gestion_atelier.modeles;

import java.util.ArrayList;

public class Gamme {
    // ========================== Attributs ================================

    private String refGamme; // Référence de la gamme
    private String dGamme; // Désignation de la gamme
    private ArrayList<Operation> operations; // Liste des opérations de la gamme
    private Produit produit; // Produit associé à la gamme

    // ========================== Constructeurs ============================

    public Gamme() {
        this.dGamme = null;
        this.operations = null;
        // this.equipements = null;
        this.refGamme = null;
    }

    public Gamme(String refGamme, String dGamme, ArrayList<Operation> operations, Produit produit) {
        this.refGamme = refGamme;
        this.dGamme = dGamme;
        this.operations = operations;
        this.produit = produit;
    }

    public Gamme(String refGamme, String dGamme) {
        this.refGamme = refGamme;
        this.dGamme = dGamme;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations de la gamme
    public void afficherGamme() {
        ArrayList<String> listeOp = new ArrayList<String>();
        for (Operation op : this.operations) {
            listeOp.add(op.getRefOperation());
        }
        System.out.println("refGamme = " + this.refGamme + ", dGamme = " + this.dGamme + ", operations = " + listeOp
                + ", produit = " + (this.produit != null ? this.produit.getCodeProduit() : "Aucun") + "\n");
    }

    public String toStringForSave() {
        String ligne = "G;" + this.refGamme + ";" + this.dGamme + ";";
        ArrayList<String> listeOp = new ArrayList<String>();
        for (Operation op : this.operations) {
            listeOp.add(op.getRefOperation());
        }
        ligne += listeOp + ";" + (this.produit != null ? this.produit.getCodeProduit() : "Aucun");
        return ligne;
    }

    // ---------------------------------------------------------------------
    // Supprime la gamme
    public void supprimerGamme() {
        this.dGamme = null;
        this.operations.clear();
        // this.equipements.clear();
        this.refGamme = null;
    }

    // ---------------------------------------------------------------------
    // Calcule le coût de la gamme
    public float calculCoutGamme() {
        float cout = 0;
        for (Operation op : this.operations) {
            if (op.getRefEquipement() instanceof Machine) {
                cout += ((Machine) op.getRefEquipement()).getCoutHoraire()
                        * ((Machine) op.getRefEquipement()).getDureeUtil();
            } else if (op.getRefEquipement() instanceof Poste) {
                for (Machine m : ((Poste) op.getRefEquipement()).getMachines()) {
                    cout += m.getCoutHoraire() * m.getDureeUtil();
                }
            }
        }
        return cout;
    }

    // ---------------------------------------------------------------------
    // Calcule la durée de la gamme
    public float calculDureeGamme() {
        float duree = 0;
        for (Operation op : this.operations) {
            duree += op.getDureeOp();
        }
        return duree;
    }

    // ========================== Getters/Setters ==========================

    public String getdGamme() {
        return dGamme;
    }

    public void setdGamme(String dGamme) {
        this.dGamme = dGamme;
    }

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public String getRefGamme() {
        return refGamme;
    }

    public void setRefGamme(String refGamme) {
        this.refGamme = refGamme;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void ajouterOperation(Operation operation) {
        if (this.operations == null) {
            this.operations = new ArrayList<>();
        }
        this.operations.add(operation);
    }

}