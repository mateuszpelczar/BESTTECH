package com.aplikacjazespolowa.BESTTECH.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
    Optional<DBUser> findByEmail(String email);
    List<DBUser> findByRoles_Name(String roleName);

    boolean existsByEmail(String email);
}