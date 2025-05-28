package fr.insa.eymin.gestion_atelier.vues;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import fr.insa.eymin.gestion_atelier.controleurs.PrincipalControleur;
import fr.insa.eymin.gestion_atelier.modeles.*;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import javafx.scene.input.KeyCombination;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.Animations;
import javafx.util.Duration;

/**
 * Classe Gui qui gère l'interface graphique de l'application de gestion
 * d'atelier
 * Cette classe contient les méthodes pour l'affichage et l'interaction avec
 * l'interface utilisateur
 */
public class PrincipalVue extends StackPane {
    // ========================== Attributs ================================
    private Stage primaryStage; // Fenêtre principale de l'application
    private Pane planAtelier; // Plan pour dessiner l'atelier
    private TextField refMach, dMach, coutHMach, dureeMach, posX, posY; // Champs pour les info de la machine
    private ComboBox<EtatMachine> etatMach; // Combo box pour l'état de la machine
    private Button modifierButton; // Bouton pour modifier la machine
    private TreeTableView<Object> gammesTable; // Table pour afficher les gammes

    private ArrayList<Machine> machines = new ArrayList<>();
    private ArrayList<Produit> produits = new ArrayList<>();
    private ArrayList<Poste> postes = new ArrayList<>();
    private ArrayList<Operation> operations = new ArrayList<>();
    private ArrayList<Gamme> gammes = new ArrayList<>();
    private Atelier atelier; // Modèle de l'atelier
    AtomicReference<String> tempRef = new AtomicReference<>();

    private PrincipalControleur controleur;

    private StackPane rootContainer; // Conteneur principal

    // ========================== Constructeurs ============================
    public PrincipalVue() {
        this.planAtelier = new Pane();
        this.controleur = new PrincipalControleur(this, machines, produits, postes, operations, gammes);
        this.rootContainer = new StackPane();
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

        // Champs pour afficher la position de la machine
        posX = new TextField();
        posX.setEditable(false);
        posX.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de position X, on le sélectionne
            PrincipalControleur.selectChamp(posX);
        });
        posY = new TextField();
        posY.setEditable(false);
        posY.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de position Y, on le sélectionne
            PrincipalControleur.selectChamp(posY);
        });

        // ComboBox pour l'état de la machine, initialement désactivée
        etatMach = new ComboBox<EtatMachine>();
        etatMach.getItems().addAll(EtatMachine.values());
        etatMach.setDisable(true);

        // Création de la barre de menu principale
        MenuBar menuBar = new MenuBar();

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Gestion" =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        Menu gestionMenu = new Menu("Gestion");
        Menu sousMenuNouveau = new Menu("Nouveau", new FontIcon(Feather.PLUS));
        Menu sousMenuAfficher = new Menu("Afficher");

        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-= Menu "Fichier" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu fichierMenu = new Menu("Fichier");
        MenuItem nvAtelier = new MenuItem("Nouvel Atelier");
        nvAtelier.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        nvAtelier.setOnAction(e -> {
            controleur.creerNouveauAtelier();
        });
        MenuItem ouvrirAtelier = new MenuItem("Ouvrir Atelier", new FontIcon(Feather.FOLDER));
        ouvrirAtelier.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        ouvrirAtelier.setOnAction(e -> {
            fenetreOuvrir();

        });
        MenuItem saveAtelier = new MenuItem("Enregistrer l'Atelier", new FontIcon(Feather.SAVE));
        saveAtelier.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAtelier.setOnAction(e -> {
            controleur.sauvegarderAtelier(new File(
                    "src\\main\\ressources\\data\\atelier_saves\\"
                            + this.getAtelier().getNomAtelier() + ".txt"));
        });
        MenuItem saveAsAtelier = new MenuItem("Enregistrer une copie sous");
        saveAsAtelier.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN));
        saveAsAtelier.setOnAction(e -> {
            fenetreSaveAs();
        });
        MenuItem quitter = new MenuItem("Quitter");
        quitter.setOnAction(e -> {
            // Action pour quitter l'application
            System.exit(0);
        });

        fichierMenu.getItems().addAll(
                nvAtelier,
                new SeparatorMenuItem(),
                ouvrirAtelier,
                new SeparatorMenuItem(),
                saveAtelier,
                saveAsAtelier,
                new SeparatorMenuItem(),
                quitter);

        // ======================================================================

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

        // Option pour créer une nouvelle opération
        MenuItem nouvelleOperation = new MenuItem("Opération");
        nouvelleOperation.setOnAction(e -> {
            controleur.creerOperation();
        });

        // Option pour créer une nouvelle gamme
        MenuItem nouvelleGamme = new MenuItem("Gamme");
        nouvelleGamme.setOnAction(e -> {
            controleur.creerGamme();
        });

        // Ajout des items au menu "Nouveau"
        sousMenuNouveau.getItems().addAll(
                sousMenuNvEq,
                nouveauProduit,
                nouvelleOperation,
                nouvelleGamme);

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

        // Option pour afficher la liste des opérations
        MenuItem afficherOperations = new MenuItem("Opérations");
        afficherOperations.setOnAction(e -> {
            controleur.afficherOperations();
        });

        // Option pour afficher la liste des gammes
        MenuItem afficherGammes = new MenuItem("Gammes");
        afficherGammes.setOnAction(e -> {
            controleur.afficherGammes();
        });

        // Ajout des items au menu "Afficher"
        sousMenuAfficher.getItems().addAll(
                afficherProduits,
                afficherMachines,
                afficherPostes,
                afficherOperations,
                afficherGammes);

        gestionMenu.getItems().addAll(
                sousMenuNouveau,
                sousMenuAfficher);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Optimisation" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu optimisationMenu = new Menu("Optimisation");

        // Option pour calculer la fiabilité des machines
        MenuItem optimisationItem = new MenuItem("Calcul fiabilité machines",
                new FontIcon(Material2AL.ANALYTICS));
        optimisationItem.setOnAction(e -> {
            controleur.calculerFiabilite();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Paramètres" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu parametresMenu = new Menu("Paramètres");

        // Option pour basculer en mode plein écran
        MenuItem fullscreenItem = new MenuItem("Plein écran", new FontIcon(Feather.MAXIMIZE));
        fullscreenItem.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        primaryStage.setFullScreenExitHint(""); // Masque le message d'indication pour quitter le plein écran
        fullscreenItem.setOnAction(e -> {
            controleur.basculerPleinEcran();
        });

        parametresMenu.getItems().addAll(
                fullscreenItem);

        // ---------------------------------------------------------------------
        // Ajout des menus à la barre de menu
        menuBar.getMenus().addAll(
                fichierMenu,
                gestionMenu,
                optimisationMenu,
                parametresMenu);

        // Création de la scène principale avec un BorderPane comme layout
        BorderPane fenetre = new BorderPane();

        // Configuration de la partie droite (panneau d'informations et de contrôle)
        // Boutons pour modifier et supprimer des équipements
        modifierButton = new Button("Modifier");
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
        MenuItem creerProduit = new MenuItem("Produit");
        creerProduit.setOnAction(e -> {
            controleur.creerProduit();
        });
        MenuItem creerOperation = new MenuItem("Opération");
        creerOperation.setOnAction(e -> {
            controleur.creerOperation();
        });
        MenuItem creerGamme = new MenuItem("Gamme");
        creerGamme.setOnAction(e -> {
            controleur.creerGamme();
        });
        Menu sousMenu = new Menu("Equipement");
        sousMenu.getItems().addAll(creerMachine, creerPoste);

        creerEq.getItems().addAll(sousMenu, creerProduit, creerOperation, creerGamme);

        // Organisation horizontale des boutons
        HBox boutonsHb = new HBox(modifierButton, supprimerButton, creerEq);
        boutonsHb.setSpacing(10);

        // Configuration du bas du panneau d'informations
        VBox basInfo = new VBox();
        gammesTable = new TreeTableView<>();
        ecrireTreeTableView();

        Label titreAtelier = new Label("Atelier : " + atelier.getNomAtelier());
        titreAtelier.getStyleClass().add(Styles.TITLE_4);

        basInfo.getChildren().addAll(titreAtelier, gammesTable);

        // Panneau d'informations sur l'équipement sélectionné
        VBox infoDroite = new VBox();
        Separator separator = new Separator();

        // Ajout des éléments au panneau d'informations
        infoDroite.getChildren().addAll(
                new Label("Informations sur l'équipement :"),
                new HBox(new Label("Référence : "), refMach),
                new HBox(new Label("Désignation : "), dMach),
                new HBox(new Label("Coût horaire : "), coutHMach, new Label(" €")),
                new HBox(new Label("Durée d'utilisation : "), dureeMach, new Label(" h")),
                new HBox(new Label("Position X : "), posX),
                new HBox(new Label("Position Y : "), posY),
                new HBox(new Label("Etat : "), etatMach),
                boutonsHb,
                separator,
                basInfo);

        // Variable temporaire pour stocker la référence initiale lors de la
        // modification
        tempRef = new AtomicReference<>();

        // Gestion de l'événement du bouton "Modifier"/"Valider"
        modifierButton.setOnAction(e -> {
            controleur.modifierMachine(modifierButton, tempRef);
        });

        // Gestion de l'événement du bouton "Supprimer"
        supprimerButton.setOnAction(e -> {
            controleur.supprimerMachine();
        });

        // Stylisation du panneau d'informations
        // infoDroite.setStyle("-fx-background-color:#eef1f5;");
        infoDroite.setStyle("-fx-background-color: #e5e9f0;"); // Couleur de fond
        infoDroite.setPadding(new javafx.geometry.Insets(10));
        infoDroite.setSpacing(10);
        infoDroite.setBorder(new Border(new BorderStroke(
                javafx.scene.paint.Paint.valueOf("#c7ceda"), // couleur
                BorderStrokeStyle.SOLID, // style
                CornerRadii.EMPTY, // rayons des coins
                new BorderWidths(0, 0, 0, 1) // largeurs: haut, droite, bas, gauche
        )));
        VBox.setVgrow(infoDroite, Priority.SOMETIMES);

        // Création d'un context menu pour le plan de l'atelier
        ContextMenu contextMenuPlan = new ContextMenu();
        contextMenuPlan.setAutoHide(true); // Ferme le menu contextuel après une sélection
        MenuItem ajouterMachine = new MenuItem("Ajouter Machine");
        ajouterMachine.setGraphic(new FontIcon(Feather.PLUS));
        ajouterMachine.setOnAction(e -> {
            controleur.creerMachine();
        });
        MenuItem redessinerPlan = new MenuItem("Redessiner Plan");
        redessinerPlan.setGraphic(new FontIcon(Feather.REFRESH_CW));
        redessinerPlan.setOnAction(e -> {
            updateTableView();
            controleur.dessinerAtelier(atelier.getLongX(), atelier.getLongY());
        });

        contextMenuPlan.getItems().addAll(ajouterMachine, new SeparatorMenuItem(), redessinerPlan);
        // Ajout du menu contextuel au plan de l'atelier
        planAtelier.setOnContextMenuRequested(e -> {
            contextMenuPlan.show(planAtelier, e.getScreenX(), e.getScreenY());
            e.consume(); // Empêche la propagation de l'événement
        });
        planAtelier.setOnMouseClicked(e -> {
            if (contextMenuPlan.isShowing()) {
                contextMenuPlan.hide();
            }
        });

        StackPane planWrapper = new StackPane();
        planWrapper.setPadding(new javafx.geometry.Insets(15)); // Espacement intérieur

        planWrapper.getChildren().add(planAtelier); // Ajout du plan de l'atelier dans le StackPane

        SplitPane splitPane = new SplitPane(planWrapper, infoDroite); // Création du SplitPane
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.8); // Position du séparateur

        // Assemblage final de l'interface
        // fenetre.setRight(infoDroite); // Panneau d'informations à droite
        fenetre.setTop(menuBar); // Barre de menu en haut
        // fenetre.setCenter(planAtelier); // Plan de l'atelier au centre
        fenetre.setCenter(splitPane); // Panneau d'informations et plan de l'atelier
        // Créons un StackPane qui contiendra toute l'interface et permettra de
        // superposer des notifications
        rootContainer.getChildren().add(fenetre); // Ajoutez votre BorderPane principal

        // Puis utilisez rootContainer comme racine de votre scène
        Scene scene = new Scene(rootContainer, 1280, 720);

        // Configuration finale et affichage de la fenêtre principale
        Image icon = new Image("file:src\\main\\ressources\\images\\icon.png"); // Icône de l'application
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Démarre en plein écran

        primaryStage.setOnCloseRequest(e -> {
            e.consume(); // Empêche la fermeture immédiate
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de fermeture");
            confirmation.setHeaderText("Voulez-vous vraiment quitter l'application ?");
            confirmation.setContentText("Toutes les modifications non enregistrées seront perdues.");
            ButtonType oui = new ButtonType("Quitter sans savegarder", ButtonData.NO);
            ButtonType non = new ButtonType("Sauvegarder et quitter", ButtonData.YES);
            ButtonType annuler = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
            confirmation.getButtonTypes().setAll(oui, non, annuler);
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == oui) {
                Platform.exit(); // Quitte l'application sans sauvegarder
            } else if (result.isPresent() && result.get() == non) {
                // Sauvegarde l'atelier avant de quitter
                controleur.sauvegarderAtelier(new File(
                        "src\\main\\ressources\\data\\atelier_saves\\"
                                + this.getAtelier().getNomAtelier() + ".txt"));
                Platform.exit(); // Quitte l'application après la sauvegarde
            } else {
                confirmation.close();
            }
        });

        primaryStage.show(); // Affiche la fenêtre

    }

    public void startWindow() {

        // Fenêtre bloquante de sélection : Nouvel atelier vierge / Ouvrir atelier
        Stage selectionStage = new Stage();
        selectionStage.setTitle("Sélection de l'atelier");
        selectionStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        selectionStage.initModality(Modality.APPLICATION_MODAL); // Modale
        selectionStage.initOwner(primaryStage); // Propriétaire de la fenêtre
        selectionStage.setResizable(false); // Non redimensionnable
        selectionStage.setWidth(300); // Largeur de la fenêtre
        selectionStage.setHeight(200); // Hauteur de la fenêtre

        // Création des boutons
        Button nvAtelierButton = new Button("Nouvel Atelier");
        nvAtelierButton.setOnAction(e -> {
            selectionStage.close(); // Ferme la fenêtre de sélection
            controleur.creerNouveauAtelier();
        });
        Button ouvrirAtelierButton = new Button("Ouvrir Atelier");
        ouvrirAtelierButton.setOnAction(e -> {
            fenetreOuvrir();
            selectionStage.close(); // Ferme la fenêtre de sélection
        });
        // Création de la disposition
        VBox layout = new VBox(20);
        layout.getChildren().addAll(nvAtelierButton, ouvrirAtelierButton);
        layout.setAlignment(Pos.CENTER); // Centre le contenu
        layout.setPadding(new javafx.geometry.Insets(10)); // Ajoute des marges
        // Création de la scène
        Scene scene = new Scene(layout);
        selectionStage.setScene(scene); // Définit la scène
        selectionStage.showAndWait(); // Affiche la fenêtre et attend sa fermeture
    }

    public void dessinerAtelier() {
        controleur.dessinerAtelier(atelier.getLongX(), atelier.getLongY());
    }

    // Affiche les détails d'une machine dans les champs de l'interface
    public void afficherDetailsMachine(Machine m) {
        dMach.setText(m.getdEquipement()); // Désignation/nom
        coutHMach.setText(String.valueOf(m.getCoutHoraire())); // Coût horaire
        dureeMach.setText(String.valueOf(m.getDureeUtil())); // Durée d'utilisation
        etatMach.setValue(m.getEtat()); // État actuel (EN_MARCHE, EN_PANNE, etc.)
        posX.setText(String.valueOf(m.getPosX())); // Position X
        posY.setText(String.valueOf(m.getPosY())); // Position Y
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

    public TextField getPosXField() {
        return posX;
    }

    public TextField getPosYField() {
        return posY;
    }

    public ComboBox<EtatMachine> getEtatMach() {
        return etatMach;
    }

    public StackPane getRootContainer() {
        return rootContainer;
    }

    public void setRootContainer(StackPane rootContainer) {
        this.rootContainer = rootContainer;
    }

    public void setAtelier(Atelier atelier) {
        this.atelier = atelier;
    }

    public Atelier getAtelier() {
        return atelier;
    }

    public TreeTableView<Object> getTreeTableView() {
        return gammesTable;
    }

    // Définir l'accessibilité des champs de texte et combobox
    public void setFieldsEditable(boolean editable) {
        dMach.setEditable(editable);
        coutHMach.setEditable(editable);
        dureeMach.setEditable(editable);
        etatMach.setDisable(!editable);
        posX.setEditable(editable);
        posY.setEditable(editable);
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
        posX.clear();
        posY.clear();
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

    public boolean afficherAlerteConfirmation(String titre, String entete, String contenu) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);

        // Attendre la réponse de l'utilisateur
        Optional<ButtonType> result = alert.showAndWait();

        // Retourner true si l'utilisateur a cliqué sur OK, false sinon
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void afficherNotif(String message, Ikon icon, StackPane rootContainer, String typeNotif) {
        final Notification msg = new Notification(
                message,
                icon != null ? new FontIcon(icon) : null);
        msg.getStyleClass().add(Styles.ELEVATED_1);
        if (typeNotif.equals("info")) {
            msg.getStyleClass().addAll(Styles.ACCENT);
        } else if (typeNotif.equals("error")) {
            msg.getStyleClass().addAll(Styles.DANGER);
        } else if (typeNotif.equals("success")) {
            msg.getStyleClass().addAll(Styles.SUCCESS);
        } else if (typeNotif.equals("warning")) {
            msg.getStyleClass().addAll(Styles.WARNING);
        } else {
            msg.getStyleClass().addAll(Styles.ACCENT);
        }

        msg.setMaxWidth(300);

        msg.setPrefHeight(Region.USE_COMPUTED_SIZE);
        msg.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane.setAlignment(msg, Pos.TOP_RIGHT);
        StackPane.setMargin(msg, new javafx.geometry.Insets(10, 10, 0, 0));
        msg.setOnClose(ev -> {
            Timeline out = Animations.slideOutUp(msg, Duration.millis(500));
            out.setOnFinished(f -> rootContainer.getChildren().remove(msg));
            out.playFromStart();
        });

        Timeline in = Animations.slideInDown(msg, Duration.millis(250));
        if (!rootContainer.getChildren().contains(msg)) {
            rootContainer.getChildren().add(msg);
        }
        in.playFromStart();

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(3000));
        delay.setOnFinished(event -> {
            Timeline out = Animations.slideOutUp(msg, Duration.millis(250));
            out.setOnFinished(f -> rootContainer.getChildren().remove(msg));
            out.playFromStart();
        });
        delay.play();

    }

    public void fermerFenetre() {

        // Réinitialise le StackPane root en supprimant tous ses enfants
        if (primaryStage != null) {
            primaryStage.close(); // Ferme la fenêtre principale
            rootContainer = new StackPane();

        }
    }

    public void fenetreSaveAs() {
        // Exécution de la tâche dans le thread de l'interface utilisateur
        Platform.runLater(() -> {
            // Créer un FileChooser pour sauvegarder
            FileChooser fileChooser = new FileChooser();

            // Configurer le FileChooser
            fileChooser.setTitle("Sauvegarder l'atelier sous...");
            fileChooser.setInitialFileName(this.getAtelier().getNomAtelier() + ".txt");

            // Définir le répertoire initial
            File repertoireInitial = new File(
                    "src\\main\\ressources\\data\\atelier_saves\\");
            fileChooser.setInitialDirectory(repertoireInitial);

            // Définir les filtres d'extension
            FileChooser.ExtensionFilter filtreTexte = new FileChooser.ExtensionFilter("Fichiers texte (*.txt)",
                    "*.txt");
            FileChooser.ExtensionFilter filtreTous = new FileChooser.ExtensionFilter("Tous les fichiers (*.*)", "*.*");
            fileChooser.getExtensionFilters().addAll(filtreTexte, filtreTous);
            fileChooser.setSelectedExtensionFilter(filtreTexte);

            // Afficher la boîte de dialogue
            File fichierChoisi = fileChooser.showSaveDialog(this.getPrimaryStage());

            if (fichierChoisi != null) {
                controleur.sauvegarderAtelier(fichierChoisi);
            }
        });
    }

    public void fenetreOuvrir() {
        Platform.runLater(() -> {
            // Créer un FileChooser pour sauvegarder
            FileChooser fileChooser = new FileChooser();

            // Configurer le FileChooser
            fileChooser.setTitle("Charger un atelier");

            // Définir le répertoire initial
            File repertoireInitial = new File(
                    "src\\main\\ressources\\data\\atelier_saves\\");
            fileChooser.setInitialDirectory(repertoireInitial);

            // Définir les filtres d'extension
            FileChooser.ExtensionFilter filtreTexte = new FileChooser.ExtensionFilter("Fichiers texte (*.txt)",
                    "*.txt");
            FileChooser.ExtensionFilter filtreTous = new FileChooser.ExtensionFilter("Tous les fichiers (*.*)", "*.*");
            fileChooser.getExtensionFilters().addAll(filtreTexte, filtreTous);
            fileChooser.setSelectedExtensionFilter(filtreTexte);

            // Afficher la boîte de dialogue
            File fichierChoisi = fileChooser.showOpenDialog(this.getPrimaryStage());
            if (fichierChoisi != null) {
                fermerFenetre();
                controleur.chargerAtelier(fichierChoisi);
            }
        });
    }

    public void updateTableView() {
        this.getTreeTableView().refresh();
    }

    public void ecrireTreeTableView() {
        gammesTable.setShowRoot(false);
        gammesTable.setPlaceholder(new Label("Aucune gamme associée à cet atelier"));
        gammesTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        // Une seule colonne pour "Référence"
        TreeTableColumn<Object, String> refGammeCol = new TreeTableColumn<>("Référence");
        refGammeCol.setCellValueFactory(param -> {
            Object obj = param.getValue().getValue();
            if (obj instanceof Gamme g)
                return new ReadOnlyStringWrapper(g.getRefGamme() + " - " + g.getdGamme());
            if (obj instanceof Operation o)
                return new ReadOnlyStringWrapper(o.getRefOperation() + " - " + o.getdOperation());
            if (obj instanceof Equipement e)
                return new ReadOnlyStringWrapper(e.getRefEquipement() + " - " + e.getdEquipement());
            return new ReadOnlyStringWrapper("");
        });

        TreeTableColumn<Object, String> prodCol = new TreeTableColumn<>("Produit");
        prodCol.setCellValueFactory(param -> {
            Object obj = param.getValue().getValue();
            if (obj instanceof Gamme g && g.getProduit() != null)
                return new ReadOnlyStringWrapper(
                        g.getProduit().getCodeProduit() + " - " + g.getProduit().getdProduit());
            return new ReadOnlyStringWrapper("");
        });

        TreeTableColumn<Object, String> coutCol = new TreeTableColumn<>("Coût");
        coutCol.setCellValueFactory(param -> {
            Object obj = param.getValue().getValue();
            if (obj instanceof Gamme g)
                return new ReadOnlyStringWrapper(String.valueOf(g.calculCoutGamme()) + " €");
            if (obj instanceof Machine m)
                return new ReadOnlyStringWrapper(String.valueOf(m.getCoutHoraire()) + " €/h");
            return new ReadOnlyStringWrapper("");
        });

        TreeTableColumn<Object, String> dureeCol = new TreeTableColumn<>("Durée");
        dureeCol.setCellValueFactory(param -> {
            Object obj = param.getValue().getValue();
            if (obj instanceof Gamme g)
                return new ReadOnlyStringWrapper(String.valueOf(g.calculDureeGamme()) + " h");
            if (obj instanceof Operation o)
                return new ReadOnlyStringWrapper(String.valueOf(o.getDureeOp()) + " h");
            if (obj instanceof Machine m)
                return new ReadOnlyStringWrapper(String.valueOf(m.getDureeUtil()) + " h");
            return new ReadOnlyStringWrapper("");

        });

        gammesTable.getColumns().setAll(refGammeCol, prodCol, coutCol, dureeCol);
        gammesTable.tableMenuButtonVisibleProperty().set(true); // Affiche le bouton de menu de la table

        // Création du root invisible
        TreeItem<Object> root = new TreeItem<>(null);
        for (Gamme gamme : gammes) {
            TreeItem<Object> gammeItem = new TreeItem<>(gamme);
            for (Operation op : gamme.getOperations()) {
                TreeItem<Object> operationItem = new TreeItem<>(op);
                if (op.getRefEquipement() instanceof Poste) {
                    TreeItem<Object> posteItem = new TreeItem<>(op.getRefEquipement());
                    operationItem.getChildren().add(posteItem);
                    for (Equipement eq : ((Poste) op.getRefEquipement()).getMachines()) {
                        TreeItem<Object> equipementItem = new TreeItem<>(eq);
                        posteItem.getChildren().add(equipementItem);
                    }
                } else if (op.getRefEquipement() instanceof Machine) {
                    TreeItem<Object> machineItem = new TreeItem<>(op.getRefEquipement());
                    operationItem.getChildren().add(machineItem);
                }
                gammeItem.getChildren().add(operationItem);
            }

            root.getChildren().add(gammeItem);
        }

        ContextMenu gammeMenu = new ContextMenu();
        MenuItem modifier = new MenuItem("Modifier");
        modifier.setGraphic(new FontIcon(Feather.EDIT));
        modifier.setOnAction(e -> {
            TreeItem<Object> selectedItem = gammesTable.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                afficherAlerte(Alert.AlertType.WARNING, "Aucun élément sélectionné",
                        "Veuillez sélectionner une référence à modifier.",
                        "Sélectionnez une référence dans la table pour la modifier.");
                return;
            } else if (selectedItem.getValue() instanceof Machine m) {
                afficherDetailsMachine(m);
                controleur.modifierMachine(modifierButton, tempRef);
                return;
            } else if (selectedItem.getValue() instanceof Poste p) {
                controleur.modifierPoste(p);
                return;
            } else if (selectedItem.getValue() instanceof Operation o) {
                controleur.modifierOperation(o);
                return;
            } else if (selectedItem.getValue() instanceof Gamme g) {
                controleur.modifierGamme(g);
                return;
            }
        });
        MenuItem suppr = new MenuItem("Supprimer");
        suppr.setGraphic(new FontIcon(Feather.TRASH_2));
        suppr.setOnAction(e -> { // TODO
            TreeItem<Object> selectedItem = gammesTable.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                afficherAlerte(Alert.AlertType.WARNING, "Aucun élément sélectionné",
                        "Veuillez sélectionner une référence à supprimer.",
                        "Sélectionnez une référence dans la table pour la supprimer.");
                return;
            } else if (selectedItem.getValue() instanceof Machine m) {
                afficherDetailsMachine(m);
                controleur.supprimerMachine();
                return;
            } else if (selectedItem.getValue() instanceof Poste p) {
                controleur.supprimerPoste(p);
                return;
            } else if (selectedItem.getValue() instanceof Operation o) {
                controleur.supprimerOperation(o);
                return;
            } else if (selectedItem.getValue() instanceof Gamme g) {
                controleur.supprimerGamme(g);
                return;
            }

        });
        gammeMenu.getItems().addAll(modifier, new SeparatorMenuItem(), suppr);
        gammesTable.setContextMenu(gammeMenu);

        gammesTable.getStyleClass().add(Tweaks.ALT_ICON);
        gammesTable.setRoot(root);
    }

    // Méthode appelée par le contrôleur pour dessiner les machines
    public void afficherMachinesSurPlan(ArrayList<Machine> machines, float longX, float longY) {
        // Effacement du contenu précédent pour éviter les superpositions
        planAtelier.getChildren().clear();

        // Calcul du ratio de l'atelier et du panneau pour préserver les proportions
        double ratioAtelier = longX / longY;
        double ratioPanneau = planAtelier.getWidth() / planAtelier.getHeight();

        double scale;
        double offsetX = 0;
        double offsetY = 0;

        // Choisir l'échelle qui respecte les proportions
        if (ratioAtelier > ratioPanneau) {
            // L'atelier est plus large proportionnellement : on se base sur la largeur
            scale = planAtelier.getWidth() / longX;
            offsetY = (planAtelier.getHeight() - (longY * scale)) / 2;
        } else {
            // L'atelier est plus haut proportionnellement : on se base sur la hauteur
            scale = planAtelier.getHeight() / longY;
            offsetX = (planAtelier.getWidth() - (longX * scale)) / 2;
        }

        // Épaisseur des murs (doit correspondre à celle utilisée dans
        // dessinerMursAtelier)
        double epaisseurMur = 5;

        // Dessiner les murs de l'atelier
        dessinerMursAtelier(longX, longY, scale, offsetX, offsetY);

        // Taille approximative des boutons
        final double largeurBouton = 80;
        final double hauteurBouton = 33;

        // Parcours de toutes les machines à placer sur le plan
        for (Machine m : machines) {
            // Création d'un bouton représentant la machine, identifié par sa référence
            Button machineButton = new Button(m.getRefEquipement());
            machineButton.setMnemonicParsing(false);

            // Définir la taille du bouton
            machineButton.setPrefSize(largeurBouton, hauteurBouton);
            machineButton.setMinSize(largeurBouton, hauteurBouton);
            machineButton.setMaxSize(largeurBouton, hauteurBouton);

            // Création du menu contextuel pour le bouton de la machine
            ContextMenu menuBtnMach = new ContextMenu();
            MenuItem modifierItem = new MenuItem("Modifier");
            modifierItem.setGraphic(new FontIcon(Feather.EDIT)); // Icône pour le menu
            modifierItem.setOnAction(e -> {
                afficherDetailsMachine(m);
                controleur.modifierMachine(modifierButton, tempRef);
            });
            MenuItem supprimerItem = new MenuItem("Supprimer");
            supprimerItem.setGraphic(new FontIcon(Feather.TRASH_2)); // Icône pour le menu
            supprimerItem.setOnAction(e -> {
                afficherDetailsMachine(m);
                controleur.supprimerMachine();
            });

            menuBtnMach.getItems().addAll(modifierItem, new SeparatorMenuItem(), supprimerItem);

            // Configuration de l'action lors du clic: afficher les détails de la machine
            machineButton.setOnAction(e -> {
                afficherDetailsMachine(m);
            });

            machineButton.setContextMenu(menuBtnMach);

            // Calcul de l'espace utilisable à l'intérieur des murs
            double espaceMurX = epaisseurMur / scale;
            double espaceMurY = epaisseurMur / scale;
            double espaceBoutonX = largeurBouton / scale;
            double espaceBoutonY = hauteurBouton / scale;

            // Zone utilisable = taille totale - 2*épaisseur_murs - taille_bouton
            double zoneUtileX = longX - (2 * espaceMurX) - espaceBoutonX;
            double zoneUtileY = longY - (2 * espaceMurY) - espaceBoutonY;

            // Calcul des positions en pourcentage de la zone totale
            double pourcentageX = Math.min(1.0, Math.max(0.0, m.getPosX() / longX));
            double pourcentageY = Math.min(1.0, Math.max(0.0, m.getPosY() / longY));

            // Position finale = décalage_mur + pourcentage * zone_utile
            double positionFinaleX = espaceMurX + (pourcentageX * zoneUtileX);
            double positionFinaleY = espaceMurY + (pourcentageY * zoneUtileY);

            // Application du mapping avec échelle uniforme et centrage
            machineButton.layoutXProperty().bind(
                    planAtelier.widthProperty()
                            .multiply(0)
                            .add(positionFinaleX * scale + offsetX));

            machineButton.layoutYProperty().bind(
                    planAtelier.heightProperty()
                            .multiply(0)
                            .add(positionFinaleY * scale + offsetY));

            // Ajout du bouton au panneau d'affichage
            planAtelier.getChildren().add(machineButton);
        }
    }

    // Nouvelle méthode pour dessiner les murs
    private void dessinerMursAtelier(float longX, float longY, double scale, double offsetX, double offsetY) {
        // Épaisseur des murs
        double epaisseurMur = 5;

        // Couleur des murs
        javafx.scene.paint.Color couleurMur = javafx.scene.paint.Color.DARKGRAY;

        // Calcul des dimensions à l'échelle
        double largeurAtelier = longX * scale;
        double hauteurAtelier = longY * scale;

        // Mur haut
        Rectangle murHaut = new Rectangle(largeurAtelier, epaisseurMur);
        murHaut.setFill(couleurMur);
        murHaut.setX(offsetX);
        murHaut.setY(offsetY);
        planAtelier.getChildren().add(murHaut);

        // Mur bas
        Rectangle murBas = new Rectangle(largeurAtelier, epaisseurMur);
        murBas.setFill(couleurMur);
        murBas.setX(offsetX);
        murBas.setY(offsetY + hauteurAtelier - epaisseurMur);
        planAtelier.getChildren().add(murBas);

        // Mur gauche
        Rectangle murGauche = new Rectangle(epaisseurMur, hauteurAtelier);
        murGauche.setFill(couleurMur);
        murGauche.setX(offsetX);
        murGauche.setY(offsetY);
        planAtelier.getChildren().add(murGauche);

        // Mur droit
        Rectangle murDroit = new Rectangle(epaisseurMur, hauteurAtelier);
        murDroit.setFill(couleurMur);
        murDroit.setX(offsetX + largeurAtelier - epaisseurMur);
        murDroit.setY(offsetY);
        planAtelier.getChildren().add(murDroit);
    }

}