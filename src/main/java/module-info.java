module com.projects.carturestiwishlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires htmlunit;


    opens com.projects.carturestiwishlist to javafx.fxml;
    exports com.projects.carturestiwishlist;
}