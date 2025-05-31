package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repozytorium JPA dla encji {@link Reklamacja}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na reklamacjach w bazie danych.
 */
@Repository
public interface ReklamacjaRepository extends JpaRepository<Reklamacja, Integer> {
}
