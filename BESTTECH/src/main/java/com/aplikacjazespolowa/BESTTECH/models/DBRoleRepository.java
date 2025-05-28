package com.aplikacjazespolowa.BESTTECH.models;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DBRoleRepository extends JpaRepository<DBRole, Integer> {
    Optional<DBRole> findByName(String name);


}
