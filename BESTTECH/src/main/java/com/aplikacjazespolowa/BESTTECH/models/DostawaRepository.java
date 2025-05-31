package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repozytorium JPA dla encji {@link Dostawa}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na dostawach w bazie danych.
 */
@Repository
public interface DostawaRepository extends JpaRepository<Dostawa, Integer> {
}
