package sn.groupeisi.etudiantappjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import tools.Notification;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClasseController implements Initializable {
    private DBConnexion db = new DBConnexion();
    private int id;
    private int effectif;

    @FXML
    private TableView<Classe> classeTbl;

    @FXML
    private Button effacerBtn;

    @FXML
    private TableColumn<Classe, String> effectifCol;

    @FXML
    private Button enregistrerBtn;

    @FXML
    private TableColumn<Classe, Integer> idCol;

    @FXML
    private Button modifierBtn;

    @FXML
    private TableColumn<Classe, String> nomCol1;

    @FXML
    private TextField nomTfd;

    @FXML
    private Button supprimerBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
    }

    public ObservableList<Classe> getClasses() {
        ObservableList<Classe> classes = FXCollections.observableArrayList();
        String sql = "SELECT * FROM classe ORDER BY nom ASC";
        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while (rs.next()){
                Classe classe = new Classe();
                classe.setId(rs.getInt("id"));
                classe.setNom(rs.getString("nom"));
                classe.setEffectif(rs.getInt("effectif"));
                classes.add(classe);
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public void loadTable() {
        ObservableList<Classe> liste = getClasses();
        classeTbl.setItems(liste);
        idCol.setCellValueFactory(new PropertyValueFactory<Classe, Integer>("id"));
        nomCol1.setCellValueFactory(new PropertyValueFactory<Classe, String>("nom"));
        effectifCol.setCellValueFactory(new PropertyValueFactory<Classe, String>("effectif"));

    }

    //Méthode pour ajouter une classe
    @FXML
    void save(ActionEvent event) {
        String sql = "INSERT INTO classe(id, nom) VALUES(DEFAULT,?)";
        try{
            db.initPrepar(sql);
            db.getPstm().setString(1, nomTfd.getText());
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès", "Classe enregistré avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            Notification.NotifError("Erreur", "Une erreur s'est produite lors de l'enregistrement !");
        }
    }

    //Méthode pour vider le formulaire
    public void clearFields(){
        nomTfd.setText("");
    }

    @FXML
    void vider(ActionEvent event) {
        clearFields();
    }

    //Méthode pour modifier une classe
    @FXML
    void update(ActionEvent event) {
        String sql = "UPDATE classe SET nom = ?, effectif = ? WHERE id = ?";
        try{
            db.initPrepar(sql);
            db.getPstm().setString(1, nomTfd.getText());
            db.getPstm().setInt(2, effectif);
            db.getPstm().setInt(3, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès", "Classe modifié avec succès !");
            enregistrerBtn.setDisable(false);

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.NotifError("Erreur SQL", "Une erreur est survenue lors de la mise à jour.");
        }
    }

    //Méthode pour supprimer une classe
    @FXML
    void delete(ActionEvent event) {
        String sql = "DELETE FROM classe WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            int ok = db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès", "Classe supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour recupérer les infos d'une classe pour modifier ou supprimé
    @FXML
    void getData(MouseEvent event) {
        Classe classe = classeTbl.getSelectionModel().getSelectedItem();
        id = classe.getId();
        nomTfd.setText(classe.getNom());
        effectif = classe.getEffectif();
        enregistrerBtn.setDisable(true);
    }
}
