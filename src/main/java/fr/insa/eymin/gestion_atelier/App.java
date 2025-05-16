package fr.insa.eymin.gestion_atelier;

import atlantafx.base.theme.*;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    // Méthode appelée au démarrage de l'application
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        PrincipalVue.mainWindow();
    }

    // Méthode principale
    public static void main(String[] args) {
        // Lance l'application
        launch();
    }

}
