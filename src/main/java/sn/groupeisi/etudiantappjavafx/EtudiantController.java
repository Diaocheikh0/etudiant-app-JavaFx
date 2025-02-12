package sn.groupeisi.etudiantappjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import tools.Notification;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EtudiantController implements Initializable {
    private DBConnexion db = new DBConnexion();

    private int id;

    @FXML
    private TableColumn<Etudiant, String> classeCol;

    @FXML
    private ComboBox<Classe> classeTfd;

    @FXML
    private Button effacerBtn;

    @FXML
    private Button enregistrerBtn;

    @FXML
    private TableView<Etudiant> etudiantsTbl;

    @FXML
    private TableColumn<Etudiant, Integer> idCol;

    @FXML
    private TableColumn<Etudiant, String> matriculeCol;

    @FXML
    private Button modifierBtn;

    @FXML
    private TableColumn<Etudiant, Double> moyenneCol;

    @FXML
    private TextField moyenneTfd;

    @FXML
    private TableColumn<Etudiant, String> nomCol1;

    @FXML
    private TextField nomTfd;

    @FXML
    private TableColumn<Etudiant, String> prenomCol;

    @FXML
    private TextField prenomTfd;

    @FXML
    private Button supprimerBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadClasses();
        loadTable();
        clearFields();
    }

    //Méthode pour charger les classes dans le formulaire d'ajout d'1 étudiant dans le comboBox Classe
    private void loadClasses() {
        String sql = "SELECT id, nom FROM classe";
        ObservableList<Classe> classList = FXCollections.observableArrayList();

        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                Classe classe = new Classe();
                classe.setId(rs.getInt("id"));
                classe.setNom(rs.getString("nom"));
                classList.add(classe);
            }
            classeTfd.setItems(classList);

            // Définition de la façon dont les objets Classe sont affichés dans le ComboBox
            classeTfd.setConverter(new StringConverter<Classe>() {
                @Override
                public String toString(Classe classe) {
                    return classe.getNom();
                }

                @Override
                public Classe fromString(String string) {
                    return classeTfd.getItems().stream()
                            .filter(classe -> classe.getNom().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            classeTfd.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Méthode pour recupérer les étudiants dans la BD
    public ObservableList<Etudiant> getEtudiants(){
        ObservableList<Etudiant> etudiants = FXCollections.observableArrayList();
        String sql = "select * from etudiant order by nom asc";
        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                Etudiant etudiant = new Etudiant();
                etudiant.setId(rs.getInt("id"));
                etudiant.setMatricule(rs.getString("matricule"));
                etudiant.setNom(rs.getString("nom"));
                etudiant.setPrenom(rs.getString("prenom"));
                etudiant.setMoyenne(rs.getDouble("moyenne"));

                // Récupérer l'ID de la classe
                int classeId = rs.getInt("classe_id");

                // Récupérer le nom de la classe via une nouvelle requête
                String sqlClasse = "SELECT nom FROM classe WHERE id = ?";
                db.initPrepar(sqlClasse);
                db.getPstm().setInt(1, classeId);
                ResultSet rsClasse = db.executeSelect();

                if (rsClasse.next()) {
                    String nomClasse = rsClasse.getString("nom");
                    etudiant.setClasse(new Classe(nomClasse));
                }
                etudiants.add(etudiant);
            }
            db.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return etudiants;
    }

    //Méthode pour charger les étudiant
    public void loadTable(){
        ObservableList<Etudiant> liste = getEtudiants();
        etudiantsTbl.setItems(liste);
        idCol.setCellValueFactory(new PropertyValueFactory<Etudiant, Integer>("id"));
        matriculeCol.setCellValueFactory(new PropertyValueFactory<Etudiant, String>("matricule"));
        nomCol1.setCellValueFactory(new PropertyValueFactory<Etudiant, String>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<Etudiant, String>("prenom"));
        moyenneCol.setCellValueFactory(new PropertyValueFactory<Etudiant, Double>("moyenne"));
        classeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClasse().getNom()));

    }

    //Méthode pour ajouter un étudiant
    @FXML
    void save(ActionEvent event) {
        String sql = "INSERT INTO etudiant(id, matricule, nom, prenom, moyenne, classe_id) VALUES(DEFAULT,?,?,?,?,?)";

        try {
            // Récupérer la classe sélectionnée
            Classe selectedClasse = classeTfd.getSelectionModel().getSelectedItem();
            // Nom de la classe ou valeur par défaut
            String nomClasse = selectedClasse != null ? selectedClasse.getNom() : "INCONNU";

            // Créer un objet Etudiant et générer le matricule
            Etudiant etudiant = new Etudiant();
            String matricule = etudiant.generateMatricule(nomClasse);

            db.initPrepar(sql);
            db.getPstm().setString(1, matricule);
            db.getPstm().setString(2, nomTfd.getText());
            db.getPstm().setString(3, prenomTfd.getText());
            db.getPstm().setDouble(4, Double.parseDouble(moyenneTfd.getText()));

            // Récupére l'ID de la classe sélectionnée
            int classeId = selectedClasse != null ? selectedClasse.getId() : 0;
            db.getPstm().setInt(5, classeId);

            //Control la moyenne saisie
            double moyenne = Double.parseDouble(moyenneTfd.getText());
            if (moyenne > 20){
                Notification.NotifError("Erreur", "Moyenne ne doit pas être supérieur à 20");
                return;
            }

            db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succés", "Etudiant enregistré avec succés !");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Méthode pour vider le formulaire
    public void clearFields(){
        prenomTfd.setText("");
        nomTfd.setText("");
        moyenneTfd.setText("");
        classeTfd.getSelectionModel().clearSelection();
    }

    @FXML
    void vider(ActionEvent event) {
        clearFields();
    }

    //Méthode pour modifier un étudiant
    @FXML
    void update(ActionEvent event) {
        String sql = "UPDATE etudiant SET nom = ?, prenom = ?, moyenne = ?, classe_id = ? WHERE id = ?";

        try {
            // Récupérer la classe sélectionnée
            Classe selectedClasse = classeTfd.getSelectionModel().getSelectedItem();
            String nomClasse = selectedClasse != null ? selectedClasse.getNom() : "INCONNU";

            db.initPrepar(sql);
            db.getPstm().setString(1, nomTfd.getText());
            db.getPstm().setString(2, prenomTfd.getText());
            db.getPstm().setDouble(3, Double.parseDouble(moyenneTfd.getText()));

            // Récupérer l'ID de la classe sélectionnée
            int classeId = selectedClasse != null ? selectedClasse.getId() : 0;
            db.getPstm().setInt(4, classeId);
            db.getPstm().setInt(5, id);

            //Control la moyenne saisie
            double moyenne = Double.parseDouble(moyenneTfd.getText());
            if (moyenne > 20){
                Notification.NotifError("Erreur", "Moyenne ne doit pas être supérieur à 20");
                return;
            }

            db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succés", "Etudiant modifié avec succés !");
            enregistrerBtn.setDisable(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    //Méthode pour supprimer un étudiant
    @FXML
    void delete(ActionEvent event) {
        String sql = "DELETE FROM etudiant WHERE id = ?";
        try{
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            db.executeMaj();
            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succés", "Etudiant supprimé avec succés !");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Méthode pour recupérer les infos d'un étudiant pour modifier ou supprimé
    @FXML
    void getData(MouseEvent event) {
        Etudiant etudiant = etudiantsTbl.getSelectionModel().getSelectedItem();
        id = etudiant.getId();
        prenomTfd.setText(etudiant.getPrenom());
        nomTfd.setText(etudiant.getNom());
        moyenneTfd.setText(etudiant.getMoyenne().toString());
        classeTfd.getSelectionModel().select(etudiant.getClasse());
        enregistrerBtn.setDisable(true);
    }

}
