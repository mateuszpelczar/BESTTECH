package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repozytorium JPA dla encji {@link Zwrot}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na zwrotach w bazie danych.
 */
@Repository
public interface ZwrotRepository extends JpaRepository<Zwrot, Integer> {
}
