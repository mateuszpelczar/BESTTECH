package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
/**
 * Repozytorium JPA dla encji {@link Zamowienie}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD oraz zapytań dotyczących zamówień w bazie danych.
 */
@Repository
public interface ZamowienieRepository extends JpaRepository<Zamowienie, Integer> {
    /**
     * Zwraca listę zamówień złożonych przez użytkownika o podanym ID.
     *
     * @param userID ID użytkownika (klienta)
     * @return lista zamówień
     */
    List<Zamowienie>findByKlient_id(Integer userID);
    /**
     * Zwraca listę zamówień złożonych przez użytkownika o podanym ID, posortowaną według podanego sortowania.
     *
     * @param userID ID użytkownika (klienta)
     * @param sort   obiekt sortowania
     * @return lista zamówień
     */
    List<Zamowienie> findByKlient_id(Integer userID,Sort sort);
    /**
     * Zwraca listę zamówień złożonych w podanym zakresie dat.
     *
     * @param od      data początkowa (włącznie)
     * @param doDaty  data końcowa (włącznie)
     * @return lista zamówień złożonych w tym przedziale czasu
     */
    List<Zamowienie> findByDataZamowieniaBetween(LocalDate od, LocalDate doDaty);
}