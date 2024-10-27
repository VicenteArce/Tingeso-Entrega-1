package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Repositories.LoanEvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanEvaluationServiceTest {

    @Mock
    private LoanEvaluationRepository loanEvaluationRepository;

    @InjectMocks
    private LoanEvaluationService loanEvaluationService;

    private LoanEvaluationEntity loanEvaluation;

    @BeforeEach
    void setUp() {
        loanEvaluation = new LoanEvaluationEntity(
                null,               // El ID se generará automáticamente
                1L,                 // loanRequestId
                true,               // dicom
                true,               // stability
                true,               // savingsHistory
                true,               // periodicSavings
                false,              // recentsWithdrawals
                5,                  // savingAntiquity
                300000L,            // monthlyDebt
                5000000L            // savings
        );
    }

    @Test
    void testCreateLoanEvaluation_Success() {
        // Configurar el mock para devolver la entidad guardada
        when(loanEvaluationRepository.save(any(LoanEvaluationEntity.class))).thenReturn(loanEvaluation);

        // Llamar al método a probar
        LoanEvaluationEntity createdLoanEvaluation = loanEvaluationService.createLoanEvaluation(loanEvaluation);

        // Verificar que la entidad retornada es la esperada
        assertThat(createdLoanEvaluation).isEqualTo(loanEvaluation);

        // Verificar que el método save fue llamado una vez con cualquier instancia de LoanEvaluationEntity
        verify(loanEvaluationRepository, times(1)).save(any(LoanEvaluationEntity.class));
    }
}
