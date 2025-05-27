package fr.insa.eymin.gestion_atelier.modeles;

public class Operation {
    // ========================== Attributs ================================

    private String refOperation; // Référence de l'opération
    private String dOperation; // Désignation de l'opération
    private float dureeOp; // Durée de l'opération
    private Equipement refEquipement; // Référence de l'équipement

    // ========================== Constructeurs ============================

    public Operation() {
        this.refOperation = null;
        this.dOperation = null;
        this.dureeOp = 0;
        this.refEquipement = null;
    }

    public Operation(String refOperation, String dOperation, float dureeOp) {
        this.refOperation = refOperation;
        this.dOperation = dOperation;
        this.dureeOp = dureeOp;
        this.refEquipement = null;
    }

    public Operation(String refOperation, String dOperation, Equipement refEquipement) {
        this.refOperation = refOperation;
        this.dOperation = dOperation;
        this.refEquipement = refEquipement;
        if (refEquipement instanceof Machine) {
            this.dureeOp = ((Machine) refEquipement).getDureeUtil();
        } else if (refEquipement instanceof Poste) {
            for (Machine m : ((Poste) refEquipement).getMachines()) {
                this.dureeOp += m.getDureeUtil();
            }
        }
    }

    // ========================== Méthodes =================================

    public void afficherOperation() {
        System.out.println(
                "Operation [refOperation=" + refOperation + ", dOperation=" + dOperation + ", dureeOp=" + dureeOp
                        + ", refEquipement=" + refEquipement.getRefEquipement() + "]");
    }

    public String toStringForSave() {

        return "OP;" + refOperation + ";" + dOperation + ";" + dureeOp + ";" + refEquipement.getRefEquipement();
    }

    public void calculerDureeOperation() {
        if (this.getRefEquipement() instanceof Machine) {
            this.setDureeOp(((Machine) this.getRefEquipement()).getDureeUtil());
            System.out.println("Duree de l'opération : " + this.getDureeOp());
        } else if (this.getRefEquipement() instanceof Poste) {
            float duree = 0;
            for (Machine m : ((Poste) this.getRefEquipement()).getMachines()) {
                System.out.println("Machine : " + m.getRefEquipement() + ", Duree : " + m.getDureeUtil());
                duree += m.getDureeUtil();
            }
            this.setDureeOp(duree);
            System.out.println("Duree de l'opération : " + this.getDureeOp());
        }
    }

    // ========================== Getters/Setters ==========================

    public String getdOperation() {
        return dOperation;
    }

    public void setdOperation(String dOperation) {
        this.dOperation = dOperation;
    }

    public float getDureeOp() {
        return dureeOp;
    }

    public void setDureeOp(float dureeOp) {
        this.dureeOp = dureeOp;
    }

    public Equipement getRefEquipement() {
        return refEquipement;
    }

    public void setrefEquipement(Equipement refEquipement) {
        this.refEquipement = refEquipement;
    }

    public String getRefOperation() {
        return refOperation;
    }

    public void setRefOperation(String refOperation) {
        this.refOperation = refOperation;
    }

}
