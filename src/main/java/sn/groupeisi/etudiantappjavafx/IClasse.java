package sn.groupeisi.etudiantappjavafx;

import java.util.List;

public interface IClasse extends Repository<Classe>{

    int add(sn.groupeisi.etudiantappjavafx.Classe classe);

    int update(sn.groupeisi.etudiantappjavafx.Classe classe);

    int delete(int id);

    List<sn.groupeisi.etudiantappjavafx.Classe> list();

    sn.groupeisi.etudiantappjavafx.Classe get(int id);
}
