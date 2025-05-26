package fr.insa.eymin.gestion_atelier.controleurs;

import fr.insa.eymin.gestion_atelier.modeles.Atelier;
import fr.insa.eymin.gestion_atelier.vues.AtelierVue;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.kordamp.ikonli.feather.Feather;

import javafx.concurrent.Task;
import javafx.stage.Stage;

/**
 * Contrôleur pour gérer les interactions entre le modèle Atelier et sa vue.
 * Coordonne les opérations de calcul de fiabilité et la gestion des erreurs.
 */
public class AtelierControleur {

    private Atelier modele;
    private AtelierVue vue;
    private PrincipalVue principalVue; // Référence à la vue principale, si nécessaire

    /**
     * Constructeur du contrôleur d'atelier
     */
    public AtelierControleur(Atelier modele, AtelierVue vue, PrincipalVue principalVue) {
        this.principalVue = principalVue; // Initialisation de la vue principale
        this.modele = modele;
        this.vue = vue;
    }

    /**
     * Déclenche le calcul de fiabilité des machines et gère l'affichage des
     * résultats
     */
    public void calculerEtAfficherFiabilite() {
        // Affiche la fenêtre de progression
        Stage loadingStage = vue.afficherFenetreCalculEnCours();

        // Définition de la tâche asynchrone de calcul
        Task<Void> calculTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Chemin du fichier de sortie
                    String excelFilePath = "src\\main\\ressources\\data\\FiabiliteMachines.csv";

                    // Appel des méthodes du modèle pour effectuer les calculs
                    Map<String, Object> resultats = modele.calculerFiabilite();
                    modele.genererRapportExcel(resultats, excelFilePath);

                    // Ouverture du fichier avec l'application par défaut
                    vue.ouvrirFichier(excelFilePath);

                } catch (FileNotFoundException ex) {
                    // Gestion de l'erreur si le fichier de suivi n'est pas trouvé
                    ex.printStackTrace();
                    vue.afficherErreur(
                            "Erreur",
                            "Fichier non trouvé",
                            "Le fichier SuiviMaintenance.txt n'a pas été trouvé. \nVeuillez le placer dans le dossier src/main/ressources/data/.");
                    vue.fermerFenetre(loadingStage);
                } catch (IOException e) {
                    // Gestion des autres erreurs d'entrée/sortie
                    e.printStackTrace();
                    vue.afficherErreur(
                            "Erreur",
                            "Erreur d'E/S",
                            "Une erreur est survenue lors de la lecture ou l'écriture des fichiers : "
                                    + e.getMessage());
                    vue.fermerFenetre(loadingStage);
                } catch (Exception e) {
                    // Gestion des erreurs inattendues
                    e.printStackTrace();
                    vue.afficherErreur(
                            "Erreur",
                            "Erreur inattendue",
                            "Une erreur inattendue est survenue : " + e.getMessage());
                    vue.fermerFenetre(loadingStage);
                }
                return null;
            }
        };

        // Liaison de la barre de progression à la tâche
        vue.lierProgressionTache(loadingStage, calculTask);

        // Démarrage de la tâche dans un thread séparé
        new Thread(calculTask).start();
    }

    /**
     * Crée un nouvel atelier en réinitialisant le modèle et la vue
     */
    public void creerNouveauAtelier() {
        // Réinitialisation du modèle et de la vue
        modele = new Atelier();
        vue = new AtelierVue(this, principalVue);
        // Affichage de la vue
        vue.afficherFenetreCreation();
    }

    public void creationAtelier(String nomAtelier, float longX, float longY) {

        Atelier atelier = new Atelier(nomAtelier, longX, longY);
        // Enregistrement de l'atelier

        atelier.sauvegarderAtelier(nomAtelier, longX, longY);

        principalVue.afficherNotif("Atelier créé avec succès", Feather.CHECK_SQUARE,
                principalVue.getRootContainer());
        principalVue.setAtelier(atelier);
        // Ferme la fenêtre principale avant de créer un nouvel atelier
        if (principalVue != null) {
            principalVue.fermerFenetre();
        }
    }
}
