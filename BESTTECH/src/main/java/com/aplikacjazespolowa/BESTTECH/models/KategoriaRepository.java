package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KategoriaRepository extends JpaRepository<Kategoria, Integer> {
    List<Kategoria> findByNazwaContainingIgnoreCase(String nazwa);

    Optional<Kategoria> findByNazwa(String nazwa);
    Optional<Kategoria> deleteByNazwa(String nazwa);
}
