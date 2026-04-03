module com.example.junglerush {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.junglerush to javafx.fxml;
    exports com.example.junglerush;
}