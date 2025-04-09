package com.aplikacjazespolowa.BESTTECH.models;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DBRoleRepository extends JpaRepository<DBRole, Integer> {
    Optional<DBRole> findByName(String name);

}
