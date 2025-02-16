package sn.groupeisi.etudiantappjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import tools.Outils;

import java.io.IOException;

public class indexController {

    @FXML
    private Button classeBtn;

    @FXML
    private Button etudiantBtn;

    @FXML
    void pageClasse(ActionEvent event) throws IOException {
        Outils.load(event, "Gestion des Classes", "/FXML/classe.fxml");
    }

    @FXML
    void pageEtudiant(ActionEvent event) throws IOException {
        Outils.load(event, "Gestion des Etudiants", "/FXML/etudiants.fxml");
    }

}
