package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Services.LoanEvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(LoanEvaluationController.class)
public class LoanEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanEvaluationService loanEvaluationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateLoanEvaluation_thenReturnNewLoanEvaluation() throws Exception {
        LoanEvaluationEntity loanEvaluation = new LoanEvaluationEntity(
                null,           // ID will be auto-generated
                1L,             // loanRequestId
                true,           // dicom
                true,           // stability
                true,           // savingsHistory
                true,           // periodicSavings
                false,          // recentsWithdrawals
                5,              // savingAntiquity
                50000L,         // monthlyDebt
                200000L         // savings
        );

        when(loanEvaluationService.createLoanEvaluation(any(LoanEvaluationEntity.class)))
                .thenReturn(new LoanEvaluationEntity(
                        1L, loanEvaluation.getLoanRequestId(), loanEvaluation.isDicom(),
                        loanEvaluation.isStability(), loanEvaluation.isSavingsHistory(),
                        loanEvaluation.isPeriodicSavings(), loanEvaluation.isRecentsWithdrawals(),
                        loanEvaluation.getSavingAntiquity(), loanEvaluation.getMonthlyDebt(),
                        loanEvaluation.getSavings()
                ));

        mockMvc.perform(post("/PrestaBanco/loanEvaluation/createLoanEvaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanEvaluation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value(1L))
                .andExpect(jsonPath("$.loanRequestId").value(1L))
                .andExpect(jsonPath("$.dicom").value(true))
                .andExpect(jsonPath("$.stability").value(true))
                .andExpect(jsonPath("$.savingsHistory").value(true))
                .andExpect(jsonPath("$.periodicSavings").value(true))
                .andExpect(jsonPath("$.recentsWithdrawals").value(false))
                .andExpect(jsonPath("$.savingAntiquity").value(5))
                .andExpect(jsonPath("$.monthlyDebt").value(50000))
                .andExpect(jsonPath("$.savings").value(200000));
    }

    @Test
    void whenCreateLoanEvaluationFails_thenReturnBadRequest() throws Exception {
        LoanEvaluationEntity loanEvaluation = new LoanEvaluationEntity(
                null,           // ID will be auto-generated
                1L,             // loanRequestId
                false,          // dicom
                false,          // stability
                false,          // savingsHistory
                false,          // periodicSavings
                true,           // recentsWithdrawals
                0,              // savingAntiquity
                0L,             // monthlyDebt
                0L              // savings
        );

        when(loanEvaluationService.createLoanEvaluation(any(LoanEvaluationEntity.class)))
                .thenReturn(null);

        mockMvc.perform(post("/PrestaBanco/loanEvaluation/createLoanEvaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanEvaluation)))
                .andExpect(status().isBadRequest());
    }
}
