module com.example.batallanaval {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.batallanaval.Controllers to javafx.fxml;
    opens com.example.batallanaval to javafx.fxml;

    exports com.example.batallanaval;
    exports com.example.batallanaval.Controllers;
    exports com.example.batallanaval.Views;

}