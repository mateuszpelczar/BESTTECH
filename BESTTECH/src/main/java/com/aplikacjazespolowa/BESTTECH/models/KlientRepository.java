package com.aplikacjazespolowa.BESTTECH.models;
import com.aplikacjazespolowa.BESTTECH.models.Klient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlientRepository extends JpaRepository<Klient, Integer> {
}