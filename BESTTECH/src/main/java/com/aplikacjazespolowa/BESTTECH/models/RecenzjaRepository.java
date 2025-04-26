package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecenzjaRepository extends JpaRepository<Recenzja, Integer> {
    List<Recenzja> findByKlient(DBUser klient);
}
