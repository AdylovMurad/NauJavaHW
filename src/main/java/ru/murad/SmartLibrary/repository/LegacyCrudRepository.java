package ru.murad.SmartLibrary.repository;

public interface LegacyCrudRepository<T, ID> {
    void create(T entity);
    T read(ID id);
    void update(T entity);
    void delete(ID id);
}
