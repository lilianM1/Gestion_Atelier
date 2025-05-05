package fr.insa.eymin.gestion_atelier;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import fr.insa.eymin.gestion_atelier.classes.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Gui {
    // ========================== Attributs ================================

    // ========================== Constructeurs ============================

    // ========================== Méthodes =================================
    // ---------------------------------------------------------------------
    // Affiche la fenête principale
    public static void mainWindow() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Gestion d'atelier");
        Pane planAtelier = new Pane();

        // Création des champs de texte pour afficher les informations de la machine
        TextField refMach = new TextField();
        refMach.setEditable(false);
        TextField dMach = new TextField();
        dMach.setEditable(false);
        TextField coutHMach = new TextField();
        coutHMach.setEditable(false);
        TextField dureeMach = new TextField();
        dureeMach.setEditable(false);
        ComboBox<EtatMachine> etatMach = new ComboBox<EtatMachine>();
        etatMach.getItems().addAll(EtatMachine.values());
        etatMach.setDisable(true);

        // Création de la barre de menu
        MenuBar menuBar = new MenuBar();

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Gestion" =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        Menu gestionMenu = new Menu("Gestion");
        Menu sousMenuNouveau = new Menu("Nouveau");
        Menu sousMenuAfficher = new Menu("Afficher");

        // ------------------------- Sous-menu "Nouveau" -------------------------
        MenuItem nouveauProduit = new MenuItem("Produit");
        ArrayList<Produit> produits = new ArrayList<Produit>();
        nouveauProduit.setOnAction(e -> {
            Produit.creerProduit(produits);
        });

        MenuItem nouveauMachine = new MenuItem("Machine");
        ArrayList<Machine> machines = new ArrayList<Machine>();
        nouveauMachine.setOnAction(e -> {
            Machine.creerMachine(machines, planAtelier, dMach, coutHMach, dureeMach, etatMach, refMach);
        });

        MenuItem nouveauPoste = new MenuItem("Poste de travail");
        ArrayList<Poste> postes = new ArrayList<Poste>();
        nouveauPoste.setOnAction(e -> {
            Poste.creerPoste(postes, machines);
        });

        // Création du sous-menu "Equipement"
        Menu sousMenuNvEq = new Menu("Equipement");
        sousMenuNvEq.getItems().addAll(
                nouveauMachine,
                nouveauPoste);

        // Ajout des items au menu "Nouveau"
        sousMenuNouveau.getItems().addAll(
                nouveauProduit,
                sousMenuNvEq);

        // ------------------------- Sous-menu "Afficher" -------------------------
        MenuItem afficherMachines = new MenuItem("Machines");
        afficherMachines.setOnAction(e -> {
            for (Machine m : machines) {
                m.afficherMachine();
            }
            System.out.println();
        });

        MenuItem afficherProduits = new MenuItem("Produits");
        afficherProduits.setOnAction(e -> {
            for (Produit p : produits) {
                p.afficherProduit();
            }
            System.out.println();
        });

        MenuItem afficherPostes = new MenuItem("Postes");
        afficherPostes.setOnAction(e -> {
            for (Poste poste : postes) {
                poste.afficherPoste();
            }
        });

        // Ajout des items au menu "Afficher"
        sousMenuAfficher.getItems().addAll(
                afficherProduits,
                afficherMachines,
                afficherPostes);

        // ------------------------- Menu "Dessiner" -----------------------------
        MenuItem dessinerAtelier = new MenuItem("Dessiner atelier");
        dessinerAtelier.setOnAction(e -> {
            Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach, refMach);
        });

        // -----------------------------------------------------------------------
        // Ajout des sous-menus au menu "Gestion"
        gestionMenu.getItems().addAll(
                sousMenuNouveau,
                sousMenuAfficher,
                dessinerAtelier);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Optimisation" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu optimisationMenu = new Menu("Optimisation");
        MenuItem optimisationItem = new MenuItem("Calcul fiabilité machines");
        optimisationItem.setOnAction(e -> {
            Atelier.calculFiabilite();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Paramètres" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu parametresMenu = new Menu("Paramètres");

        MenuItem fullscreenItem = new MenuItem("Plein écran");
        primaryStage.setFullScreenExitHint("");
        fullscreenItem.setOnAction(e -> {
            if (primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            } else {
                primaryStage.setFullScreen(true);
            }
        });

        parametresMenu.getItems().addAll(

                fullscreenItem);

        // ---------------------------------------------------------------------
        // Ajout des menus à la barre de menu
        menuBar.getMenus().addAll(
                gestionMenu,
                optimisationMenu,
                parametresMenu);

        // Création de la scène
        BorderPane fenetre = new BorderPane();

        // Configuration de la partie droite
        Button modifierButton = new Button("Modifier");
        Button supprimerButton = new Button("Supprimer");
        MenuButton creerEq = new MenuButton("Nouveau");
        MenuItem creerMachine = new MenuItem("Machine");
        creerMachine.setOnAction(e -> {
            Machine.creerMachine(machines, planAtelier, dMach, coutHMach, dureeMach, etatMach,
                    refMach);
        });
        MenuItem creerPoste = new MenuItem("Poste de travail");
        creerPoste.setOnAction(e -> {
            Poste.creerPoste(postes, machines);
        });
        creerEq.getItems().addAll(creerMachine, creerPoste);
        HBox boutonsHb = new HBox(modifierButton, supprimerButton, creerEq);
        boutonsHb.setSpacing(10);

        VBox infoDroite = new VBox();
        infoDroite.getChildren().addAll(
                new Label("Informations sur l'équipement :"),
                new HBox(new Label("Référence : "), refMach),
                new HBox(new Label("Désignation : "), dMach),
                new HBox(new Label("Coût horaire : "), coutHMach),
                new HBox(new Label("Durée d'utilisation : "), dureeMach),
                new HBox(new Label("Etat : "), etatMach),
                boutonsHb);

        AtomicReference<String> tempRef = new AtomicReference<>();
        modifierButton.setOnAction(e -> {
            if (dMach.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement");
                alert.setHeaderText("Aucun équipement sélectionné");
                alert.setContentText("Veuillez sélectionner un équipement à modifier.");
                alert.showAndWait();
            } else {
                if (modifierButton.getText().equals("Modifier")) {
                    dMach.setEditable(true);
                    coutHMach.setEditable(true);
                    dureeMach.setEditable(true);
                    etatMach.setDisable(false);
                    refMach.setEditable(true);
                    modifierButton.setText("Valider");
                    tempRef.set(refMach.getText());
                } else if (modifierButton.getText().equals("Valider")) {
                    dMach.setEditable(false);
                    coutHMach.setEditable(false);
                    dureeMach.setEditable(false);
                    etatMach.setDisable(true);
                    refMach.setEditable(false);
                    modifierButton.setText("Modifier");
                    for (Machine m : machines) {
                        System.out.println(m.getRefEquipement());
                        if (m.getRefEquipement().equals(tempRef.get())) {
                            m.setdEquipement(dMach.getText());
                            m.setCoutHoraire(Float.parseFloat(coutHMach.getText()));
                            m.setDureeUtil(Float.parseFloat(dureeMach.getText()));
                            m.setEtat(etatMach.getValue());
                            m.setRefEquipement(refMach.getText());
                        }
                    }
                }
            }
        });

        infoDroite.setStyle("-fx-background-color: #161b22;");
        infoDroite.setPadding(new javafx.geometry.Insets(10));
        infoDroite.setSpacing(10);
        infoDroite.setBorder(new Border(new BorderStroke(javafx.scene.paint.Paint.valueOf("#21262d"),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        VBox.setVgrow(infoDroite, Priority.SOMETIMES);

        // Création de la disposition
        fenetre.setRight(infoDroite);
        fenetre.setTop(menuBar);
        fenetre.setCenter(planAtelier);
        Scene scene = new Scene(fenetre, 1280, 720);

        // Définition de la scène pour la fenêtre principale
        Image icon = new Image("file:src\\main\\ressources\\icon.png"); // Assurez-vous que le chemin est correct
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

}
