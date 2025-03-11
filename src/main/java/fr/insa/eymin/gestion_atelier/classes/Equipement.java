package fr.insa.eymin.gestion_atelier.classes;

public abstract class Equipement {
    // ========================== Attributs ================================

    protected String refEquipement; // Référence de l'équipement
    protected String dEquipement; // Désignation de l'équipement

    // ========================== Constructeurs ============================

    public Equipement() {
        this.refEquipement = null;
        this.dEquipement = null;
    }

    public Equipement(String refEquipement, String dEquipement) {
        this.refEquipement = refEquipement;
        this.dEquipement = dEquipement;
    }

    // ========================== Méthodes =================================

    // ========================== Getters/Setters ==========================

    public String getRefEquipement() {
        return refEquipement;
    }

    public void setRefEquipement(String refEquipement) {
        this.refEquipement = refEquipement;
    }

    public String getdEquipement() {
        return dEquipement;
    }

    public void setdEquipement(String dEquipement) {
        this.dEquipement = dEquipement;
    }

}
