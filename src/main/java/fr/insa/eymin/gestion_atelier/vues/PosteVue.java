package fr.insa.eymin.gestion_atelier.vues;

import java.util.ArrayList;

import atlantafx.base.theme.Styles;
import fr.insa.eymin.gestion_atelier.controleurs.PosteControleur;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PosteVue {
    private PosteControleur controleur;

    public PosteVue(PosteControleur controleur) {
        this.controleur = controleur;
    }

    public void afficherFenetreCreation() {
        Stage creerPosteStage = new Stage();
        creerPosteStage.setTitle("Nouveau poste de travail");

        // Champs de saisie
        TextField refPosteField = new TextField();
        refPosteField.setPromptText("Référence du poste");
        TextField dPosteField = new TextField();
        dPosteField.setPromptText("Désignation du poste");

        // Liste des machines disponibles
        ListView<String> machinesListView = new ListView<>();
        machinesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        machinesListView.getStyleClass().add(Styles.STRIPED);

        // Remplir la liste des machines
        ArrayList<Machine> machines = controleur.getMachines();
        ArrayList<String> nomsMachines = new ArrayList<>();
        for (Machine m : machines) {
            nomsMachines.add(m.getRefEquipement());
        }
        machinesListView.setItems(FXCollections.observableArrayList(nomsMachines));

        // Boutons
        Button creerButton = new Button("Créer");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button terminerButton = new Button("Terminer");

        // Action du bouton Créer
        creerButton.setOnAction(e -> {
            if (refPosteField.getText().isEmpty() || dPosteField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Champs vides");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.showAndWait();
                return;
            }

            if (controleur.posteExistant(refPosteField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Poste existant");
                alert.setContentText("Un poste avec cette référence existe déjà.");
                alert.showAndWait();
                return;
            }

            // Récupérer les machines sélectionnées
            ArrayList<Machine> machinesSelectionnees = new ArrayList<>();
            for (String nomMachine : machinesListView.getSelectionModel().getSelectedItems()) {
                for (Machine m : machines) {
                    if (m.getRefEquipement().equals(nomMachine)) {
                        machinesSelectionnees.add(m);
                        break;
                    }
                }
            }

            // Créer le poste
            controleur.ajouterPoste(refPosteField.getText(), dPosteField.getText(), machinesSelectionnees);

            // Réinitialiser les champs
            refPosteField.clear();
            dPosteField.clear();
            machinesListView.getSelectionModel().clearSelection();
        });

        // Action du bouton Terminer
        terminerButton.setOnAction(e -> {
            PosteControleur.fermerFenetre(creerPosteStage);
        });

        // Layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        // Ajouter les composants au layout
        gridPane.add(new Label("Référence:"), 0, 0);
        gridPane.add(refPosteField, 1, 0);
        gridPane.add(new Label("Désignation:"), 0, 1);
        gridPane.add(dPosteField, 1, 1);
        gridPane.add(new Label("Machines:\n(Sélectionner\nplusieurs machines\navec CTRL+clic)"), 0, 2);
        gridPane.add(machinesListView, 1, 2);

        HBox buttonBox = new HBox(10, terminerButton, creerButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox mainLayout = new VBox(20, gridPane, buttonBox);
        mainLayout.setPadding(new Insets(10));

        // Scène
        Scene scene = new Scene(mainLayout);
        creerPosteStage.setScene(scene);
        creerPosteStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        creerPosteStage.initModality(Modality.APPLICATION_MODAL);
        creerPosteStage.show();
    }

    // Méthode statique temporaire pour la compatibilité avec le code existant
    public static void fenetreCreationPoste(ArrayList<Machine> machines, ArrayList<Poste> postes) {
        PosteControleur controleur = new PosteControleur(postes, machines);
        PosteVue vue = new PosteVue(controleur);
        vue.afficherFenetreCreation();
    }
}
