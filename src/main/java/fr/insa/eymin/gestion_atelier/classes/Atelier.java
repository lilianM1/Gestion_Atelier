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
 * Classe représentant un atelier de production industriel.
 * Cette classe fournit les fonctionnalités de gestion des postes de travail,
 * des machines,
 * et d'analyse de leur fiabilité à partir des logs de maintenance.
 * 
 * @author eymin
 * @version 1.0
 */
public class Atelier {
    // ========================== Attributs ================================

    /**
     * Liste des postes de travail présents dans l'atelier.
     * Chaque poste peut contenir plusieurs machines et est impliqué dans
     * différentes
     * opérations de production.
     */
    private ArrayList<Poste> postes;

    // ========================== Constructeurs ============================

    /**
     * Constructeur par défaut.
     * Initialise un atelier vide sans postes de travail.
     */
    public Atelier() {
        this.postes = new ArrayList<Poste>();
    }

    /**
     * Constructeur avec liste de postes existante.
     * Permet d'initialiser l'atelier avec une configuration prédéfinie de postes.
     * 
     * @param postes Liste de postes à ajouter à l'atelier
     */
    public Atelier(ArrayList<Poste> postes) {
        this.postes = postes;
    }

    // ========================== Méthodes =================================

    /**
     * Analyse et calcule la fiabilité des machines de l'atelier.
     * Cette méthode:
     * 1. Lit les logs de maintenance des machines
     * 2. Calcule les temps de fonctionnement par jour et par machine
     * 3. Détermine la fiabilité (% du temps de fonctionnement par rapport au temps
     * total)
     * 4. Génère un rapport Excel avec les statistiques détaillées et les moyennes
     * par machine
     * 5. Présente les résultats triés par fiabilité décroissante
     * 
     * Le fichier est généré dans src/main/ressources/Fiabilité Machines.csv
     * et ouvert automatiquement une fois créé.
     */
    public static void calculFiabilite() {
        // ---------- Création de l'interface de progression ----------
        // Fenêtre modale qui bloque l'interaction avec l'application principale
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle("Calcul en cours...");

        // Barre de progression pour indiquer visuellement que le traitement est en
        // cours
        ProgressBar progressBar = new ProgressBar();
        StackPane root = new StackPane(progressBar);
        root.setPrefSize(220, 50); // Dimensions de la fenêtre de progression

        Scene scene = new Scene(root);
        loadingStage.setScene(scene);

        // ---------- Définition de la tâche asynchrone de calcul ----------
        Task<Void> calcul = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // ----- Initialisation des structures de données pour l'analyse -----
                // Stocke tous les événements groupés par jour (clé = numéro du jour)
                LinkedHashMap<String, ArrayList<String>> jours = new LinkedHashMap<>();

                // Contient le temps total de fonctionnement de chaque machine (en minutes)
                HashMap<String, Integer> tempsFonctionnement = new HashMap<>();

                // Mémorise l'heure du dernier démarrage de chaque machine
                HashMap<String, Integer> dernierDepart = new HashMap<>();

                // Garde en mémoire le dernier événement (D=démarrage ou A=arrêt) pour chaque
                // machine
                HashMap<String, String> lastEvent = new HashMap<>();

                // Historique des fiabilités journalières de chaque machine
                HashMap<String, ArrayList<Float>> fiabiliteMulti = new HashMap<>();

                // Liste des références de toutes les machines identifiées dans les logs
                List<String> machines = new ArrayList<>();

                // ----- Paramètres de calcul -----
                // Temps total de fonctionnement possible par jour (de 6h à 20h = 14h = 840
                // minutes)
                int tempsTotal = (20 - 6) * 60;

                // ----- Création du fichier Excel pour les résultats -----
                String excelFilePath = "src\\main\\ressources\\Fiabilité Machines.csv";
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Fiabilité Machines");

                // ----- Définition des styles pour le fichier Excel -----
                // Style pour les en-têtes: fond gris foncé avec texte en gras
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
                    // ----- Lecture et analyse du fichier de logs de maintenance -----
                    File maintenanceLog = new File("src\\main\\ressources\\suiviMaintenance.txt");
                    Scanner scanner = new Scanner(maintenanceLog);

                    // Première passe: lecture et préparation des données
                    while (scanner.hasNextLine()) {
                        String ligne = scanner.nextLine();
                        String[] elements = ligne.split(";");

                        // Format du fichier: JJ;HH:MM;REF_MACHINE;ÉVÉNEMENT
                        String date = elements[0]; // Format: JJ (jour)
                        String refMachine = elements[2]; // Référence unique de la machine

                        // Groupement des événements par jour pour traitement ultérieur
                        if (!jours.containsKey(date)) {
                            jours.put(date, new ArrayList<>());
                        }
                        jours.get(date).add(ligne);

                        // Initialisation des structures pour chaque nouvelle machine identifiée
                        if (!machines.contains(refMachine)) {
                            machines.add(refMachine);
                            // Valeurs par défaut: démarrage à 6h00 du matin
                            dernierDepart.put(refMachine, 6 * 60); // Conversion en minutes
                            lastEvent.put(refMachine, "D"); // D = Démarrage initial
                            tempsFonctionnement.put(refMachine, 0); // Pas encore de fonctionnement
                            fiabiliteMulti.put(refMachine, new ArrayList<>()); // Liste vide pour stocker l'historique
                        }
                    }
                    scanner.close();

                    // ----- Création de l'en-tête du tableau dans le fichier Excel -----
                    int rowNum = 0;
                    Row headerRow = sheet.createRow(rowNum++);

                    // Définition des en-têtes de colonnes avec style
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

                    // Drapeau pour alterner les couleurs des lignes
                    boolean utiliserGris = false;

                    // ----- Traitement des données jour par jour -----
                    for (String date : jours.keySet()) {
                        // Réinitialisation des compteurs pour chaque nouveau jour
                        for (String ref : machines) {
                            // Hypothèse: toutes les machines démarrent à 6h00 chaque jour
                            dernierDepart.put(ref, 6 * 60);
                            lastEvent.put(ref, "D"); // État initial: démarrée
                            tempsFonctionnement.put(ref, 0); // Remise à zéro du compteur
                        }

                        // Analyse des événements pour le jour courant
                        for (String ligne : jours.get(date)) {
                            String[] elements = ligne.split(";");
                            String time = elements[1]; // Format: HH:MM
                            String refMachine = elements[2]; // Référence de la machine
                            String event = elements[3]; // Type d'événement: D (Démarrage) ou A (Arrêt)

                            // Conversion de l'heure en minutes depuis minuit
                            String[] timeComponents = time.split(":");
                            int heure = Integer.parseInt(timeComponents[0]);
                            int minute = Integer.parseInt(timeComponents[1]);
                            int horaireEvent = heure * 60 + minute;

                            // Traitement différent selon le type d'événement
                            if (event.equals("A")) { // Arrêt de la machine
                                // Calcul du temps de fonctionnement depuis le dernier démarrage
                                int dureeFonct = horaireEvent - dernierDepart.get(refMachine);
                                // Ajout au compteur global de la machine
                                tempsFonctionnement.put(refMachine, tempsFonctionnement.get(refMachine) + dureeFonct);
                                lastEvent.put(refMachine, event); // Mise à jour de l'état
                            } else if (event.equals("D")) { // Démarrage de la machine
                                dernierDepart.put(refMachine, horaireEvent); // Mémorisation de l'heure de démarrage
                                lastEvent.put(refMachine, event); // Mise à jour de l'état
                            }
                        }

                        // Finalisation des calculs pour les machines encore en fonctionnement à 20h
                        for (String ref : machines) {
                            if (lastEvent.get(ref).equals("D")) {
                                // Si la machine était encore en marche à la fin de la journée,
                                // on compte le temps jusqu'à 20h (fin de journée)
                                int dureeFonct = 20 * 60 - dernierDepart.get(ref);
                                tempsFonctionnement.put(ref, tempsFonctionnement.get(ref) + dureeFonct);
                            }

                            // Calcul du pourcentage de fiabilité: (temps fonctionnement / temps total
                            // possible) * 100
                            float fiab = ((float) tempsFonctionnement.get(ref) / tempsTotal) * 100;
                            fiab = Math.round(fiab * 100.0f) / 100.0f; // Arrondi à 2 décimales
                            fiabiliteMulti.get(ref).add(fiab); // Sauvegarde pour le calcul de moyenne plus tard

                            // ----- Écriture des résultats journaliers dans Excel -----
                            Row row = sheet.createRow(rowNum++);

                            // Création et stylisation des cellules
                            for (int i = 0; i < 4; i++) {
                                Cell cell = row.createCell(i);
                                // Application du style alterné
                                cell.setCellStyle(utiliserGris ? styleGris : styleFonce);

                                // Remplissage des données selon la colonne
                                switch (i) {
                                    case 0: // Jour
                                        cell.setCellValue(Integer.parseInt(date));
                                        break;
                                    case 1: // Référence machine
                                        cell.setCellValue(ref);
                                        break;
                                    case 2: // Temps de fonctionnement (en minutes)
                                        cell.setCellValue(tempsFonctionnement.get(ref));
                                        break;
                                    case 3: // Fiabilité (en pourcentage)
                                        cell.setCellValue(fiab + "%");
                                        break;
                                }
                            }
                        }
                        // Alternance des couleurs pour le prochain jour
                        utiliserGris = !utiliserGris;
                    }

                    // Ligne vide pour séparer les deux sections du rapport
                    rowNum++;

                    // ----- Calcul et tri des fiabilités moyennes par machine -----
                    Map<String, Float> fiabiliteMoyenne = machines.stream()
                            .collect(Collectors.toMap(
                                    ref -> ref, // Clé: référence de la machine
                                    ref -> {
                                        // Calcul de la moyenne des fiabilités journalières
                                        float sum = fiabiliteMulti.get(ref).stream().reduce(0f, Float::sum);
                                        return Math.round((sum / fiabiliteMulti.get(ref).size()) * 100.0f) / 100.0f;
                                    }))
                            .entrySet().stream()
                            // Tri par fiabilité décroissante
                            .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                            // Conversion en LinkedHashMap pour préserver l'ordre de tri
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (e1, e2) -> e1, // En cas de conflit (ne devrait pas arriver)
                                    LinkedHashMap::new));

                    // ----- Création de l'en-tête pour la section des fiabilités moyennes -----
                    headerRow = sheet.createRow(rowNum++);
                    headerCell = headerRow.createCell(0);
                    headerCell.setCellValue("Machine");
                    headerCell.setCellStyle(headerStyle);

                    headerCell = headerRow.createCell(1);
                    headerCell.setCellValue("Fiabilité moyenne");
                    headerCell.setCellStyle(headerStyle);

                    // ----- Écriture des fiabilités moyennes par machine (triées) -----
                    for (Map.Entry<String, Float> entry : fiabiliteMoyenne.entrySet()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(entry.getKey()); // Référence machine
                        row.createCell(1).setCellValue(entry.getValue() + "%"); // Fiabilité moyenne

                        // Application du style alterné
                        if (rowNum % 2 == 0) {
                            row.getCell(0).setCellStyle(styleGris);
                            row.getCell(1).setCellStyle(styleGris);
                        } else {
                            row.getCell(0).setCellStyle(styleFonce);
                            row.getCell(1).setCellStyle(styleFonce);
                        }
                    }

                    // Ajustement automatique de la largeur des colonnes selon leur contenu
                    for (int i = 0; i < 4; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // ----- Enregistrement du fichier Excel sur le disque -----
                    try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                        workbook.write(outputStream);
                        System.out.println("Fichier Excel de fiabilité créé avec succès!");
                    }

                } catch (FileNotFoundException ex) {
                    // Gestion d'erreur: fichier de suivi de maintenance non trouvé
                    ex.printStackTrace();

                    // Affichage d'une alerte sur l'interface utilisateur
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
                    // Gestion des autres erreurs d'entrée/sortie
                    e.printStackTrace();
                }

                // ----- Tentative d'ouverture automatique du fichier Excel généré -----
                try {
                    File excelFile = new File(excelFilePath);
                    if (excelFile.exists()) {
                        if (Desktop.isDesktopSupported()) {
                            // Ouverture du fichier avec l'application par défaut du système
                            Desktop.getDesktop().open(excelFile);
                        } else {
                            // Message d'erreur si l'API Desktop n'est pas supportée par le système
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
                        // Message d'erreur si le fichier n'a pas été créé correctement
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
                    // Fermeture du classeur Excel dans tous les cas pour libérer les ressources
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        // Liaison de la barre de progression à l'état d'avancement de la tâche
        progressBar.progressProperty().bind(calcul.progressProperty());

        // Fermeture automatique de la fenêtre de progression une fois le calcul terminé
        calcul.setOnSucceeded(event -> loadingStage.close());

        // Démarrage de la tâche de calcul dans un thread séparé pour ne pas bloquer
        // l'UI
        new Thread(calcul).start();

        // Affichage de la fenêtre de progression
        loadingStage.show();
    }

    /**
     * Affiche graphiquement les machines de l'atelier dans le panneau spécifié.
     * Cette méthode:
     * 1. Efface l'affichage précédent
     * 2. Crée un bouton pour chaque machine aux coordonnées spécifiées
     * 3. Configure les événements pour afficher les informations détaillées lors de
     * la sélection
     * 
     * @param planAtelier Panneau graphique où dessiner les machines
     * @param machines    Liste des machines à dessiner
     * @param dMach       Champ texte pour afficher la désignation de la machine
     *                    sélectionnée
     * @param coutHMach   Champ texte pour afficher le coût horaire de la machine
     *                    sélectionnée
     * @param dureeMach   Champ texte pour afficher la durée d'utilisation de la
     *                    machine sélectionnée
     * @param etatMach    ComboBox pour afficher/modifier l'état de la machine
     *                    sélectionnée
     * @param refMach     Champ texte pour afficher la référence de la machine
     *                    sélectionnée
     */
    public static void dessinerAtelier(Pane planAtelier, ArrayList<Machine> machines, TextField dMach,
            TextField coutHMach, TextField dureeMach, ComboBox<EtatMachine> etatMach, TextField refMach) {

        // Effacement du contenu précédent pour éviter les superpositions
        planAtelier.getChildren().clear();

        // Parcours de toutes les machines à placer sur le plan
        for (Machine m : machines) {
            // Création d'un bouton représentant la machine, identifié par sa référence
            Button machineButton = new Button(m.getRefEquipement());
            machineButton.setMnemonicParsing(false); // Désactive l'interprétation des caractères spéciaux (ex: '_')

            // Configuration de l'action lors du clic: afficher les détails de la machine
            machineButton.setOnAction(e -> {
                // Remplissage des champs d'information avec les données de la machine
                // sélectionnée
                dMach.setText(m.getdEquipement()); // Désignation/nom
                coutHMach.setText(String.valueOf(m.getCoutHoraire())); // Coût horaire
                dureeMach.setText(String.valueOf(m.getDureeUtil())); // Durée d'utilisation
                etatMach.setValue(m.getEtat()); // État actuel (EN_MARCHE, EN_PANNE, etc.)
                refMach.setText(m.getRefEquipement()); // Référence unique
            });

            // Positionnement du bouton sur le plan selon les coordonnées de la machine
            machineButton.setLayoutX(m.getPosX());
            machineButton.setLayoutY(m.getPosY());

            // Ajout du bouton au panneau d'affichage
            planAtelier.getChildren().add(machineButton);
        }
    }

    // ========================== Getters/Setters ==========================

    /**
     * Récupère la liste des postes de travail présents dans l'atelier.
     * 
     * @return Liste des postes de l'atelier
     */
    public ArrayList<Poste> getPostes() {
        return postes;
    }

    /**
     * Définit une nouvelle liste de postes de travail pour l'atelier.
     * Remplace complètement la liste existante.
     * 
     * @param postes Nouvelle liste de postes à affecter à l'atelier
     */
    public void setPostes(ArrayList<Poste> postes) {
        this.postes = postes;
    }
}