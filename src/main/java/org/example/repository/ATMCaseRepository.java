package org.example.repository;

import org.example.model.ATMCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ATMCaseRepository extends JpaRepository<ATMCase, Integer> {
}
