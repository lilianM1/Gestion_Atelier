package fr.insa.eymin.gestion_atelier.vues;

import java.util.ArrayList;

import fr.insa.eymin.gestion_atelier.controleurs.MachineControleur;
import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MachineVue {
    // ==========================================================================
    // Méthodes fenêtre de création de machine

    public static void creation(ArrayList<Machine> machines, Pane planAtelier,
            TextField dMach, TextField coutHMach, TextField dureeMach,
            ComboBox<EtatMachine> etatMach, TextField refMach) {

        // Création de la fenêtre
        Stage creerMachineStage = new Stage();
        creerMachineStage.setTitle("Nouvelle machine");

        // Création des champs de texte
        TextField refMachField = new TextField();
        refMachField.setPromptText("refMachine");
        TextField dMachField = new TextField();
        dMachField.setPromptText("dMachine");
        TextField posXField = new TextField();
        posXField.setPromptText("posX");
        TextField posYField = new TextField();
        posYField.setPromptText("posY");
        TextField coutHoraireField = new TextField();
        coutHoraireField.setPromptText("coutHoraire");
        TextField dureeUtilField = new TextField();
        dureeUtilField.setPromptText("dureeUtil");

        // Création des labels
        Label refMachLabel = new Label("Référence : ");
        Label dMachLabel = new Label("Désignation : ");
        Label posXLabel = new Label("Abscisse : ");
        Label posYLabel = new Label("Ordonnée : ");
        Label coutHoraireLabel = new Label("Coût horaire : ");
        Label dureeUtilLabel = new Label("Durée : ");

        // Création des boutons
        Button creerButton = new Button("Créer");
        Button terminerButton = new Button("Terminer");

        // Action du bouton Creer
        creerButton.setOnAction(e -> {
            MachineControleur.creerMachine(refMachField, dMachField, posXField,
                    posYField, coutHoraireField, dureeUtilField, machines, planAtelier,
                    dMach, coutHMach, dureeMach, etatMach, refMach);
        });

        // Action du bouton Terminer : ferme la fenêtre
        terminerButton.setOnAction(e -> {
            MachineControleur.closeWindow(creerMachineStage);
        });

        // Création de la disposition de l'interface
        VBox layout = new VBox(20);
        HBox layoutBoutons = new HBox(20);
        GridPane layoutChamps = new GridPane();

        // Configuration de la grille pour les champs de saisie
        layoutChamps.setHgap(10);
        layoutChamps.setVgap(10);
        layoutChamps.add(refMachLabel, 0, 0);
        layoutChamps.add(refMachField, 1, 0);
        layoutChamps.add(dMachLabel, 0, 1);
        layoutChamps.add(dMachField, 1, 1);
        layoutChamps.add(posXLabel, 0, 2);
        layoutChamps.add(posXField, 1, 2);
        layoutChamps.add(posYLabel, 0, 3);
        layoutChamps.add(posYField, 1, 3);
        layoutChamps.add(coutHoraireLabel, 0, 4);
        layoutChamps.add(coutHoraireField, 1, 4);
        layoutChamps.add(dureeUtilLabel, 0, 5);
        layoutChamps.add(dureeUtilField, 1, 5);

        // Organisation des boutons et des champs dans le layout
        layoutBoutons.getChildren().addAll(terminerButton, creerButton);
        layout.getChildren().addAll(layoutChamps, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création et configuration de la scène
        Scene scene = new Scene(layout);

        // Configuration et affichage de la fenêtre
        creerMachineStage.getIcons().add(new Image("file:src\\main\\ressources\\icon.png"));
        creerMachineStage.setScene(scene);
        creerMachineStage.show();
    }

    // ===============================================================
}
