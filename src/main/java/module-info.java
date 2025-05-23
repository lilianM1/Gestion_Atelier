module fr.insa.eymin.gestion_atelier {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires org.fxyz3d.core;
    requires atlantafx.base;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires java.desktop;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.feather;

    exports fr.insa.eymin.gestion_atelier;

    opens fr.insa.eymin.gestion_atelier to javafx.fxml;
}