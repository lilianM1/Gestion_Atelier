package fr.insa.eymin.gestion_atelier.modeles;

import java.util.ArrayList;

public class Gamme {
    // ========================== Attributs ================================

    private String refGamme; // Référence de la gamme
    private String dGamme; // Désignation de la gamme
    private ArrayList<Operation> operations; // Liste des opérations de la gamme
    // private ArrayList<Equipement> equipements; // Liste des équipements
    // nécessaires pour la gamme

    // ========================== Constructeurs ============================

    public Gamme() {
        this.dGamme = null;
        this.operations = null;
        // this.equipements = null;
        this.refGamme = null;
    }

    public Gamme(String refGamme, String dGamme, ArrayList<Operation> operations, ArrayList<Equipement> equipements) {
        this.dGamme = dGamme;
        this.operations = operations;
        // this.equipements = equipements;
        this.refGamme = refGamme;
    }

    public Gamme(String refGamme, String dGamme, ArrayList<Operation> operations) {
        this.refGamme = refGamme;
        this.dGamme = dGamme;
        this.operations = operations;
        ArrayList<Equipement> listEquipements = new ArrayList<Equipement>();
        for (Operation op : operations) {
            listEquipements.add(op.getrefEquipement());
        }
        // this.equipements = listEquipements;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations de la gamme
    public void afficherGamme() {
        ArrayList<String> listeOp = new ArrayList<String>();
        for (Operation op : this.operations) {
            listeOp.add(op.getRefOperation());
        }
        // ArrayList<String> listeEq = new ArrayList<String>();
        // for (Equipement eq : this.equipements) {
        // listeEq.add(eq.getRefEquipement());
        // }
        // System.out.println("refGamme = " + refGamme + ", dGamme = " + dGamme + ",
        // operations = " + listeOp
        // + ", equipements = " + listeEq + "\n");
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
        // TODO : a rafaire mais avec operations -> equipement
        // for (Equipement eq : this.equipements) {
        // if (eq instanceof Machine) {
        // cout += ((Machine) eq).getCoutHoraire() * ((Machine) eq).getDureeUtil();
        // } else if (eq instanceof Poste) {
        // for (Machine m : ((Poste) eq).getMachines()) {
        // cout += m.getCoutHoraire() * m.getDureeUtil();
        // }
        // }
        // }
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

    // public ArrayList<Equipement> getEquipements() {
    // return equipements;
    // }

    // public void setEquipements(ArrayList<Equipement> equipements) {
    // this.equipements = equipements;
    // }

    public String getRefGamme() {
        return refGamme;
    }

    public void setRefGamme(String refGamme) {
        this.refGamme = refGamme;
    }

}