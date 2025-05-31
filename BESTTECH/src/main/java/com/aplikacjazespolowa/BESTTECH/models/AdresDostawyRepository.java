package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repozytorium JPA dla encji {@link AdresDostawy}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na adresach dostawy w bazie danych.
 */
@Repository
public interface AdresDostawyRepository extends JpaRepository<AdresDostawy, Integer> {
}
