package fr.insa.eymin.gestion_atelier.classes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Atelier {
    // ========================== Attributs ================================

    private ArrayList<Poste> postes;

    // ========================== Constructeurs ============================

    public Atelier() {
        this.postes = new ArrayList<Poste>();
    }

    public Atelier(ArrayList<Poste> postes) {
        this.postes = postes;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Calcul de la fiabilité des machines de l'atelier

    public static void calculFiabilite() {
        // Initialisation des variables
        LinkedHashMap<String, ArrayList<String>> jours = new LinkedHashMap<>();
        HashMap<String, Integer> tempsFonctionnement = new HashMap<>();
        HashMap<String, Integer> dernierDepart = new HashMap<>();
        HashMap<String, String> lastEvent = new HashMap<>();
        HashMap<String, ArrayList<Float>> fiabiliteMulti = new HashMap<>();
        List<String> machines = new ArrayList<>();

        int tempsTotal = (20 - 6) * 60; // Temps total de fonctionnement de l'atelier

        File maintenanceLog = new File("src\\main\\ressources\\suiviMaintenance.txt");

        // Création du workbook Excel
        String excelFilePath = "src\\main\\ressources\\Fiabilité Machines.xlsx";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fiabilité Machines");
        // Style pour les en-têtes (gras)
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleGris = workbook.createCellStyle();
        styleGris.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleGris.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleFonce = workbook.createCellStyle();
        styleFonce.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        styleFonce.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        try {
            // Lecture du fichier de maintenance
            Scanner scanner = new Scanner(maintenanceLog);
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                String date = ligne.split(" ")[0];
                String refMachine = ligne.split(" ")[2];

                if (!jours.containsKey(date)) {
                    jours.put(date, new ArrayList<>());
                }
                jours.get(date).add(ligne);

                if (!machines.contains(refMachine)) {
                    machines.add(refMachine);
                    dernierDepart.put(refMachine, 6 * 60);
                    lastEvent.put(refMachine, "D");
                    tempsFonctionnement.put(refMachine, 0);
                    fiabiliteMulti.put(refMachine, new ArrayList<>());
                }
            }
            scanner.close();

            // Création des en-têtes
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

            boolean utiliserGris = false; // Commencer avec blanc

            // Traitement des données par jour
            for (String date : jours.keySet()) {
                // Réinitialisation pour chaque jour
                for (String ref : machines) {
                    dernierDepart.put(ref, 6 * 60);
                    lastEvent.put(ref, "D");
                    tempsFonctionnement.put(ref, 0);
                }

                // Traitement des événements
                for (String ligne : jours.get(date)) {
                    String time = ligne.split(" ")[1];
                    String refMachine = ligne.split(" ")[2];
                    String event = ligne.split(" ")[3];

                    int heure = Integer.parseInt(time.split(":")[0]);
                    int minute = Integer.parseInt(time.split(":")[1]);
                    int horaireEvent = heure * 60 + minute;

                    if (event.equals("A")) {
                        int dureeFonct = horaireEvent - dernierDepart.get(refMachine);
                        tempsFonctionnement.put(refMachine, tempsFonctionnement.get(refMachine) + dureeFonct);
                        lastEvent.put(refMachine, event);
                    } else if (event.equals("D")) {
                        dernierDepart.put(refMachine, horaireEvent);
                        lastEvent.put(refMachine, event);
                    }
                }

                // Calcul final pour les machines encore en fonctionnement
                for (String ref : machines) {
                    if (lastEvent.get(ref).equals("D")) {
                        int dureeFonct = 20 * 60 - dernierDepart.get(ref);
                        tempsFonctionnement.put(ref, tempsFonctionnement.get(ref) + dureeFonct);
                    }

                    // Calcul et enregistrement de la fiabilité
                    float fiab = ((float) tempsFonctionnement.get(ref) / tempsTotal) * 100;
                    fiab = Math.round(fiab * 100.0f) / 100.0f;
                    fiabiliteMulti.get(ref).add(fiab);

                    // Écriture dans Excel
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(date);
                    row.createCell(1).setCellValue(ref);
                    row.createCell(2).setCellValue(tempsFonctionnement.get(ref));
                    row.createCell(3).setCellValue(fiab + "%");
                    // Appliquer le style à TOUTES les cellules de la ligne
                    for (int i = 0; i < 4; i++) {
                        Cell cell = row.createCell(i);
                        cell.setCellStyle(utiliserGris ? styleGris : styleFonce);

                        // Remplissage des données
                        switch (i) {
                            case 0:
                                cell.setCellValue(Integer.parseInt(date));
                                break;
                            case 1:
                                cell.setCellValue(ref);
                                break;
                            case 2:
                                cell.setCellValue(tempsFonctionnement.get(ref));
                                break;
                            case 3:
                                cell.setCellValue(fiab + "%");
                                break;
                        }
                    }
                }
                // Alterner pour le prochain jour
                utiliserGris = !utiliserGris;
            }

            // Ligne vide pour séparation
            rowNum++;

            // Tri des machines par fiabilité moyenne décroissante
            Map<String, Float> fiabiliteMoyenne = machines.stream()
                    .collect(Collectors.toMap(
                            ref -> ref,
                            ref -> {
                                float sum = fiabiliteMulti.get(ref).stream().reduce(0f, Float::sum);
                                return Math.round((sum / fiabiliteMulti.get(ref).size()) * 100.0f) / 100.0f;
                            }))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, _) -> e1,
                            LinkedHashMap::new));

            // En-têtes pour les fiabilités moyennes
            headerRow = sheet.createRow(rowNum++);
            headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Machine");
            headerCell.setCellStyle(headerStyle);
            headerCell = headerRow.createCell(1);
            headerCell.setCellValue("Fiabilité moyenne");
            headerCell.setCellStyle(headerStyle);

            // Écriture des fiabilités moyennes triées
            for (Map.Entry<String, Float> entry : fiabiliteMoyenne.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                if (rowNum % 2 == 0) {
                    row.createCell(1).setCellValue(entry.getValue() + "%");
                } else {
                    row.createCell(1).setCellValue(entry.getValue() + "%");
                }
                row.createCell(1).setCellValue(entry.getValue() + "%");
                if (rowNum % 2 == 0) {
                    row.getCell(0).setCellStyle(styleGris);
                    row.getCell(1).setCellStyle(styleGris);
                } else {
                    row.getCell(0).setCellStyle(styleFonce);
                    row.getCell(1).setCellStyle(styleFonce);
                }

            }
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ajuste la largeur des 4 premières colonnes (index 0 à 3)
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écriture du fichier Excel
            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
                System.out.println("Fichier Excel créé avec succès !");
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File excelFile = new File(excelFilePath);
            if (excelFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(excelFile);
                } else {
                    System.out.println("Impossible d'ouvrir le fichier automatiquement (Desktop non supporté)");
                }
            } else {
                System.out.println("Le fichier Excel n'a pas été trouvé à l'emplacement : " + excelFilePath);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier Excel : " + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------------
    // Dessin de l'atelier
    public static void dessinerAtelier(Pane planAtelier, ArrayList<Machine> machines, Label dMach, Label coutHMach,
            Label dureeMach, Label etatMach) {
        for (Machine m : machines) {
            Button machineButton = new Button(m.getRefEquipement());
            machineButton.setOnAction(_ -> {
                dMach.setText(m.getdEquipement());
                coutHMach.setText("Coût horaire : " + m.getCoutHoraire() + " €");
                int h = Integer.parseInt(Float.toString(m.getDureeUtil()).split("\\.")[0]);
                String min = "0." + Float.toString(m.getDureeUtil()).split("\\.")[1];
                min = Integer.toString((int) (Float.parseFloat(min) * 60));
                dureeMach.setText("Durée d'utilisation : " + h + "h" + min + "min");
                etatMach.setText("Etat : " + m.getEtat());
            });
            machineButton.setLayoutX(m.getPosX());
            machineButton.setLayoutY(m.getPosY());
            planAtelier.getChildren().add(machineButton);
        }
    }

    // ========================== Getters/Setters ==========================

    public ArrayList<Poste> getPostes() {
        return postes;
    }

    public void setPostes(ArrayList<Poste> postes) {
        this.postes = postes;
    }

}
