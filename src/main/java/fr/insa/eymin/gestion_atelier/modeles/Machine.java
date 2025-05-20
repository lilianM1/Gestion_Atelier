/**
 * Classe Machine représentant une machine dans un atelier
 * Hérite de la classe Equipement et ajoute des attributs et méthodes spécifiques aux machines
 * Permet de gérer les caractéristiques et l'état des machines dans l'atelier
 */
package fr.insa.eymin.gestion_atelier.modeles;


public class Machine extends Equipement {
    // ========================== Attributs ================================

    private float posX; // Coordonnée x de la machine
    private float posY; // Coordonnée y de la machine
    private float coutHoraire; // Coût horaire de la machine
    private float dureeUtil; // Durée d'utilisation de la machine
    private EtatMachine etatMachine; // Etat de la machine

    // ========================== Constructeurs ============================

    /**
     * Constructeur par défaut
     * Initialise une machine avec des valeurs par défaut
     */
    public Machine() {
        super();
        this.posX = 0;
        this.posY = 0;
        this.coutHoraire = 0;
        this.dureeUtil = 0;
        this.etatMachine = EtatMachine.OCCUPE;
    }

    /**
     * Constructeur avec paramètres
     */
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

    /**
     * Affiche les informations de la machine dans la console
     * Utilisé principalement pour le débogage
     */
    public void afficherMachine() {
        System.out.println("refMachine = " + super.refEquipement + ", dMachine = " + super.dEquipement + ", posX = "
                + this.posX + ", posY = "
                + this.posY + ", coutHoraire = " + this.coutHoraire + ", dureeUtil = " + this.dureeUtil + ", etat = "
                + this.etatMachine);
    }

    /**
     * Supprime les données d'une machine en réinitialisant tous ses attributs
     * Met la référence et la description à null
     * Remet les valeurs numériques à 0 et l'état à null
     */
    public void supprimerMachine() {
        super.refEquipement = null;
        super.dEquipement = null;
        this.posX = 0;
        this.posY = 0;
        this.coutHoraire = 0;
        this.dureeUtil = 0;
        this.etatMachine = null;
    }

    // ========================== Getters/Setters ==========================

    /**
     * @return La position X de la machine dans l'atelier
     */
    public float getPosX() {
        return posX;
    }

    /**
     * @param posX Nouvelle position X de la machine
     */
    public void setPosX(float posX) {
        this.posX = posX;
    }

    /**
     * @return La position Y de la machine dans l'atelier
     */
    public float getPosY() {
        return posY;
    }

    /**
     * @param posY Nouvelle position Y de la machine
     */
    public void setPosY(float posY) {
        this.posY = posY;
    }

    /**
     * @return Le coût horaire d'utilisation de la machine
     */
    public float getCoutHoraire() {
        return coutHoraire;
    }

    /**
     * @param coutHoraire Nouveau coût horaire de la machine
     */
    public void setCoutHoraire(float coutHoraire) {
        this.coutHoraire = coutHoraire;
    }

    /**
     * @return La durée d'utilisation de la machine
     */
    public float getDureeUtil() {
        return dureeUtil;
    }

    /**
     * @param dureeUtil Nouvelle durée d'utilisation de la machine
     */
    public void setDureeUtil(float dureeUtil) {
        this.dureeUtil = dureeUtil;
    }

    /**
     * @return L'état actuel de la machine (DISPONIBLE, OCCUPE, etc.)
     */
    public EtatMachine getEtat() {
        return etatMachine;
    }

    /**
     * @param etat Nouvel état de la machine
     */
    public void setEtat(EtatMachine etat) {
        this.etatMachine = etat;
    }
}