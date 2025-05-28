package com.aplikacjazespolowa.BESTTECH.models;

import lombok.extern.java.Log;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LogsRepository extends JpaRepository<LogsSystem, Long> {
    List<LogsSystem> findByLevel(String level, Sort sort);
    List<LogsSystem> findByUsernameAndLevel(String username, String level, Sort sort);
    List<LogsSystem> findByUsername(String username, Sort sort);


}
