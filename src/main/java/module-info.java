module com.example.csit228capstonesirjaysimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires com.almasb.fxgl.all;
    requires com.almasb.fxgl.entity;
    requires com.almasb.fxgl.core;
    requires java.sql;
    requires annotations;

    opens assets.textures;
    opens assets.sounds;
    opens assets.music;
    opens assets.textures.students;
    // Reflection access for FXGL
    // This allows FXGL to "see" your Factory and Components
    opens com.example.csit228capstonesirjaysimulator.entity to com.almasb.fxgl.core;
    opens com.example.csit228capstonesirjaysimulator.component.student to com.almasb.fxgl.core;

    // Boilerplate for JavaFX startup
    opens com.example.csit228capstonesirjaysimulator to javafx.fxml;
    exports com.example.csit228capstonesirjaysimulator;

    // Exports/Opens for other application sub-packages
    exports com.example.csit228capstonesirjaysimulator.application;
    opens com.example.csit228capstonesirjaysimulator.application to javafx.fxml;
    exports com.example.csit228capstonesirjaysimulator.scene;
    opens com.example.csit228capstonesirjaysimulator.scene to javafx.fxml;
}