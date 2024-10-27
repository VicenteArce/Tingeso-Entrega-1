package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanEvaluationRepository extends JpaRepository<LoanEvaluationEntity, Long> {
}
