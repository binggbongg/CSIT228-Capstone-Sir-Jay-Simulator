module com.example.csit228capstonesirjaysimulator {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.csit228capstonesirjaysimulator to javafx.fxml;
    exports com.example.csit228capstonesirjaysimulator;
}