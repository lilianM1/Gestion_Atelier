module fr.insa.eymin.gestion_atelier {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires atlantafx.base;
    requires org.fxyz3d.core;

    opens fr.insa.eymin.gestion_atelier to javafx.fxml;

    exports fr.insa.eymin.gestion_atelier;
}
