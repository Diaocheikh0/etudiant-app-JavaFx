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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String sqlInsert = "INSERT INTO etudiant(id, matricule, nom, prenom, moyenne, classe_id) VALUES(DEFAULT,?,?,?,?,?)";
        String sqlUpdate = "UPDATE classe SET effectif = effectif + 1 WHERE id = ?";

        try {
            // Récupérer la classe sélectionnée
            Classe selectedClasse = classeTfd.getSelectionModel().getSelectedItem();
            if (selectedClasse == null) {
                Notification.NotifError("Erreur", "Veuillez sélectionner une classe !");
                return;
            }

            // Vérifier et récupérer la moyenne saisie
            double moyenne = Double.parseDouble(moyenneTfd.getText());
            if (moyenne > 20) {
                Notification.NotifError("Erreur", "Moyenne ne doit pas être supérieure à 20 !");
                return;
            }

            // Générer le matricule
            Etudiant etudiant = new Etudiant();
            String matricule = etudiant.generateMatricule(selectedClasse.getNom());

            // Exécuter l'insertion
            db.initPrepar(sqlInsert);
            db.getPstm().setString(1, matricule);
            db.getPstm().setString(2, nomTfd.getText());
            db.getPstm().setString(3, prenomTfd.getText());
            db.getPstm().setDouble(4, moyenne);
            db.getPstm().setInt(5, selectedClasse.getId());

            int ok = db.executeMaj();

            // Si insertion réussie, mettre à jour l'effectif de la classe
            if (ok == 1) {
                db.initPrepar(sqlUpdate);
                db.getPstm().setInt(1, selectedClasse.getId());
                db.executeMaj();
            }

            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès", "Étudiant enregistré avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            Notification.NotifError("Erreur", "Une erreur s'est produite lors de l'enregistrement !");
        } catch (NumberFormatException e) {
            Notification.NotifError("Erreur", "Veuillez entrer une moyenne valide !");
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
        String selectClasseSql = "SELECT nom FROM classe WHERE id = ?";
        String selectSql = "SELECT classe_id, matricule FROM etudiant WHERE id = ?";
        String updateSql = "UPDATE etudiant SET matricule = ?, nom = ?, prenom = ?, moyenne = ?, classe_id = ? WHERE id = ?";

        String existingMatricule = null;
        int existingClasseId = -1;

        try {
            // Récupération de la classe sélectionnée
            Classe selectedClasse = classeTfd.getSelectionModel().getSelectedItem();
            if (selectedClasse == null) {
                Notification.NotifError("Erreur", "Veuillez sélectionner une classe !");
                return;
            }
            int newClasseId = selectedClasse.getId();
            String nomClasse = selectedClasse.getNom();

            // Étape 1 : Récupérer la classe et le matricule actuels
            db.initPrepar(selectSql);
            db.getPstm().setInt(1, id);
            ResultSet rs = db.executeSelect();

            if (rs.next()) {
                existingClasseId = rs.getInt("classe_id");
                existingMatricule = rs.getString("matricule");
            }
            rs.close();
            db.closeConnection();

            // Étape 2 : Si la classe a changé, mettre à jour le matricule
            String newMatricule = existingMatricule;
            if (existingMatricule != null && existingClasseId != newClasseId) {
                db.initPrepar(selectClasseSql);
                db.getPstm().setInt(1, newClasseId);
                ResultSet rsClasse = db.executeSelect();

                if (rsClasse.next()) {
                    nomClasse = rsClasse.getString("nom");
                    String regex = "ET@(\\d{14})(.*?)#";
                    Matcher matcher = Pattern.compile(regex).matcher(existingMatricule);
                    if (matcher.find()) {
                        String datePart = matcher.group(1);
                        newMatricule = "ET@" + datePart + nomClasse + "#";
                    }
                }
                rsClasse.close();
                db.closeConnection();
            }

            // Vérification de la moyenne
            double moyenne = Double.parseDouble(moyenneTfd.getText());
            if (moyenne > 20) {
                Notification.NotifError("Erreur", "La moyenne ne doit pas dépasser 20 !");
                return;
            }

            // Étape 3 : Exécuter la mise à jour
            db.initPrepar(updateSql);
            db.getPstm().setString(1, newMatricule);
            db.getPstm().setString(2, nomTfd.getText());
            db.getPstm().setString(3, prenomTfd.getText());
            db.getPstm().setDouble(4, moyenne);
            db.getPstm().setInt(5, newClasseId);
            db.getPstm().setInt(6, id);
            db.executeMaj();

            if (existingClasseId != newClasseId) {
                String decreaseOldClassSql = "UPDATE classe SET effectif = effectif - 1 WHERE id = ?";
                String increaseNewClassSql = "UPDATE classe SET effectif = effectif + 1 WHERE id = ?";

                try {
                    // Diminuer l'effectif de l'ancienne classe
                    db.initPrepar(decreaseOldClassSql);
                    db.getPstm().setInt(1, existingClasseId);
                    db.executeMaj();
                    db.closeConnection();

                    // Augmenter l'effectif de la nouvelle classe
                    db.initPrepar(increaseNewClassSql);
                    db.getPstm().setInt(1, newClasseId);
                    db.executeMaj();
                    db.closeConnection();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Notification.NotifError("Erreur SQL", "Mise à jour des effectifs des classes échouée !");
                }
            }


            db.closeConnection();

            // Rafraîchir l'affichage
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès", "Étudiant modifié avec succès !");
            enregistrerBtn.setDisable(false);

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.NotifError("Erreur SQL", "Une erreur est survenue lors de la mise à jour.");
        } catch (NumberFormatException e) {
            Notification.NotifError("Erreur de saisie", "Veuillez entrer une valeur numérique valide pour la moyenne !");
        }
    }


    //Méthode pour supprimer un étudiant
    @FXML
    void delete(ActionEvent event) {
        String sqlSelect = "SELECT classe_id FROM etudiant WHERE id = ?";
        String sqlDelete = "DELETE FROM etudiant WHERE id = ?";
        String sqlUpdate = "UPDATE classe SET effectif = effectif - 1 WHERE id = ?";

        try {
            // Récupérer l'ID de la classe de l'étudiant
            db.initPrepar(sqlSelect);
            db.getPstm().setInt(1, id);
            ResultSet rs = db.executeSelect();

            int idClasse = -1;
            if (rs.next()) {
                idClasse = rs.getInt("classe_id");
            }
            rs.close();

            // Supprimer l'étudiant
            db.initPrepar(sqlDelete);
            db.getPstm().setInt(1, id);
            int ok = db.executeMaj();

            // Si suppression réussie et que l'étudiant avait une classe, on met à jour l'effectif
            if (ok == 1 && idClasse != -1) {
                db.initPrepar(sqlUpdate);
                db.getPstm().setInt(1, idClasse);
                db.executeMaj();
            }

            db.closeConnection();
            loadTable();
            clearFields();
            Notification.NotifSuccess("Succès", "Étudiant supprimé avec succès !");
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
