package fr.insa.eymin.gestion_atelier.vues;

import java.util.ArrayList;

import atlantafx.base.theme.Styles;
import fr.insa.eymin.gestion_atelier.controleurs.GammeControleur;
import fr.insa.eymin.gestion_atelier.modeles.Gamme;
import fr.insa.eymin.gestion_atelier.modeles.Operation;
import fr.insa.eymin.gestion_atelier.modeles.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

public class GammeVue {
    private GammeControleur controleur;

    public GammeVue(GammeControleur controleur) {
        this.controleur = controleur;
    }

    public void afficherFenetreCreation() {
        Stage creerGammeStage = new Stage();
        creerGammeStage.setTitle("Création d'une Gamme");
        creerGammeStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));

        // Champs de saisie
        TextField refField = new TextField();
        refField.setPromptText("Référence de la gamme");
        TextField dField = new TextField();
        dField.setPromptText("Désignation de la gamme");

        // Labels
        Label refLabel = new Label("Référence : ");
        Label dLabel = new Label("Désignation : ");
        Label produitLabel = new Label("Produit associé : ");
        Label operationsLabel = new Label("Opérations : \nSélectionnez plusieurs opérations avec Ctrl+clic");

        ArrayList<Produit> produits = controleur.getProduits();
        ArrayList<String> nomsProduits = new ArrayList<>();
        for (Produit produit : produits) {
            nomsProduits.add(produit.getCodeProduit() + " - " + produit.getdProduit());
        }

        ComboBox<String> produitComboBox = new ComboBox<String>();
        produitComboBox.setPromptText("Produit associé");

        // Remplir le ComboBox avec les produits
        ObservableList<String> produitsObservable = FXCollections.observableArrayList(nomsProduits);
        produitComboBox.setItems(produitsObservable);

        ListView<String> operationsListView = new ListView<>();
        operationsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        operationsListView.getStyleClass().add(Styles.STRIPED);

        // Remplir la liste des opérations
        ArrayList<Operation> operations = controleur.getOperations();
        ArrayList<String> nomsOperations = new ArrayList<>();
        for (Operation op : operations) {
            nomsOperations.add(op.getRefOperation());
        }
        operationsListView.setItems(FXCollections.observableArrayList(nomsOperations));

        // Boutons
        Button creerButton = new Button("Créer");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button terminerButton = new Button("Terminer");

        // Action du bouton Créer
        creerButton.setOnAction(e -> {
            String refGamme = refField.getText();
            String dGamme = dField.getText();
            Produit produitAssocie = null;
            for (Produit produit : produits) {
                if (produitComboBox.getValue() == null) {
                    // Si aucun produit n'est sélectionné, on ne peut pas créer la gamme
                    return;
                }
                if (produit.getCodeProduit().equals(produitComboBox.getValue().split(" - ")[0])) {
                    // Produit trouvé
                    produitAssocie = produit;
                    break;

                }
            }
            ArrayList<Operation> operationsAssociees = new ArrayList<>();
            for (String opRef : operationsListView.getSelectionModel().getSelectedItems()) {
                for (Operation op : operations) {
                    if (op.getRefOperation().equals(opRef)) {
                        operationsAssociees.add(op);
                        break;
                    }
                }
            }

            // Appel du contrôleur pour gérer la création de l'opération
            boolean succes = controleur.creerGamme(refGamme, dGamme, produitAssocie, operationsAssociees);

            if (succes) {
                // Efface les champs si la création a réussi
                refField.clear();
                dField.clear();
                produitComboBox.getSelectionModel().clearSelection();
                operationsListView.getSelectionModel().clearSelection();
            }
        });

        // Action du bouton Terminer
        terminerButton.setOnAction(e -> {
            creerGammeStage.close();
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Ajout des champs de saisie et des boutons au layout
        grid.add(refLabel, 0, 0);
        grid.add(refField, 1, 0);
        grid.add(dLabel, 0, 1);
        grid.add(dField, 1, 1);
        grid.add(produitLabel, 0, 2);
        grid.add(produitComboBox, 1, 2);
        grid.add(operationsLabel, 0, 3);
        grid.add(operationsListView, 1, 3);

        HBox buttonsBox = new HBox(10, terminerButton, creerButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        VBox mainLayout = new VBox(20, grid, buttonsBox);
        mainLayout.setPadding(new Insets(10));

        // Configuration de la scène et de la fenêtre
        Scene scene = new Scene(mainLayout);
        creerGammeStage.setScene(scene);
        creerGammeStage.initModality(Modality.APPLICATION_MODAL);
        creerGammeStage.show();
    }

    public void afficherFenetreModification(Gamme gamme) {
        Stage creerGammeStage = new Stage();
        creerGammeStage.setTitle("Modification d'une Gamme");
        creerGammeStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));

        // Champs de saisie
        TextField refField = new TextField();
        refField.setText(gamme.getRefGamme());
        TextField dField = new TextField();
        dField.setText(gamme.getdGamme());
        // Labels
        Label refLabel = new Label("Référence : ");
        Label dLabel = new Label("Désignation : ");
        Label produitLabel = new Label("Produit associé : ");
        Label operationsLabel = new Label("Opérations : \nSélectionnez plusieurs opérations avec Ctrl+clic");

        ArrayList<Produit> produits = controleur.getProduits();
        ArrayList<String> nomsProduits = new ArrayList<>();
        for (Produit produit : produits) {
            nomsProduits.add(produit.getCodeProduit() + " - " + produit.getdProduit());
        }

        ComboBox<String> produitComboBox = new ComboBox<String>();

        // Remplir le ComboBox avec les produits
        ObservableList<String> produitsObservable = FXCollections.observableArrayList(nomsProduits);
        produitComboBox.setItems(produitsObservable);

        // Sélectionner le produit associé à la gamme

        String produitAssocie = gamme.getProduit().getCodeProduit() + " - "
                + gamme.getProduit().getdProduit();
        produitComboBox.setValue(produitAssocie);

        ListView<String> operationsListView = new ListView<>();
        operationsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        operationsListView.getStyleClass().add(Styles.STRIPED);

        // Remplir la liste des opérations
        ArrayList<Operation> operations = controleur.getOperations();
        ArrayList<String> nomsOperations = new ArrayList<>();
        for (Operation op : operations) {
            nomsOperations.add(op.getRefOperation());
        }
        operationsListView.setItems(FXCollections.observableArrayList(nomsOperations));

        // Sélectionner les opérations associées à la gamme
        operationsListView.getSelectionModel().clearSelection();
        for (Operation op : gamme.getOperations()) {
            if (nomsOperations.contains(op.getRefOperation())) {
                operationsListView.getSelectionModel().select(op.getRefOperation());
            }
        }

        // Boutons
        Button creerButton = new Button("Valider");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button terminerButton = new Button("Annuler");

        // Action du bouton Créer
        creerButton.setOnAction(e -> {
            String refGamme = refField.getText();
            String dGamme = dField.getText();
            Produit prod = null;
            for (Produit produit : produits) {
                if (produitComboBox.getValue() == null) {
                    // Si aucun produit n'est sélectionné, on ne peut pas créer la gamme
                    return;
                }
                if (produit.getCodeProduit().equals(produitComboBox.getValue().split(" - ")[0])) {
                    // Produit trouvé
                    prod = produit;
                    break;

                }
            }
            ArrayList<Operation> operationsAssociees = new ArrayList<>();
            for (String opRef : operationsListView.getSelectionModel().getSelectedItems()) {
                for (Operation op : operations) {
                    if (op.getRefOperation().equals(opRef)) {
                        operationsAssociees.add(op);
                        break;
                    }
                }
            }

            // Appel du contrôleur pour gérer la création de l'opération
            boolean succes = controleur.modifierGamme(gamme, refGamme, dGamme, prod, operationsAssociees);

            if (succes) {
                // ferme la fenêtre si la création a réussi
                creerGammeStage.close();
            }
        });

        // Action du bouton Terminer
        terminerButton.setOnAction(e -> {
            creerGammeStage.close();
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Ajout des champs de saisie et des boutons au layout
        grid.add(refLabel, 0, 0);
        grid.add(refField, 1, 0);
        grid.add(dLabel, 0, 1);
        grid.add(dField, 1, 1);
        grid.add(produitLabel, 0, 2);
        grid.add(produitComboBox, 1, 2);
        grid.add(operationsLabel, 0, 3);
        grid.add(operationsListView, 1, 3);

        HBox buttonsBox = new HBox(10, terminerButton, creerButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        VBox mainLayout = new VBox(20, grid, buttonsBox);
        mainLayout.setPadding(new Insets(10));

        // Configuration de la scène et de la fenêtre
        Scene scene = new Scene(mainLayout);
        creerGammeStage.setScene(scene);
        creerGammeStage.initModality(Modality.APPLICATION_MODAL);
        creerGammeStage.show();
    }

    public void afficherErreur(String titre, String entete, String contenu) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

}
