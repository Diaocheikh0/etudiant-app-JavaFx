package sn.groupeisi.etudiantappjavafx;

import java.sql.ResultSet;

public class UserImpl implements IUser {
    private DBConnexion db = new DBConnexion();
    @Override
    public User seConnecter(String email, String password) {
        String sql = "select * from users where email = ? and password = ?";
        User user = null;

        try {
            db.initPrepar(sql);

            db.getPstm().setString(1, email);
            db.getPstm().setString(2, password);

            ResultSet rs = db.executeSelect();

            if (rs.next()) {
                user = new User();
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
            }

            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
