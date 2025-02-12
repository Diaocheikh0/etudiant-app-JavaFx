package sn.groupeisi.etudiantappjavafx;

import java.util.List;

public interface IEtudiant extends Repository<Etudiant>{

   int add(sn.groupeisi.etudiantappjavafx.Etudiant etudiant);

   int update(sn.groupeisi.etudiantappjavafx.Etudiant etudiant);

   int delete(int id);

   List<sn.groupeisi.etudiantappjavafx.Etudiant> list();

   sn.groupeisi.etudiantappjavafx.Etudiant get(int id);

   public List<Etudiant> getEtudiantsByClasse(String classe);
}
