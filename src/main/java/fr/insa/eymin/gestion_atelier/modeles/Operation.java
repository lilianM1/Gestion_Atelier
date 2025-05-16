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

    public Operation(String refOperation, String dOperation, float dureeOp, Equipement refEquipement) {
        this.refOperation = refOperation;
        this.dOperation = dOperation;
        this.dureeOp = dureeOp;
        this.refEquipement = refEquipement;
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

    public Equipement getrefEquipement() {
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
