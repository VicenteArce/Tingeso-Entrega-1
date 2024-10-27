package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.DocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<DocumentsEntity, Long> {
    List<DocumentsEntity> findByLoanRequestId(Long loanRequestId);
}