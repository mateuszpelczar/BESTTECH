package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interfejs repozytorium JPA dla encji {@link Produkt}.
 *
 * Umożliwia wykonywanie zapytań do bazy danych dotyczących produktów.
 * Dziedziczy podstawowe operacje CRUD z {@link JpaRepository}.
 */
@Repository
public interface ProduktRepository extends JpaRepository<Produkt, Integer> {
    /**
     * Wyszukuje produkty, których nazwa zawiera podany ciąg znaków, ignorując wielkość liter.
     *
     * @param nazwa część nazwy produktu
     * @return lista pasujących produktów
     */
    List<Produkt> findByNazwaContainingIgnoreCase(String nazwa);

    /**
     * Zwraca produkty przypisane do danej kategorii na podstawie jej ID.
     *
     * @param kategoriaID identyfikator kategorii
     * @return lista produktów należących do tej kategorii
     */
    List<Produkt> findByKategoria_KategoriaID(Integer kategoriaID);

    /**
     * Zwraca produkty przypisane do danej kategorii na podstawie jej nazwy.
     *
     * @param nazwa nazwa kategorii
     * @return lista produktów należących do tej kategorii
     */
    List<Produkt> findByKategoria_Nazwa(String nazwa);
}

