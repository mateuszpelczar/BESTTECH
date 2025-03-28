package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProduktRepository extends JpaRepository<Produkt, Integer> {
    List<Produkt> findByNazwaContainingIgnoreCase(String nazwa);
    List<Produkt> findByKategoria_KategoriaID(Integer kategoriaID);
}

