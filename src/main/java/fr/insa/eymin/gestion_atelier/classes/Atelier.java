package fr.insa.eymin.gestion_atelier.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
        LinkedHashMap<String, ArrayList<String>> jours = new LinkedHashMap<String, ArrayList<String>>();
        LinkedHashMap<String, Float> fiabTri = new LinkedHashMap<String, Float>();
        HashMap<String, Integer> tempsFonctionnement = new HashMap<String, Integer>();
        HashMap<String, Integer> dernierDepart = new HashMap<String, Integer>();
        HashMap<String, String> lastEvent = new HashMap<String, String>();
        HashMap<String, Float> fiabiliteJour = new HashMap<String, Float>();
        HashMap<String, ArrayList<Float>> fiabiliteMulti = new HashMap<String, ArrayList<Float>>();
        HashMap<String, Float> fiabiliteTotal = new HashMap<String, Float>();
        LinkedHashMap<String, Float> fiabiliteMoyenne = new LinkedHashMap<String, Float>();

        int tempsTotal = (20 - 6) * 60; // Temps total de fonctionnement de l'atelier

        File maintenanceLog = new File("src\\main\\ressources\\suiviMaintenance.txt"); // Fichier de maintenance
        try {

            // Initialisation des variables
            int e = 0;
            boolean ajouter = true;
            ArrayList<String> M = new ArrayList<>();

            int horaireEvent = 0;

            Scanner scanner = new Scanner(maintenanceLog);

            while (scanner.hasNextLine()) { // Parcours du fichier de maintenance
                String ligne = scanner.nextLine();
                String date = ligne.split(" ")[0]; // Date de la maintenance
                String refMachine = ligne.split(" ")[2]; // Référence de la machine

                if (!jours.containsKey(date)) { // Création d'une liste pour chaque jour
                    jours.put(date, new ArrayList<String>());
                    jours.get(date).add(ligne);
                } else {
                    jours.get(date).add(ligne);
                }

                if (e == 0) { // Ajout de la première machine
                    M.add(refMachine);
                    dernierDepart.put(refMachine, 6 * 60);
                    lastEvent.put(refMachine, "D");
                    tempsFonctionnement.put(refMachine, 0);
                    fiabiliteMulti.put(refMachine, new ArrayList<Float>());
                    e++;
                } else { // Vérification de la présence de la machine dans la liste
                    ajouter = true;
                    for (int i = 0; i < e; i++) {
                        if (M.contains(refMachine)) {
                            ajouter = false;
                            break;
                        }
                    }
                    if (ajouter) { // Ajout de la machine si elle n'est pas déjà dans la liste
                        M.add(refMachine);
                        dernierDepart.put(refMachine, 6 * 60);
                        lastEvent.put(refMachine, "D");
                        tempsFonctionnement.put(refMachine, 0);
                        fiabiliteMulti.put(refMachine, new ArrayList<Float>());
                        e++;
                    }
                }
            }

            scanner.close();

            for (String date : jours.keySet()) { // Parcours des jours
                int dureeFonct = 0;
                for (String ref : M) { // Initialisation des variables pour chaque machine
                    dernierDepart.put(ref, 6 * 60);
                    lastEvent.put(ref, "D");
                    tempsFonctionnement.put(ref, 0);
                }

                System.out.println("Jour : " + date);

                for (String ligne : jours.get(date)) { // Parcours des événements de la journée

                    String time = ligne.split(" ")[1]; // Heure de la maintenance
                    String refMachine = ligne.split(" ")[2]; // Référence de la machine
                    String event = ligne.split(" ")[3]; // Type d'événement
                    // String op = ligne.split(" ")[4]; // Opérateur
                    // String cause = ligne.split(" ")[5]; // Cause de la maintenance

                    int heure = Integer.parseInt(time.split(":")[0]); // Heure de l'événement
                    int minute = Integer.parseInt(time.split(":")[1]); // Minute de l'événement
                    horaireEvent = heure * 60 + minute; // Heure de l'événement en minutes

                    if (event.equals("A")) { // Cas d'un arrêt
                        dureeFonct = horaireEvent - dernierDepart.get(refMachine); // Calcul de la durée de
                                                                                   // fonctionnement
                        tempsFonctionnement.put(refMachine, tempsFonctionnement.get(refMachine) +
                                dureeFonct); // Ajout de la durée de fonctionnement de la machine depuis le dernier
                                             // départ
                        lastEvent.put(refMachine, event); // Mise à jour du dernier événement
                    } else if (event.equals("D")) { // Cas d'un départ
                        dernierDepart.put(refMachine, horaireEvent); // Mise à jour de l'heure de départ
                        lastEvent.put(refMachine, event); // Mise à jour du dernier événement
                    }

                }

                for (String ref : M) { // Calcul final de la durée de fonctionnement de chaque machine
                    if (lastEvent.get(ref).equals("D")) { // Cas où la machine n'a pas été arrêtée (on suppose qu'elle
                                                          // l'est à la fin de la journée (20h))
                        dureeFonct = 20 * 60 - dernierDepart.get(ref);
                        tempsFonctionnement.put(ref, tempsFonctionnement.get(ref) + dureeFonct);
                        System.out
                                .println("Machine " + ref + " : " + tempsFonctionnement.get(ref)
                                        + "/840 minutes de fonctionnement aujourd'hui");
                    } else { // Cas où la machine a été arrêtée
                        System.out
                                .println("Machine " + ref + " : " + tempsFonctionnement.get(ref)
                                        + "/840 minutes de fonctionnement aujourd'hui");
                    }
                }

                // Calcul de la fiabilité des machines
                System.out.println("Fiabilité des machines : ");
                for (String ref : M) { // Calcul de la fiabilité de chaque machine
                    float fiab = ((float) tempsFonctionnement.get(ref) / tempsTotal) * 100; // Fiabilité = temps de
                                                                                            // fonctionnement / temps
                                                                                            // total
                    fiabiliteJour.put(ref, Math.round(fiab * 100.0f) / 100.0f); // Arrondi à 2 décimales
                    fiabiliteMulti.get(ref).add(fiab); // Ajout de la fiabilité de la machine dans la liste des
                                                       // fiabilités
                }

                // Tri des fiabilités
                fiabTri = fiabiliteJour.entrySet().stream()
                        .sorted((HashMap.Entry.<String, Float>comparingByValue().reversed()))
                        .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey,
                                java.util.Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
                fiabTri.forEach((key, value) -> System.out.println(key + ": " + value +
                        "%"));
                System.out.println("");
            }

            // Calcul de la fiabilité totale (moyenne des fiabilités des machines chaque
            // jour)
            for (String ref : M) {
                float fiabTot = 0;
                for (float f : fiabiliteMulti.get(ref)) {
                    fiabTot += f;
                }
                fiabTot = fiabTot / fiabiliteMulti.get(ref).size(); // Moyenne des fiabilités
                fiabiliteTotal.put(ref, Math.round(fiabTot * 100.0f) / 100.0f);
                fiabiliteMoyenne = fiabiliteTotal.entrySet().stream()
                        .sorted((HashMap.Entry.<String, Float>comparingByValue().reversed()))
                        .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey,
                                java.util.Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
            }
            fiabiliteMoyenne.forEach(
                    (key, value) -> System.out.println("Fiabilité moyenne de la machine " + key + ": " + value + "%"));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
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
