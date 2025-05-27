package fr.insa.eymin.gestion_atelier.vues;

import atlantafx.base.theme.Styles;
import fr.insa.eymin.gestion_atelier.controleurs.MachineControleur;
import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MachineVue extends StackPane {
    private MachineControleur controleur;

    public MachineVue(MachineControleur controleur) {
        this.controleur = controleur;
    }

    public void afficherFenetreCreation() {
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
        ComboBox<EtatMachine> etatMach = new ComboBox<>();
        etatMach.getItems().addAll(EtatMachine.values());

        // Création des labels
        Label refMachLabel = new Label("Référence : ");
        Label dMachLabel = new Label("Désignation : ");
        Label posXLabel = new Label("Abscisse : ");
        Label posYLabel = new Label("Ordonnée : ");
        Label coutHoraireLabel = new Label("Coût horaire : ");
        Label dureeUtilLabel = new Label("Durée (h) : ");
        Label etatMachLabel = new Label("État : ");

        // Création des boutons
        Button creerButton = new Button("Créer");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button terminerButton = new Button("Terminer");

        // Action du bouton Créer
        creerButton.setOnAction(e -> {
            if (refMachField.getText().isEmpty() || dMachField.getText().isEmpty()
                    || posXField.getText().isEmpty() || posYField.getText().isEmpty()
                    || coutHoraireField.getText().isEmpty() || dureeUtilField.getText().isEmpty()
                    || etatMach.getValue() == null) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText("Champs vides");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.showAndWait();
                return;
            } else if (controleur.machineExistante(refMachField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText("Machine déjà existante");
                alert.setContentText(refMachField.getText() + " existe déjà.");
                alert.showAndWait();
                return;
            }

            try {
                float posX = Float.parseFloat(posXField.getText());
                float posY = Float.parseFloat(posYField.getText());
                float coutHoraire = Float.parseFloat(coutHoraireField.getText());
                float dureeUtil = Float.parseFloat(dureeUtilField.getText());

                controleur.creationMachine(
                        refMachField.getText(),
                        dMachField.getText(),
                        posX,
                        posY,
                        coutHoraire,
                        dureeUtil,
                        etatMach.getValue());

                // Réinitialisation des champs de saisie
                refMachField.clear();
                dMachField.clear();
                posXField.clear();
                posYField.clear();
                coutHoraireField.clear();
                dureeUtilField.clear();
                etatMach.setValue(null);

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setHeaderText("Valeurs numériques invalides");
                alert.setContentText("Veuillez entrer des nombres valides pour les champs numériques.");
                alert.showAndWait();
            }
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
        layoutChamps.add(etatMachLabel, 0, 6);
        layoutChamps.add(etatMach, 1, 6);

        // Organisation des boutons et des champs dans le layout
        layoutBoutons.getChildren().addAll(terminerButton, creerButton);
        layout.getChildren().addAll(layoutChamps, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création et configuration de la scène
        Scene scene = new Scene(layout);

        // Configuration et affichage de la fenêtre
        creerMachineStage.initModality(Modality.APPLICATION_MODAL);
        creerMachineStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        creerMachineStage.setScene(scene);
        creerMachineStage.show();
    }

}
