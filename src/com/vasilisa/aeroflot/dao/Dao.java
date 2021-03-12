package com.vasilisa.aeroflot.dao;

import com.vasilisa.aeroflot.entity.Aircraft;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    boolean delete(K id);

    E save(E e);

    void update(E e);

    Optional<E> findById(K id);

    List<E> findAll();
}
