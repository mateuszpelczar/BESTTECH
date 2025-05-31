package com.aplikacjazespolowa.BESTTECH.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repozytorium JPA dla encji {@link DBUser}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD na użytkownikach oraz wyszukiwanie i sprawdzanie istnienia użytkowników po e-mailu i roli.
 */
@Repository
public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
    /**
     * Wyszukuje użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return obiekt Optional zawierający znalezionego użytkownika lub pusty, jeśli nie znaleziono
     */
    Optional<DBUser> findByEmail(String email);
    /**
     * Wyszukuje listę użytkowników na podstawie nazwy przypisanej roli.
     *
     * @param roleName nazwa roli (np. "CLIENT", "ADMIN")
     * @return lista użytkowników posiadających daną rolę
     */
    List<DBUser> findByRoles_Name(String roleName);
    /**
     * Sprawdza, czy istnieje użytkownik o podanym adresie e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return true jeśli użytkownik istnieje, false w przeciwnym wypadku
     */
    boolean existsByEmail(String email);
}