package ru.murad.NauJava.repository;

public interface LegacyCrudRepository<T, ID> {
    void create(T entity);
    T read(ID id);
    void update(T entity);
    void delete(ID id);
}
