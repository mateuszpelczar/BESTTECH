package com.aplikacjazespolowa.BESTTECH.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogsRepository extends JpaRepository<LogsSystem, Long> {
    List<LogsSystem> findByLevel(String level);
    List<LogsSystem> findByUsernameAndLevel(String username, String level);
    List<LogsSystem> findByUsername(String username);


}
