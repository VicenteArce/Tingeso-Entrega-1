package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Repositories.LoanEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanEvaluationService {
    @Autowired
    LoanEvaluationRepository loanEvaluationRepository;

    /**
     * Método que crea una evaluación de préstamo
     * @param loanEvaluation Entidad de evaluación de préstamo
     * @return Entidad de evaluación de préstamo creada
     */
    public LoanEvaluationEntity createLoanEvaluation(LoanEvaluationEntity loanEvaluation) {
        return loanEvaluationRepository.save(loanEvaluation);
    }
}
