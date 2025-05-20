package fr.insa.eymin.gestion_atelier.modeles;

import java.util.ArrayList;

public class Poste extends Equipement {
    // ========================== Attributs ================================

    private ArrayList<Machine> machines; // Liste des machines du poste

    // ========================== Constructeurs ============================

    public Poste() {
        super();
        this.machines = new ArrayList<Machine>();
    }

    public Poste(String refPoste, String dPoste, ArrayList<Machine> machines) {
        super(refPoste, dPoste);
        this.machines = machines;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations du poste
    public void afficherPoste() {
        ArrayList<String> listeM = new ArrayList<String>();
        for (Machine m : this.machines) {
            listeM.add(m.getRefEquipement());
        }
        System.out.println("refPoste = " + super.refEquipement + ", dPoste = " + super.dEquipement + ", machines = "
                + listeM + "\n");
    }

    // ---------------------------------------------------------------------
    // Crée un poste (méthode de convenance statique, mais en MVC devrait être dans
    // un contrôleur)
    public static ArrayList<Poste> creerPoste(ArrayList<Poste> postes, ArrayList<Machine> machines) {
        // La création devrait être gérée par le contrôleur, mais gardé pour
        // compatibilité
        return postes;
    }

    // ---------------------------------------------------------------------
    // Supprime le poste
    public void supprimerPoste() {
        super.refEquipement = null;
        super.dEquipement = null;
        this.machines.clear();
    }

    // ========================== Getters/Setters ==========================

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public void setMachines(ArrayList<Machine> machines) {
        this.machines = machines;
    }

}
