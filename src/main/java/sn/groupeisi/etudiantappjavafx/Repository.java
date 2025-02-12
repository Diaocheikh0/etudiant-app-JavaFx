package sn.groupeisi.etudiantappjavafx;

import java.util.List;

public interface Repository<T> {
    public int add(T t);
    public int update(T t);
    public int delete(int id);
    public List<T> list();
    public T get(int id);
}
