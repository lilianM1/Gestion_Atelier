package fr.insa.eymin.gestion_atelier.classes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe représentant un atelier de production
 * Permet de gérer les postes de travail et de réaliser des analyses de
 * fiabilité des machines
 */
public class Atelier {
    // ========================== Attributs ================================

    /**
     * Liste des postes de travail présents dans l'atelier
     */
    private ArrayList<Poste> postes;

    // ========================== Constructeurs ============================

    /**
     * Constructeur par défaut
     * Initialise un atelier vide
     */
    public Atelier() {
        this.postes = new ArrayList<Poste>();
    }

    /**
     * Constructeur avec liste de postes existante
     * 
     * @param postes Liste de postes à ajouter à l'atelier
     */
    public Atelier(ArrayList<Poste> postes) {
        this.postes = postes;
    }

    // ========================== Méthodes =================================

    /**
     * Calcule la fiabilité des machines de l'atelier à partir des logs de
     * maintenance
     * Génère un fichier Excel avec les résultats de fiabilité par jour et moyennes
     * par machine
     */
    public static void calculFiabilite() {
        // Création d'une fenêtre modale pour afficher la progression du calcul
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle("Calcul...");

        // Ajout d'une barre de progression pour l'interface
        ProgressBar progressBar = new ProgressBar();
        StackPane root = new StackPane(progressBar);
        root.setPrefSize(220, 50);

        Scene scene = new Scene(root);
        loadingStage.setScene(scene);

        // Création d'une tâche asynchrone pour le calcul de fiabilité
        Task<Void> calcul = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Initialisation des structures de données pour le traitement
                LinkedHashMap<String, ArrayList<String>> jours = new LinkedHashMap<>(); // Map des événements par jour
                HashMap<String, Integer> tempsFonctionnement = new HashMap<>(); // Temps de fonctionnement par machine
                HashMap<String, Integer> dernierDepart = new HashMap<>(); // Dernier horaire de démarrage par machine
                HashMap<String, String> lastEvent = new HashMap<>(); // Dernier événement enregistré par machine
                HashMap<String, ArrayList<Float>> fiabiliteMulti = new HashMap<>(); // Historique des fiabilités par
                                                                                    // machine
                List<String> machines = new ArrayList<>(); // Liste des références de machines

                // Calcul du temps total de fonctionnement théorique de l'atelier (de 6h à 20h
                // en minutes)
                int tempsTotal = (20 - 6) * 60; // 14 heures = 840 minutes

                // Création du classeur Excel pour les résultats
                String excelFilePath = "src\\main\\ressources\\Fiabilité Machines.csv";
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Fiabilité Machines");

                // Définition des styles pour le fichier Excel
                // Style pour les en-têtes (gras sur fond gris)
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Style pour l'alternance des lignes (gris clair)
                CellStyle styleGris = workbook.createCellStyle();
                styleGris.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                styleGris.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Style pour l'alternance des lignes (gris foncé)
                CellStyle styleFonce = workbook.createCellStyle();
                styleFonce.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                styleFonce.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                try {
                    // Lecture du fichier de logs de maintenance
                    File maintenanceLog = new File("src\\main\\ressources\\suiviMaintenance.txt");
                    Scanner scanner = new Scanner(maintenanceLog);

                    // Parcours des lignes du fichier
                    while (scanner.hasNextLine()) {
                        String ligne = scanner.nextLine();
                        String date = ligne.split(";")[0]; // Format: JJ
                        String refMachine = ligne.split(";")[2]; // Référence de la machine

                        // Regroupement des événements par jour
                        if (!jours.containsKey(date)) {
                            jours.put(date, new ArrayList<>());
                        }
                        jours.get(date).add(ligne);

                        // Initialisation des structures pour chaque nouvelle machine rencontrée
                        if (!machines.contains(refMachine)) {
                            machines.add(refMachine);
                            dernierDepart.put(refMachine, 6 * 60); // Départ par défaut à 6h00
                            lastEvent.put(refMachine, "D"); // D = Démarrage
                            tempsFonctionnement.put(refMachine, 0); // Initialisation du temps de fonctionnement
                            fiabiliteMulti.put(refMachine, new ArrayList<>());
                        }
                    }
                    scanner.close();

                    // Création des en-têtes du tableau dans le fichier Excel
                    int rowNum = 0;
                    Row headerRow = sheet.createRow(rowNum++);
                    Cell headerCell = headerRow.createCell(0);
                    headerCell.setCellValue("Jour");
                    headerCell.setCellStyle(headerStyle);
                    headerCell = headerRow.createCell(1);
                    headerCell.setCellValue("Machine");
                    headerCell.setCellStyle(headerStyle);
                    headerCell = headerRow.createCell(2);
                    headerCell.setCellValue("Temps de fonctionnement (min)  ");
                    headerCell.setCellStyle(headerStyle);
                    headerCell = headerRow.createCell(3);
                    headerCell.setCellValue("Fiabilité");
                    headerCell.setCellStyle(headerStyle);

                    boolean utiliserGris = false; // Indicateur pour l'alternance des couleurs de lignes

                    // Traitement des données jour par jour
                    for (String date : jours.keySet()) {
                        // Réinitialisation des compteurs pour chaque nouveau jour
                        for (String ref : machines) {
                            dernierDepart.put(ref, 6 * 60); // On part du principe que toutes les machines démarrent à
                                                            // 6h
                            lastEvent.put(ref, "D"); // D = Démarrage
                            tempsFonctionnement.put(ref, 0); // Remise à zéro du temps de fonctionnement
                        }

                        // Traitement des événements pour le jour courant
                        for (String ligne : jours.get(date)) {
                            String time = ligne.split(";")[1]; // Format: HH:MM
                            String refMachine = ligne.split(";")[2]; // Référence de la machine
                            String event = ligne.split(";")[3]; // Type d'événement: D (Démarrage) ou A (Arrêt)

                            // Conversion de l'heure en minutes depuis minuit
                            int heure = Integer.parseInt(time.split(":")[0]);
                            int minute = Integer.parseInt(time.split(":")[1]);
                            int horaireEvent = heure * 60 + minute;

                            // Traitement selon le type d'événement
                            if (event.equals("A")) { // Arrêt de la machine
                                // Calcul du temps de fonctionnement depuis le dernier démarrage
                                int dureeFonct = horaireEvent - dernierDepart.get(refMachine);
                                tempsFonctionnement.put(refMachine, tempsFonctionnement.get(refMachine) + dureeFonct);
                                lastEvent.put(refMachine, event);
                            } else if (event.equals("D")) { // Démarrage de la machine
                                dernierDepart.put(refMachine, horaireEvent);
                                lastEvent.put(refMachine, event);
                            }
                        }

                        // Calcul final pour les machines encore en fonctionnement à la fin de la
                        // journée (20h)
                        for (String ref : machines) {
                            if (lastEvent.get(ref).equals("D")) {
                                // Si la dernière action était un démarrage, on compte jusqu'à 20h
                                int dureeFonct = 20 * 60 - dernierDepart.get(ref);
                                tempsFonctionnement.put(ref, tempsFonctionnement.get(ref) + dureeFonct);
                            }

                            // Calcul de la fiabilité (pourcentage du temps de fonctionnement par rapport au
                            // temps total possible)
                            float fiab = ((float) tempsFonctionnement.get(ref) / tempsTotal) * 100;
                            fiab = Math.round(fiab * 100.0f) / 100.0f; // Arrondi à 2 décimales
                            fiabiliteMulti.get(ref).add(fiab); // Stockage pour calcul de moyenne ultérieur

                            // Écriture dans Excel - Création de la ligne
                            Row row = sheet.createRow(rowNum++);

                            // Création des cellules avec style alterné
                            for (int i = 0; i < 4; i++) {
                                Cell cell = row.createCell(i);
                                cell.setCellStyle(utiliserGris ? styleGris : styleFonce);

                                // Remplissage des données selon la colonne
                                switch (i) {
                                    case 0: // Jour
                                        cell.setCellValue(Integer.parseInt(date));
                                        break;
                                    case 1: // Machine
                                        cell.setCellValue(ref);
                                        break;
                                    case 2: // Temps de fonctionnement
                                        cell.setCellValue(tempsFonctionnement.get(ref));
                                        break;
                                    case 3: // Fiabilité
                                        cell.setCellValue(fiab + "%");
                                        break;
                                }
                            }
                        }
                        // Alternance des couleurs pour le prochain jour
                        utiliserGris = !utiliserGris;
                    }

                    // Ligne vide pour séparation
                    rowNum++;

                    // Calcul des fiabilités moyennes par machine et tri décroissant
                    Map<String, Float> fiabiliteMoyenne = machines.stream()
                            .collect(Collectors.toMap(
                                    ref -> ref, // Clé: référence de la machine
                                    ref -> {
                                        // Calcul de la moyenne des fiabilités pour la machine
                                        float sum = fiabiliteMulti.get(ref).stream().reduce(0f, Float::sum);
                                        return Math.round((sum / fiabiliteMulti.get(ref).size()) * 100.0f) / 100.0f;
                                    }))
                            .entrySet().stream()
                            // Tri par ordre décroissant de fiabilité
                            .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                            // Conversion en LinkedHashMap pour préserver l'ordre
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (e1, e2) -> e1,
                                    LinkedHashMap::new));

                    // Création des en-têtes pour la section des fiabilités moyennes
                    headerRow = sheet.createRow(rowNum++);
                    headerCell = headerRow.createCell(0);
                    headerCell.setCellValue("Machine");
                    headerCell.setCellStyle(headerStyle);
                    headerCell = headerRow.createCell(1);
                    headerCell.setCellValue("Fiabilité moyenne");
                    headerCell.setCellStyle(headerStyle);

                    // Écriture des fiabilités moyennes triées par machine
                    for (Map.Entry<String, Float> entry : fiabiliteMoyenne.entrySet()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(entry.getKey());
                        row.createCell(1).setCellValue(entry.getValue() + "%");

                        // Application des styles alternés
                        if (rowNum % 2 == 0) {
                            row.getCell(0).setCellStyle(styleGris);
                            row.getCell(1).setCellStyle(styleGris);
                        } else {
                            row.getCell(0).setCellStyle(styleFonce);
                            row.getCell(1).setCellStyle(styleFonce);
                        }
                    }

                    // Ajustement automatique de la largeur des colonnes
                    for (int i = 0; i < 4; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // Écriture du fichier Excel sur le disque
                    try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                        workbook.write(outputStream);
                        System.out.println("Fichier Excel créé avec succès !");
                    }

                } catch (FileNotFoundException ex) {
                    // Gestion d'erreur: fichier de maintenance non trouvé
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Fichier non trouvé");
                        alert.setContentText(
                                "Le fichier suiviMaintenance.txt n'a pas été trouvé. \nVeuillez le placer dans le dossier src/main/ressources.");
                        alert.showAndWait();
                        loadingStage.close();
                    });
                    workbook.close();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Tentative d'ouverture automatique du fichier Excel généré
                try {
                    File excelFile = new File(excelFilePath);
                    if (excelFile.exists()) {
                        if (Desktop.isDesktopSupported()) {
                            // Ouverture du fichier avec l'application par défaut du système
                            Desktop.getDesktop().open(excelFile);
                        } else {
                            // Message d'erreur si l'API Desktop n'est pas supportée
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur d'ouverture");
                                alert.setContentText(
                                        "Impossible d'ouvrir le fichier automatiquement (Desktop non supporté)");
                                alert.showAndWait();
                            });
                        }
                    } else {
                        // Message d'erreur si le fichier n'existe pas
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur");
                            alert.setHeaderText("Fichier non trouvé");
                            alert.setContentText(
                                    "Le fichier Excel n'a pas été trouvé à l'emplacement : " + excelFilePath);
                            alert.showAndWait();
                        });
                    }
                } catch (IOException e) {
                    // Gestion d'erreur lors de l'ouverture du fichier
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Erreur d'ouverture");
                        alert.setContentText("Erreur lors de l'ouverture du fichier Excel.");
                        alert.showAndWait();
                    });
                } finally {
                    // Fermeture du classeur Excel quoi qu'il arrive
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        // Lier la barre de progression à la tâche
        progressBar.progressProperty().bind(calcul.progressProperty());

        // Fermer la fenêtre une fois la tâche terminée
        calcul.setOnSucceeded(event -> loadingStage.close());

        // Démarrer la tâche dans un nouveau thread
        new Thread(calcul).start();

        // Afficher la fenêtre de progression
        loadingStage.show();
    }

    /**
     * Dessine les machines de l'atelier dans le panneau graphique
     * Permet de sélectionner une machine pour afficher ses informations
     * 
     * @param planAtelier Panneau où dessiner les machines
     * @param machines    Liste des machines à dessiner
     * @param dMach       Champ pour afficher la désignation de la machine
     *                    sélectionnée
     * @param coutHMach   Champ pour afficher le coût horaire de la machine
     *                    sélectionnée
     * @param dureeMach   Champ pour afficher la durée d'utilisation de la machine
     *                    sélectionnée
     * @param etatMach    ComboBox pour afficher l'état de la machine sélectionnée
     * @param refMach     Champ pour afficher la référence de la machine
     *                    sélectionnée
     */
    public static void dessinerAtelier(Pane planAtelier, ArrayList<Machine> machines, TextField dMach,
            TextField coutHMach,
            TextField dureeMach, ComboBox<EtatMachine> etatMach, TextField refMach) {

        planAtelier.getChildren().clear(); // Efface le contenu précédent du panneau
        // Parcours de la liste des machines
        for (Machine m : machines) {
            // Création d'un bouton pour chaque machine
            Button machineButton = new Button(m.getRefEquipement());
            machineButton.setMnemonicParsing(false); // Désactive l'interprétation des caractères mnémoniques

            // Définition de l'action lors du clic sur la machine: afficher ses informations
            machineButton.setOnAction(e -> {
                dMach.setText(m.getdEquipement()); // Désignation
                coutHMach.setText(m.getCoutHoraire() + ""); // Coût horaire
                dureeMach.setText(m.getDureeUtil() + ""); // Durée d'utilisation
                etatMach.setValue(m.getEtat()); // État
                refMach.setText(m.getRefEquipement()); // Référence
            });

            // Positionnement du bouton aux coordonnées de la machine
            machineButton.setLayoutX(m.getPosX());
            machineButton.setLayoutY(m.getPosY());

            // Ajout du bouton au panneau d'atelier
            planAtelier.getChildren().add(machineButton);
        }
    }

    // ========================== Getters/Setters ==========================

    /**
     * Récupère la liste des postes de travail de l'atelier
     * 
     * @return Liste des postes de l'atelier
     */
    public ArrayList<Poste> getPostes() {
        return postes;
    }

    /**
     * Définit la liste des postes de travail de l'atelier
     * 
     * @param postes Nouvelle liste de postes
     */
    public void setPostes(ArrayList<Poste> postes) {
        this.postes = postes;
    }
}