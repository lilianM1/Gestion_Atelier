package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import fr.insa.eymin.gestion_atelier.modeles.Atelier;
import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MachineControleur {
    // ===========================================================================
    // Méthodes fenêtre de création de machine

    public static void creerMachine(TextField refMachField, TextField dMachField, TextField posXField,
            TextField posYField, TextField coutHoraireField, TextField dureeUtilField,
            ArrayList<Machine> machines, Pane planAtelier, TextField dMach,
            TextField coutHMach, TextField dureeMach, ComboBox<EtatMachine> etatMach,
            TextField refMach) {
        try {
            // Vérification que tous les champs sont remplis
            if (refMachField.getText().isEmpty() || dMachField.getText().isEmpty() || posXField.getText().isEmpty()
                    || posYField.getText().isEmpty() || coutHoraireField.getText().isEmpty()
                    || dureeUtilField.getText().isEmpty()) {
                throw new Exception("Veuillez remplir tous les champs");
            }

            // Vérification que la référence n'existe pas déjà
            if (machines.stream().anyMatch(m -> m.getRefEquipement().equals(refMachField.getText()))) {
                throw new Exception("La machine existe déjà");
            }

            // Récupération des valeurs des champs
            String refMachine = refMachField.getText();
            String dMachine = dMachField.getText();
            float posX = Float.parseFloat(posXField.getText());
            float posY = Float.parseFloat(posYField.getText());
            float coutHoraire = Float.parseFloat(coutHoraireField.getText());
            float dureeUtil = Float.parseFloat(dureeUtilField.getText());

            // Création de la nouvelle machine et ajout à la liste
            Machine machine = new Machine(refMachine, dMachine, posX, posY, coutHoraire, dureeUtil,
                    EtatMachine.DISPONIBLE);
            machines.add(machine);

            // Réinitialisation des champs de saisie
            refMachField.clear();
            dMachField.clear();
            posXField.clear();
            posYField.clear();
            coutHoraireField.clear();
            dureeUtilField.clear();

            // Mise à jour de l'affichage de l'atelier
            Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach, refMach);
        } catch (Exception ex) {
            // Gestion des erreurs avec des alertes appropriées
            if (ex.getMessage().equals("La machine existe déjà")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Machine déjà existante");
                alert.setContentText("La machine existe déjà.");
                alert.showAndWait();
                return;
            } else if (ex.getMessage().equals("Veuillez remplir tous les champs")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Champs vides");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.showAndWait();
                return;
            }
        }
    }

    // ---------------------------------------------------------------------------

    public static void closeWindow(Stage creerMachineStage) {
        creerMachineStage.close();
    }

    // ===========================================================================

}
