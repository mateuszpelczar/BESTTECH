package com.aplikacjazespolowa.BESTTECH.models;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repozytorium JPA dla encji {@link Kategoria}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD oraz wyszukiwanie i usuwanie kategorii po nazwie.
 */
@Repository
public interface KategoriaRepository extends JpaRepository<Kategoria, Integer> {
    /**
     * Wyszukuje listę kategorii, których nazwa zawiera podany fragment (ignorując wielkość liter).
     *
     * @param nazwa fragment nazwy kategorii
     * @return lista pasujących kategorii
     */
    List<Kategoria> findByNazwaContainingIgnoreCase(String nazwa);
    /**
     * Wyszukuje kategorię na podstawie dokładnej nazwy.
     *
     * @param nazwa nazwa kategorii
     * @return obiekt Optional zawierający znalezioną kategorię lub pusty, jeśli nie znaleziono
     */
    Optional<Kategoria> findByNazwa(String nazwa);
    /**
     * Usuwa kategorię na podstawie dokładnej nazwy.
     *
     * @param nazwa nazwa kategorii do usunięcia
     * @return obiekt Optional zawierający usuniętą kategorię lub pusty, jeśli nie znaleziono
     */
    Optional<Kategoria> deleteByNazwa(String nazwa);
}
