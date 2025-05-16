package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import fr.insa.eymin.gestion_atelier.modeles.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PrincipalControleur {
    public static void selectChamp(TextField champ) {
        champ.selectAll();
    }

    // =================================================================================

    public static void creerProduit(ArrayList<Produit> produits) {
        Produit.creerProduit(produits);
    }

    public static void creerMachine(ArrayList<Machine> machines, Pane planAtelier, TextField dMach,
            TextField coutHMach, TextField dureeMach, ComboBox<EtatMachine> etatMach, TextField refMach) {
        Machine.creerMachine(machines, planAtelier, dMach, coutHMach, dureeMach, etatMach, refMach);
    }

    public static void creerPoste(ArrayList<Poste> postes, ArrayList<Machine> machines) {
        Poste.creerPoste(postes, machines);
    }

    // =================================================================================

    public static void afficherMachines(ArrayList<Machine> machines) {
        for (Machine m : machines) {
            m.afficherMachine();
        }
        System.out.println();
    }

    public static void afficherProduits(ArrayList<Produit> produits) {
        for (Produit p : produits) {
            p.afficherProduit();
        }
        System.out.println();
    }

    public static void afficherPostes(ArrayList<Poste> postes) {
        for (Poste p : postes) {
            p.afficherPoste();
        }
        System.out.println();
    }

    // =================================================================================

    public static void dessinerAtelier(Pane planAtelier, ArrayList<Machine> machines, TextField dMach,
            TextField coutHMach, TextField dureeMach, ComboBox<EtatMachine> etatMach, TextField refMach) {
        Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach, refMach);
    }

    public static void fiabAtelier() {
        Atelier.calculFiabilite();
    }

    // =================================================================================

    public static void pleinEcran(Stage primaryStage) {
        if (primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(false);
        } else {
            primaryStage.setFullScreen(true);
        }
    }

    // =================================================================================

    public static void modification(TextField dMach, TextField coutHMach, TextField dureeMach,
            ComboBox<EtatMachine> etatMach, TextField refMach, Button modifierButton, ArrayList<Machine> machines,
            Pane planAtelier, AtomicReference<String> tempRef) {
        if (dMach.getText().isEmpty()) {
            // Affiche une alerte si aucun équipement n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun équipement sélectionné");
            alert.setContentText("Veuillez sélectionner un équipement à modifier.");
            alert.showAndWait();
        } else {
            if (modifierButton.getText().equals("Modifier")) {
                // Passe en mode édition: active les champs de saisie
                dMach.setEditable(true);
                coutHMach.setEditable(true);
                dureeMach.setEditable(true);
                etatMach.setDisable(false);
                refMach.setEditable(true);
                modifierButton.setText("Valider");
                tempRef.set(refMach.getText()); // Mémorise la référence originale
            } else if (modifierButton.getText().equals("Valider")) {
                // Passe en mode lecture: désactive les champs de saisie et enregistre les
                // modifications
                dMach.setEditable(false);
                coutHMach.setEditable(false);
                dureeMach.setEditable(false);
                etatMach.setDisable(true);
                refMach.setEditable(false);
                modifierButton.setText("Modifier");

                // Met à jour les informations de la machine correspondante
                for (Machine m : machines) {
                    if (m.getRefEquipement().equals(tempRef.get())) {
                        m.setdEquipement(dMach.getText());
                        m.setCoutHoraire(Float.parseFloat(coutHMach.getText()));
                        m.setDureeUtil(Float.parseFloat(dureeMach.getText()));
                        m.setEtat(etatMach.getValue());
                        m.setRefEquipement(refMach.getText());
                    }
                }
                Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach,
                        refMach); // Redessine l'atelier après la modification
            }
        }
    }

    public static void suppression(TextField dMach, TextField coutHMach, TextField dureeMach,
            ComboBox<EtatMachine> etatMach, TextField refMach, ArrayList<Machine> machines, Pane planAtelier) {
        if (dMach.getText().isEmpty()) {
            // Affiche une alerte si aucun équipement n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucun équipement sélectionné");
            alert.setContentText("Veuillez sélectionner un équipement à supprimer.");
            alert.showAndWait();
        } else {
            // Supprime l'équipement sélectionné
            machines.removeIf(m -> m.getRefEquipement().equals(refMach.getText()));
            dMach.clear();
            coutHMach.clear();
            dureeMach.clear();
            refMach.clear();
            etatMach.setValue(null);

            Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach,
                    refMach); // Redessine l'atelier après la suppression
        }
    }
}