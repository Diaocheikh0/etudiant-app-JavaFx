module sn.groupeisi.etudiantappjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires TrayNotification;
    requires java.desktop;


    opens sn.groupeisi.etudiantappjavafx to javafx.fxml;
    exports sn.groupeisi.etudiantappjavafx;
}