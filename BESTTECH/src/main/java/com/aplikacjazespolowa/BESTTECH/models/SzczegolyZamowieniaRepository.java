package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repozytorium JPA dla encji {@link SzczegolyZamowienia}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na szczegółach zamówienia w bazie danych.
 */
@Repository
public interface SzczegolyZamowieniaRepository extends JpaRepository<SzczegolyZamowienia, Integer> {
}
