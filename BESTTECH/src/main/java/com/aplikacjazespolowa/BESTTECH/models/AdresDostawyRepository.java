package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdresDostawyRepository extends JpaRepository<AdresDostawy, Integer> {
}
