package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ZamowienieRepository extends JpaRepository<Zamowienie, Integer> {

    List<Zamowienie>findByKlient_id(Integer userID);

    List<Zamowienie> findByKlient_id(Integer userID,Sort sort);

    List<Zamowienie> findByDataZamowieniaBetween(LocalDate od, LocalDate doDaty);
}