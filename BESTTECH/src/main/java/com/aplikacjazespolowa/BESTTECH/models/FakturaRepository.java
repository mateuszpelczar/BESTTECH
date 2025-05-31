package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repozytorium JPA dla encji {@link Faktura}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na fakturach w bazie danych.
 */
@Repository
public interface FakturaRepository extends JpaRepository<Faktura, Integer> {
}
