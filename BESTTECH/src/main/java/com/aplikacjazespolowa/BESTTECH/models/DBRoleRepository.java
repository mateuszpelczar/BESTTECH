package com.aplikacjazespolowa.BESTTECH.models;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repozytorium JPA dla encji {@link DBRole}.
 * <p>
 * Umożliwia wykonywanie operacji CRUD oraz wyszukiwanie ról po nazwie.
 */
@Repository
public interface DBRoleRepository extends JpaRepository<DBRole, Integer> {

    /**
     * Wyszukuje rolę na podstawie jej nazwy.
     *
     * @param name nazwa roli (np. "ADMIN", "CLIENT")
     * @return obiekt Optional zawierający znalezioną rolę lub pusty, jeśli nie znaleziono
     */
    Optional<DBRole> findByName(String name);


}
