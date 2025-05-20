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
    private Stage primaryStage; // Fenêtre principale de l'application
    private Pane planAtelier; // Plan pour dessiner l'atelier
    private TextField refMach, dMach, coutHMach, dureeMach; // Champs pour les info de la machine
    private ComboBox<EtatMachine> etatMach; // Combo box pour l'état de la machine

    private ArrayList<Machine> machines = new ArrayList<>();
    private ArrayList<Produit> produits = new ArrayList<>();
    private ArrayList<Poste> postes = new ArrayList<>();

    private PrincipalControleur controleur;

    // ========================== Constructeurs ============================
    public PrincipalVue() {
        this.planAtelier = new Pane();
        this.controleur = new PrincipalControleur(this, machines, produits, postes);
    }

    // ========================== Méthodes =================================
    /**
     * Méthode qui affiche la fenêtre principale de l'application
     * Crée et configure l'interface utilisateur avec menus, boutons et zones
     * d'affichage
     */
    public void mainWindow() {
        // Création de la fenêtre principale
        primaryStage = new Stage();
        primaryStage.setTitle("Gestion d'atelier");

        // Création des champs de texte pour afficher les informations de la machine
        // sélectionnée
        // Ces champs sont initialement en lecture seule
        refMach = new TextField();
        refMach.setEditable(false);
        refMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de référence, on le sélectionne
            PrincipalControleur.selectChamp(refMach);
        });
        dMach = new TextField();
        dMach.setEditable(false);
        dMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de désignation, on le sélectionne
            PrincipalControleur.selectChamp(dMach);
        });
        coutHMach = new TextField();
        coutHMach.setEditable(false);
        coutHMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de coût horaire, on le sélectionne
            PrincipalControleur.selectChamp(coutHMach);
        });
        dureeMach = new TextField();
        dureeMach.setEditable(false);
        dureeMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de durée d'utilisation, on le sélectionne
            PrincipalControleur.selectChamp(dureeMach);
        });

        // ComboBox pour l'état de la machine, initialement désactivée
        etatMach = new ComboBox<EtatMachine>();
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
        nouveauProduit.setOnAction(e -> {
            controleur.creerProduit();
        });

        // Option pour créer une nouvelle machine
        MenuItem nouveauMachine = new MenuItem("Machine");
        nouveauMachine.setOnAction(e -> {
            controleur.creerMachine();
        });

        // Option pour créer un nouveau poste de travail
        MenuItem nouveauPoste = new MenuItem("Poste de travail");
        nouveauPoste.setOnAction(e -> {
            controleur.creerPoste();
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
            controleur.afficherMachines();
        });

        // Option pour afficher la liste des produits
        MenuItem afficherProduits = new MenuItem("Produits");
        afficherProduits.setOnAction(e -> {
            controleur.afficherProduits();
        });

        // Option pour afficher la liste des postes
        MenuItem afficherPostes = new MenuItem("Postes");
        afficherPostes.setOnAction(e -> {
            controleur.afficherPostes();
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
            controleur.dessinerAtelier();
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
            controleur.calculerFiabilite();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Paramètres" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu parametresMenu = new Menu("Paramètres");

        // Option pour basculer en mode plein écran
        MenuItem fullscreenItem = new MenuItem("Plein écran");
        primaryStage.setFullScreenExitHint(""); // Masque le message d'indication pour quitter le plein écran
        fullscreenItem.setOnAction(e -> {
            controleur.basculerPleinEcran();
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
            controleur.creerMachine();
        });
        MenuItem creerPoste = new MenuItem("Poste de travail");
        creerPoste.setOnAction(e -> {
            controleur.creerPoste();
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
            controleur.modifierMachine(modifierButton, tempRef);
        });

        // Gestion de l'événement du bouton "Supprimer"
        supprimerButton.setOnAction(e -> {
            controleur.supprimerMachine();
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

    public void dessinerAtelier() {
        controleur.dessinerAtelier();
    }

    // Méthode appelée par le contrôleur pour dessiner les machines
    public void afficherMachinesSurPlan(ArrayList<Machine> machines) {
        // Effacement du contenu précédent pour éviter les superpositions
        planAtelier.getChildren().clear();
        // Parcours de toutes les machines à placer sur le plan
        for (Machine m : machines) {
            // Création d'un bouton représentant la machine, identifié par sa référence
            Button machineButton = new Button(m.getRefEquipement());
            machineButton.setMnemonicParsing(false); // Désactive l'interprétation des caractères spéciaux (ex: '_')

            // Configuration de l'action lors du clic: afficher les détails de la machine
            machineButton.setOnAction(e -> {
                afficherDetailsMachine(m);
            });

            // Positionnement du bouton sur le plan selon les coordonnées de la machine
            machineButton.setLayoutX(m.getPosX());
            machineButton.setLayoutY(m.getPosY());

            // Ajout du bouton au panneau d'affichage
            planAtelier.getChildren().add(machineButton);
        }
    }

    // Affiche les détails d'une machine dans les champs de l'interface
    public void afficherDetailsMachine(Machine m) {
        dMach.setText(m.getdEquipement()); // Désignation/nom
        coutHMach.setText(String.valueOf(m.getCoutHoraire())); // Coût horaire
        dureeMach.setText(String.valueOf(m.getDureeUtil())); // Durée d'utilisation
        etatMach.setValue(m.getEtat()); // État actuel (EN_MARCHE, EN_PANNE, etc.)
        refMach.setText(m.getRefEquipement()); // Référence unique
    }

    // Méthodes pour accéder aux composants d'interface
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Pane getPlanAtelier() {
        return planAtelier;
    }

    public TextField getRefMach() {
        return refMach;
    }

    public TextField getDMach() {
        return dMach;
    }

    public TextField getCoutHMach() {
        return coutHMach;
    }

    public TextField getDureeMach() {
        return dureeMach;
    }

    public ComboBox<EtatMachine> getEtatMach() {
        return etatMach;
    }

    // Définir l'accessibilité des champs de texte et combobox
    public void setFieldsEditable(boolean editable) {
        dMach.setEditable(editable);
        coutHMach.setEditable(editable);
        dureeMach.setEditable(editable);
        etatMach.setDisable(!editable);
        refMach.setEditable(editable);
    }

    // Changer le texte du bouton modifier
    public void setModifierButtonText(Button button, String text) {
        button.setText(text);
    }

    // Méthode pour vider les champs d'information
    public void viderChamps() {
        dMach.clear();
        coutHMach.clear();
        dureeMach.clear();
        refMach.clear();
        etatMach.setValue(null);
    }

    // Affiche une alerte
    public void afficherAlerte(Alert.AlertType type, String titre, String entete, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}