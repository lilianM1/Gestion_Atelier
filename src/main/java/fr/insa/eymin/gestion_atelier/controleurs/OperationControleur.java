package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import fr.insa.eymin.gestion_atelier.modeles.Equipement;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Operation;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import fr.insa.eymin.gestion_atelier.vues.OperationVue;

/**
 * Contrôleur pour gérer les interactions entre le modèle Operation et sa vue.
 * Coordonne les opérations de création et validation des opérations.
 */
public class OperationControleur {
    // Attributs
    private ArrayList<Operation> operations;
    private ArrayList<Machine> machines;
    private ArrayList<Poste> postes;
    private OperationVue vue;

    /**
     * Constructeur du contrôleur d'opérations
     * 
     * @param operations la liste des opérations
     */
    public OperationControleur(ArrayList<Operation> operations) {
        this.operations = operations;
        this.vue = new OperationVue(this);
    }

    /**
     * Affiche la fenêtre de création d'une opération
     */
    public void afficherFenetreCreation() {
        vue.afficherFenetreCreation();
    }

    /**
     * Crée une nouvelle opération avec les données saisies
     * Vérifie la validité des entrées et génère une erreur si nécessaire
     * 
     * @param refOperation  référence de l'opération
     * @param dOperation    désignation de l'opération
     * @param dureeOp       durée de l'opération
     * @param refEquipement référence de l'équipement associé
     * @return true si l'opération a été créée avec succès, false sinon
     */
    public boolean creerOperation(String refOperation, String dOperation, Equipement refEquipement) {
        try {
            // Vérification des champs vides
            if (refOperation.isEmpty() || dOperation.isEmpty() || refEquipement == null) {
                vue.afficherErreur("Erreur", "Champs vides", "Veuillez remplir tous les champs obligatoires.");
                return false;
            }

            // Vérification si l'opération existe déjà
            if (operations.stream().anyMatch(o -> o.getRefOperation().equals(refOperation))) {
                vue.afficherErreur("Erreur", "Opération déjà existante", "L'opération existe déjà.");
                return false;
            }

            // Parsing de la durée
            float duree;
            try {

                // Utiliser la durée calculée à partir de l'équipement
                duree = 0; // sera automatiquement calculée dans le constructeur
                Operation operation = new Operation(refOperation, dOperation, refEquipement);
                operations.add(operation);

                if (duree < 0) {
                    vue.afficherErreur("Erreur", "Durée invalide", "La durée ne peut pas être négative.");
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                vue.afficherErreur("Erreur", "Format incorrect", "La durée doit être un nombre.");
                return false;
            }

        } catch (Exception ex) {
            vue.afficherErreur("Erreur", "Erreur lors de la création", ex.getMessage());
            return false;
        }
    }

    /**
     * Retourne la liste des opérations
     * 
     * @return la liste des opérations
     */
    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public ArrayList<Poste> getPostes() {
        return postes;
    }

    public ArrayList<Machine> setMachines(ArrayList<Machine> machines) {
        this.machines = machines;
        return machines;
    }

    public ArrayList<Poste> setPostes(ArrayList<Poste> postes) {
        this.postes = postes;
        return postes;
    }
}
