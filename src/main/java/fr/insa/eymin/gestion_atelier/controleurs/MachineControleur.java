package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;
import javafx.stage.Stage;

public class MachineControleur {
    private ArrayList<Machine> machines; // la liste des machines existantes, idéalement injectée via constructeur
    private PrincipalVue principalVue; // référence à la vue principale

    public MachineControleur(ArrayList<Machine> machines, PrincipalVue principalVue) {
        this.machines = machines;
        this.principalVue = principalVue;
    }

    public void creationMachine(String refMach, String dMach, float posX, float posY,
            float coutHoraire, float dureeUtil, EtatMachine etatMach) {
        // Vérification que la référence n'existe pas déjà
        if (machines.stream().anyMatch(m -> m.getRefEquipement().equals(refMach))) {
            return;
        }

        // Création de la nouvelle machine
        Machine machine = new Machine(refMach, dMach, posX, posY, coutHoraire, dureeUtil, etatMach);
        machines.add(machine);

        // Mise à jour de l'affichage de l'atelier

        principalVue.dessinerAtelier();
        principalVue.afficherNotif("Machine", null, principalVue.getRootContainer());

    }

    // ===========================================================================
    // Méthodes fenêtre de création de machine

    // public void creerMachine(TextField refMachField, TextField dMachField,
    // TextField posXField,
    // TextField posYField, TextField coutHoraireField, TextField dureeUtilField,
    // ArrayList<Machine> machines, Pane planAtelier, TextField dMach,
    // TextField coutHMach, TextField dureeMach, ComboBox<EtatMachine> etatMach,
    // TextField refMach) {
    // try {
    // // Vérification que tous les champs sont remplis
    // if (refMachField.getText().isEmpty() || dMachField.getText().isEmpty() ||
    // posXField.getText().isEmpty()
    // || posYField.getText().isEmpty() || coutHoraireField.getText().isEmpty()
    // || dureeUtilField.getText().isEmpty()) {
    // throw new Exception("Veuillez remplir tous les champs");
    // }

    // // Vérification que la référence n'existe pas déjà
    // if (machines.stream().anyMatch(m ->
    // m.getRefEquipement().equals(refMachField.getText()))) {
    // throw new Exception("La machine existe déjà");
    // }

    // // Récupération des valeurs des champs
    // String refMachine = refMachField.getText();
    // String dMachine = dMachField.getText();
    // float posX = Float.parseFloat(posXField.getText());
    // float posY = Float.parseFloat(posYField.getText());
    // float coutHoraire = Float.parseFloat(coutHoraireField.getText());
    // float dureeUtil = Float.parseFloat(dureeUtilField.getText());

    // // Création de la nouvelle machine et ajout à la liste
    // Machine machine = new Machine(refMachine, dMachine, posX, posY, coutHoraire,
    // dureeUtil,
    // EtatMachine.DISPONIBLE);
    // machines.add(machine);

    // // Réinitialisation des champs de saisie
    // refMachField.clear();
    // dMachField.clear();
    // posXField.clear();
    // posYField.clear();
    // coutHoraireField.clear();
    // dureeUtilField.clear();

    // // Mise à jour de l'affichage de l'atelier
    // Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach,
    // etatMach, refMach);
    // } catch (Exception ex) {
    // // Gestion des erreurs avec des alertes appropriées
    // if (ex.getMessage().equals("La machine existe déjà")) {
    // Alert alert = new Alert(AlertType.ERROR);
    // alert.setTitle("Erreur");
    // alert.setHeaderText("Machine déjà existante");
    // alert.setContentText("La machine existe déjà.");
    // alert.showAndWait();
    // return;
    // } else if (ex.getMessage().equals("Veuillez remplir tous les champs")) {
    // Alert alert = new Alert(AlertType.ERROR);
    // alert.setTitle("Erreur");
    // alert.setHeaderText("Champs vides");
    // alert.setContentText("Veuillez remplir tous les champs.");
    // alert.showAndWait();
    // return;
    // }
    // }
    // }

    // ---------------------------------------------------------------------------

    public static void closeWindow(Stage creerMachineStage) {
        creerMachineStage.close();
    }

    // ---------------------------------------------------------------------------

    public boolean machineExistante(String refMach) {
        if (machines.stream().anyMatch(m -> m.getRefEquipement().equals(refMach))) {
            return true;
        } else {
            return false;
        }
    }

    // ===========================================================================

}
