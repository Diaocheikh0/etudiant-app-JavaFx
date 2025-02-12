package sn.groupeisi.etudiantappjavafx;

import java.sql.*;

public class DBConnexion {

    private static final String HOST = "localhost";
    private static final String USER = "postgres";
    private static final String PASSWORD = "PASSWORD";
    private static final String DATABASE = "app_etudiant_db";
    private static final int PORT = 5433;
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    private Connection cnx;
    private PreparedStatement pstm;
    private ResultSet rs;
    private int ok;

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            //System.out.println("Connexion Réussie !");
        } catch (Exception ex) {
            System.out.println("Erreur de connexion à la DB: " + ex.getMessage());
        }
    }

    public void initPrepar(String sql) {
        try {
            connect();
            pstm = cnx.prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeSelect() {
        rs = null;
        try {
            rs = pstm.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public int executeMaj() {
        try {
            ok = pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public void closeConnection() {
        try {
            if (cnx != null) {
                cnx.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement getPstm() {
        return pstm;
    }

}
