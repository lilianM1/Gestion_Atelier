package fr.insa.eymin.gestion_atelier.vues;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import fr.insa.eymin.gestion_atelier.controleurs.PrincipalControleur;
import fr.insa.eymin.gestion_atelier.modeles.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;

/**
 * Classe Gui qui gère l'interface graphique de l'application de gestion
 * d'atelier
 * Cette classe contient les méthodes pour l'affichage et l'interaction avec
 * l'interface utilisateur
 */
public class PrincipalVue {
    // ========================== Attributs ================================
    // Cette section est vide dans le code original, prête pour de futurs attributs

    // ========================== Constructeurs ============================
    // Cette section est vide dans le code original, prête pour de futurs
    // constructeurs

    // ========================== Méthodes =================================
    /**
     * Méthode qui affiche la fenêtre principale de l'application
     * Crée et configure l'interface utilisateur avec menus, boutons et zones
     * d'affichage
     */
    public static void mainWindow() {
        // Création de la fenêtre principale
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Gestion d'atelier");

        // Création d'un panneau pour visualiser l'atelier
        Pane planAtelier = new Pane();

        // Création des champs de texte pour afficher les informations de la machine
        // sélectionnée
        // Ces champs sont initialement en lecture seule
        TextField refMach = new TextField();
        refMach.setEditable(false);
        refMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de référence, on le sélectionne
            PrincipalControleur.selectChamp(refMach);
        });
        TextField dMach = new TextField();
        dMach.setEditable(false);
        dMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de désignation, on le sélectionne
            PrincipalControleur.selectChamp(dMach);
        });
        TextField coutHMach = new TextField();
        coutHMach.setEditable(false);
        coutHMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de coût horaire, on le sélectionne
            PrincipalControleur.selectChamp(coutHMach);
        });
        TextField dureeMach = new TextField();
        dureeMach.setEditable(false);
        dureeMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de durée d'utilisation, on le sélectionne
            PrincipalControleur.selectChamp(dureeMach);
        });

        // ComboBox pour l'état de la machine, initialement désactivée
        ComboBox<EtatMachine> etatMach = new ComboBox<EtatMachine>();
        etatMach.getItems().addAll(EtatMachine.values());
        etatMach.setDisable(true);

        // Création de la barre de menu principale
        MenuBar menuBar = new MenuBar();

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Gestion" =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        Menu gestionMenu = new Menu("Gestion");
        Menu sousMenuNouveau = new Menu("Nouveau");
        Menu sousMenuAfficher = new Menu("Afficher");

        // ------------------------- Sous-menu "Nouveau" -------------------------
        // Option pour créer un nouveau produit
        MenuItem nouveauProduit = new MenuItem("Produit");
        ArrayList<Produit> produits = new ArrayList<Produit>();
        nouveauProduit.setOnAction(e -> {
            PrincipalControleur.creerProduit(produits);
        });

        // Option pour créer une nouvelle machine
        MenuItem nouveauMachine = new MenuItem("Machine");
        ArrayList<Machine> machines = new ArrayList<Machine>();
        nouveauMachine.setOnAction(e -> {
            PrincipalControleur.creerMachine(machines, planAtelier, dMach, coutHMach, dureeMach, etatMach,
                    refMach);
        });

        // Option pour créer un nouveau poste de travail
        MenuItem nouveauPoste = new MenuItem("Poste de travail");
        ArrayList<Poste> postes = new ArrayList<Poste>();
        nouveauPoste.setOnAction(e -> {
            PrincipalControleur.creerPoste(postes, machines);
        });

        // Création du sous-menu "Equipement" regroupant machines et postes
        Menu sousMenuNvEq = new Menu("Equipement");
        sousMenuNvEq.getItems().addAll(
                nouveauMachine,
                nouveauPoste);

        // Ajout des items au menu "Nouveau"
        sousMenuNouveau.getItems().addAll(
                nouveauProduit,
                sousMenuNvEq);

        // ------------------------- Sous-menu "Afficher" -------------------------
        // Option pour afficher la liste des machines
        MenuItem afficherMachines = new MenuItem("Machines");
        afficherMachines.setOnAction(e -> {
            PrincipalControleur.afficherMachines(machines);
        });

        // Option pour afficher la liste des produits
        MenuItem afficherProduits = new MenuItem("Produits");
        afficherProduits.setOnAction(e -> {
            PrincipalControleur.afficherProduits(produits);
        });

        // Option pour afficher la liste des postes
        MenuItem afficherPostes = new MenuItem("Postes");
        afficherPostes.setOnAction(e -> {
            PrincipalControleur.afficherPostes(postes);
        });

        // Ajout des items au menu "Afficher"
        sousMenuAfficher.getItems().addAll(
                afficherProduits,
                afficherMachines,
                afficherPostes);

        // ------------------------- Menu "Dessiner" -----------------------------
        // Option pour dessiner l'atelier dans le panneau central
        MenuItem dessinerAtelier = new MenuItem("Dessiner atelier");
        dessinerAtelier.setOnAction(e -> {
            PrincipalControleur.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach,
                    refMach);
        });

        // -----------------------------------------------------------------------
        // Ajout des sous-menus au menu "Gestion"
        gestionMenu.getItems().addAll(
                sousMenuNouveau,
                sousMenuAfficher,
                dessinerAtelier);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Optimisation" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu optimisationMenu = new Menu("Optimisation");

        // Option pour calculer la fiabilité des machines
        MenuItem optimisationItem = new MenuItem("Calcul fiabilité machines");
        optimisationItem.setOnAction(e -> {
            PrincipalControleur.fiabAtelier();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Paramètres" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu parametresMenu = new Menu("Paramètres");

        // Option pour basculer en mode plein écran
        MenuItem fullscreenItem = new MenuItem("Plein écran");
        primaryStage.setFullScreenExitHint(""); // Masque le message d'indication pour quitter le plein écran
        fullscreenItem.setOnAction(e -> {
            PrincipalControleur.pleinEcran(primaryStage);
        });

        parametresMenu.getItems().addAll(
                fullscreenItem);

        // ---------------------------------------------------------------------
        // Ajout des menus à la barre de menu
        menuBar.getMenus().addAll(
                gestionMenu,
                optimisationMenu,
                parametresMenu);

        // Création de la scène principale avec un BorderPane comme layout
        BorderPane fenetre = new BorderPane();

        // Configuration de la partie droite (panneau d'informations et de contrôle)
        // Boutons pour modifier et supprimer des équipements
        Button modifierButton = new Button("Modifier");
        Button supprimerButton = new Button("Supprimer");

        // Menu déroulant pour créer de nouveaux équipements
        MenuButton creerEq = new MenuButton("Nouveau");
        MenuItem creerMachine = new MenuItem("Machine");
        creerMachine.setOnAction(e -> {
            PrincipalControleur.creerMachine(machines, planAtelier, dMach, coutHMach, dureeMach, etatMach,
                    refMach);
        });
        MenuItem creerPoste = new MenuItem("Poste de travail");
        creerPoste.setOnAction(e -> {
            PrincipalControleur.creerPoste(postes, machines);
        });
        creerEq.getItems().addAll(creerMachine, creerPoste);

        // Organisation horizontale des boutons
        HBox boutonsHb = new HBox(modifierButton, supprimerButton, creerEq);
        boutonsHb.setSpacing(10);

        // Panneau d'informations sur l'équipement sélectionné
        VBox infoDroite = new VBox();
        infoDroite.getChildren().addAll(
                new Label("Informations sur l'équipement :"),
                new HBox(new Label("Référence : "), refMach),
                new HBox(new Label("Désignation : "), dMach),
                new HBox(new Label("Coût horaire : "), coutHMach),
                new HBox(new Label("Durée d'utilisation : "), dureeMach),
                new HBox(new Label("Etat : "), etatMach),
                boutonsHb);

        // Variable temporaire pour stocker la référence initiale lors de la
        // modification
        AtomicReference<String> tempRef = new AtomicReference<>();

        // Gestion de l'événement du bouton "Modifier"/"Valider"
        modifierButton.setOnAction(e -> {
            PrincipalControleur.modification(dMach, coutHMach, dureeMach, etatMach, refMach, modifierButton,
                    machines, planAtelier, tempRef);
        });

        // Gestion de l'événement du bouton "Supprimer"
        supprimerButton.setOnAction(e -> {
            PrincipalControleur.suppression(dMach, coutHMach, dureeMach, etatMach, refMach, machines, planAtelier);
        });

        // Stylisation du panneau d'informations
        infoDroite.setStyle("-fx-background-color: #161b22;");
        infoDroite.setPadding(new javafx.geometry.Insets(10));
        infoDroite.setSpacing(10);
        infoDroite.setBorder(new Border(new BorderStroke(javafx.scene.paint.Paint.valueOf("#21262d"),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        VBox.setVgrow(infoDroite, Priority.SOMETIMES);

        // Assemblage final de l'interface
        fenetre.setRight(infoDroite); // Panneau d'informations à droite
        fenetre.setTop(menuBar); // Barre de menu en haut
        fenetre.setCenter(planAtelier); // Plan de l'atelier au centre
        Scene scene = new Scene(fenetre, 1280, 720);

        // Configuration finale et affichage de la fenêtre principale
        Image icon = new Image("file:src\\main\\ressources\\icon.png"); // Icône de l'application
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Démarre en plein écran
        primaryStage.show(); // Affiche la fenêtre
    }
}