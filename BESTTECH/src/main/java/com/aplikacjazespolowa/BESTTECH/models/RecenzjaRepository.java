package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repozytorium JPA dla encji {@link Recenzja}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD oraz wyszukiwanie recenzji na podstawie klienta z możliwością sortowania.
 */
@Repository
public interface RecenzjaRepository extends JpaRepository<Recenzja, Integer> {
    /**
     * Zwraca listę recenzji wystawionych przez danego klienta, posortowaną zgodnie z przekazanym obiektem sortowania.
     *
     * @param klient obiekt użytkownika (klienta)
     * @param sort   obiekt sortowania
     * @return lista recenzji klienta
     */
    List<Recenzja> findByKlient(DBUser klient, Sort sort);
}
