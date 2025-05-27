package fr.insa.eymin.gestion_atelier.modeles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;

/**
 * Classe représentant un atelier de production industriel.
 * Cette classe fournit les fonctionnalités de gestion des postes de travail,
 * des machines, et d'analyse de leur fiabilité à partir des logs de
 * maintenance.
 */
public class Atelier {
    // ========================== Attributs ================================

    /**
     * Liste des postes de travail présents dans l'atelier.
     * Chaque poste peut contenir plusieurs machines et est impliqué dans
     * différentes opérations de production.
     */

    private String nomAtelier; // Référence unique de l'atelier
    private float longX; // Longueur de l'atelier en X
    private float longY; // Longueur de l'atelier en Y
    private ArrayList<Equipement> equipements;

    private PrincipalVue principalVue; // Référence à la vue principale pour les alertes

    // ========================== Constructeurs ============================

    /**
     * Constructeur par défaut.
     * Initialise un atelier vide sans postes de travail.
     */
    public Atelier(PrincipalVue principalVue) {
        this.principalVue = principalVue; // Référence à la vue principale pour les alertes
        this.equipements = new ArrayList<>();
        this.nomAtelier = "";
        this.longX = 0;
        this.longY = 0;
    }

    /**
     * Constructeur avec paramètres.
     * Permet de créer un atelier avec un nom, une longueur en X et Y, et une
     * liste de postes de travail.
     * 
     * @param nomAtelier  Nom de l'atelier
     * @param longX       Longueur de l'atelier en X
     * @param longY       Longueur de l'atelier en Y
     * @param equipements Liste des équipements (postes et machines) dans l'atelier
     */
    public Atelier(String nomAtelier, float longX, float longY, ArrayList<Equipement> equipements,
            PrincipalVue principalVue) {
        this.principalVue = principalVue; // Référence à la vue principale pour les alertes
        this.equipements = equipements;
        this.nomAtelier = nomAtelier;
        this.longX = longX;
        this.longY = longY;
    }

    // ========================== Méthodes =================================

    /**
     * Calcule la fiabilité des machines à partir des logs de maintenance.
     * Cette méthode:
     * 1. Lit les logs de maintenance des machines
     * 2. Calcule les temps de fonctionnement par jour et par machine
     * 3. Détermine la fiabilité (% du temps de fonctionnement par rapport au temps
     * total)
     */
    public Map<String, Object> calculerFiabilite() throws FileNotFoundException, IOException {
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

        // Temps total de fonctionnement possible par jour (de 6h à 20h = 14h = 840
        // minutes)
        int tempsTotal = (20 - 6) * 60;

        // ----- Lecture et analyse du fichier de logs de maintenance -----
        File maintenanceLog = new File(
                "src\\main\\ressources\\data\\Maintenance-" + principalVue.getAtelier().getNomAtelier() + ".txt");
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
            }
        }

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

        // Préparation des données à retourner
        Map<String, Object> resultats = new HashMap<>();
        resultats.put("jours", jours);
        resultats.put("machines", machines);
        resultats.put("tempsFonctionnement", tempsFonctionnement);
        resultats.put("fiabiliteJournaliere", fiabiliteMulti);
        resultats.put("fiabiliteMoyenne", fiabiliteMoyenne);
        resultats.put("tempsTotal", tempsTotal);

        return resultats;
    }

    /**
     * Génère un rapport Excel avec les résultats du calcul de fiabilité
     */
    public void genererRapportExcel(Map<String, Object> resultats, String excelFilePath) throws IOException {
        // Récupération des données du calcul
        LinkedHashMap<String, ArrayList<String>> jours = (LinkedHashMap<String, ArrayList<String>>) resultats
                .get("jours");
        List<String> machines = (List<String>) resultats.get("machines");
        HashMap<String, Integer> tempsFonctionnement = (HashMap<String, Integer>) resultats.get("tempsFonctionnement");
        HashMap<String, ArrayList<Float>> fiabiliteMulti = (HashMap<String, ArrayList<Float>>) resultats
                .get("fiabiliteJournaliere");
        Map<String, Float> fiabiliteMoyenne = (Map<String, Float>) resultats.get("fiabiliteMoyenne");

        // Création du workbook Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("FiabiliteMachines");

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
        boolean utiliserGris = false; // ----- Écriture des données journalières dans le fichier Excel -----
        // Créer un map pour stocker l'index de traitement des jours (pour corriger
        // l'erreur d'index)
        Map<String, Integer> jourIndex = new HashMap<>();
        int indexJour = 0;
        for (String jour : jours.keySet()) {
            jourIndex.put(jour, indexJour++);
        }

        for (String date : jours.keySet()) {
            for (String ref : machines) {
                int index = jourIndex.get(date);
                float fiab = fiabiliteMulti.get(ref).get(index); // Utilisation de l'index calculé

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
        } finally {
            workbook.close();
        }
    }

    public void firstSaveAtelier(String nomAtelier, float longX, float longY) {
        try (FileWriter writer = new FileWriter("src/main/ressources/data/atelier_saves/" + nomAtelier + ".txt")) {
            writer.write("A;" + nomAtelier + ";" + longX + ";" + longY + "\n");

        } catch (IOException e) {
            e.printStackTrace();
            principalVue.afficherAlerte(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Erreur",
                    "Erreur lors de la sauvegarde de l'atelier",
                    "Une erreur est survenue lors de la sauvegarde de l'atelier : " + e.getMessage());
            return;
        }
    }

    // =========================== Getters/Setters ==========================
    public String getNomAtelier() {
        return nomAtelier;
    }

    public void setNomAtelier(String nomAtelier) {
        this.nomAtelier = nomAtelier;
    }

    public float getLongX() {
        return longX;
    }

    public void setLongX(float longX) {
        this.longX = longX;
    }

    public float getLongY() {
        return longY;
    }

    public void setLongY(float longY) {
        this.longY = longY;
    }

    public void setEquipements(ArrayList<Equipement> equipements) {
        this.equipements = equipements;
    }

    public ArrayList<Equipement> getEquipements() {
        return equipements;
    }

    public ArrayList<Machine> getMachines() {
        return equipements.stream()
                .filter(e -> e instanceof Machine)
                .map(e -> (Machine) e)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList<Poste> getPostes() {
        return equipements.stream()
                .filter(e -> e instanceof Poste)
                .map(e -> (Poste) e)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void ajouterEquipement(Equipement equipement) {
        equipements.add(equipement);
    }
}
