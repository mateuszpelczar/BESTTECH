package com.aplikacjazespolowa.BESTTECH.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DBUserRepository extends JpaRepository<DBUser, Integer> {
    Optional<DBUser> findByEmail(String email);
}