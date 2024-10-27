package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.LoanRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequestEntity, Long> {
    LoanRequestEntity findByLoanRequestId(Long loanRequestId);
    List<LoanRequestEntity> findAllByLoanStatus(int loanStatus);
    List<LoanRequestEntity> findByUserId(Long userId);
}
