package sn.groupeisi.etudiantappjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import tools.Notification;
import tools.Outils;

public class LoginController {
    private IUser userDao = new UserImpl();

    @FXML
    private TextField emailTfd;

    @FXML
    private PasswordField passwordTfd;

    @FXML
    private Button saveBtn;

    @FXML
    void login(ActionEvent event) {
        String email = emailTfd.getText();
        String password = passwordTfd.getText();
        if (email.isEmpty() || password.isEmpty()) {
            Notification.NotifError("Erreur", "Tous les champs sont obligatoires !");
        } else {
            User u = userDao.seConnecter(email, password);
            if (u != null) {
                try {
                    Notification.NotifSuccess("Succès", "Connexion Réussie !");
                    Outils.load(event, "Gestion des Etudiants", "/FXML/etudiants.fxml");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Notification.NotifError("Erreur", "Email/Password incorrect !");
            }
        }

    }
}
