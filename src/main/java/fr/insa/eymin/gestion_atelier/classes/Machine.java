package fr.insa.eymin.gestion_atelier.classes;

public class Machine extends Equipement {
    // ========================== Attributs ================================

    private float posX; // Coordonnée x de la machine
    private float posY; // Coordonnée y de la machine
    private float coutHoraire; // Coût horaire de la machine
    private float dureeUtil; // Durée d'utilisation de la machine
    private EtatMachine etatMachine; // Etat de la machine

    // ========================== Constructeurs ============================

    public Machine() {
        super();
        this.posX = 0;
        this.posY = 0;
        this.coutHoraire = 0;
        this.dureeUtil = 0;
        this.etatMachine = EtatMachine.OCCUPE;
    }

    public Machine(String refMachine, String dMachine, float posX, float posY, float coutHoraire, float dureeUtil,
            EtatMachine etat) {
        super(refMachine, dMachine);
        this.posX = posX;
        this.posY = posY;
        this.coutHoraire = coutHoraire;
        this.dureeUtil = dureeUtil;
        this.etatMachine = etat;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations de la machine
    public void afficherMachine() {
        System.out.println("refMachine = " + super.refEquipement + ", dMachine = " + super.dEquipement + ", posX = "
                + this.posX + ", posY = "
                + this.posY + ", coutHoraire = " + this.coutHoraire + ", dureeUtil = " + this.dureeUtil + ", etat = "
                + this.etatMachine
                + "\n");
    }

    // ---------------------------------------------------------------------
    // Supprime la machine
    public void supprimerMachine() {
        super.refEquipement = null;
        super.dEquipement = null;
        this.posX = 0;
        this.posY = 0;
        this.coutHoraire = 0;
        this.dureeUtil = 0;
        this.etatMachine = EtatMachine.OCCUPE;
    }

    // ========================== Getters/Setters ==========================

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getCoutHoraire() {
        return coutHoraire;
    }

    public void setCoutHoraire(float coutHoraire) {
        this.coutHoraire = coutHoraire;
    }

    public float getDureeUtil() {
        return dureeUtil;
    }

    public void setDureeUtil(float dureeUtil) {
        this.dureeUtil = dureeUtil;
    }

    public EtatMachine getEtat() {
        return etatMachine;
    }

    public void setEtat(EtatMachine etat) {
        this.etatMachine = etat;
    }

}
