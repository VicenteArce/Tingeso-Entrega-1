package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Entities.LoanRequestEntity;
import com.Tingeso.prestabanco.Services.LoanRequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanRequestController.class)
public class LoanRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanRequestService loanRequestService;

    @Test
    void whenGetLoanRequestById_thenReturnLoanRequest() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanRequestId(1L);

        when(loanRequestService.getLoanRequestById(1L)).thenReturn(loanRequest);

        mockMvc.perform(get("/PrestaBanco/loanRequest/getLoanRequestById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanRequestId").value(1L));
    }

    @Test
    void whenGetClientRequests_thenReturnLoanRequestsList() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setUserId(1L);

        when(loanRequestService.getClientRequests(1L)).thenReturn(List.of(loanRequest));

        mockMvc.perform(get("/PrestaBanco/loanRequest/getClientRequests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    void whenGetLoanRequestsByStatus_thenReturnLoanRequestsList() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanStatus(2);

        when(loanRequestService.getLoanRequestsByStatus(2)).thenReturn(List.of(loanRequest));

        mockMvc.perform(get("/PrestaBanco/loanRequest/getLoanRequestsByStatus/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanStatus").value(2));
    }

    @Test
    void whenGetLoanRequests_thenReturnAllLoanRequests() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanRequestId(1L);

        when(loanRequestService.getLoanRequests()).thenReturn(List.of(loanRequest));

        mockMvc.perform(get("/PrestaBanco/loanRequest/getLoanRequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loanRequestId").value(1L));
    }

    @Test
    void whenSimulateMortgage_thenReturnSimulationResult() throws Exception {
        when(loanRequestService.simulateMortgage(100000L, 200000L, 20, 1)).thenReturn(5000.0);

        mockMvc.perform(post("/PrestaBanco/loanRequest/simulateMortgage/100000/200000/20/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5000.0"));
    }

    @Test
    void whenCalculateTotalCost_thenReturnTotalCost() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanRequestId(1L);

        when(loanRequestService.calculateTotal(loanRequest)).thenReturn(300000L);

        String loanRequestJson = "{\"loanRequestId\":1}";

        mockMvc.perform(post("/PrestaBanco/loanRequest/calculateTotal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("300000"));
    }

    @Test
    void whenRequestMortgage_thenReturnNewLoanRequest() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanRequestId(1L);

        when(loanRequestService.requestMortgage(Mockito.any(LoanRequestEntity.class))).thenReturn(loanRequest);

        String loanRequestJson = "{\"loanRequestId\":1}";

        mockMvc.perform(post("/PrestaBanco/loanRequest/requestMortgage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanRequestId").value(1L));
    }

    @Test
    void whenEvaluateLoanRequest_thenReturnEvaluatedLoanRequest() throws Exception {
        LoanEvaluationEntity loanEvaluation = new LoanEvaluationEntity();
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanRequestId(1L);

        when(loanRequestService.evaluateLoanRequest(Mockito.any(LoanEvaluationEntity.class))).thenReturn(loanRequest);

        String loanEvaluationJson = "{\"evaluationDetails\":\"details\"}";

        mockMvc.perform(put("/PrestaBanco/loanRequest/evaluateLoanRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanEvaluationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanRequestId").value(1L));
    }

    @Test
    void whenAcceptConditions_thenReturnUpdatedLoanRequest() throws Exception {
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanRequestId(1L);

        when(loanRequestService.acceptConditions(1L, true)).thenReturn(loanRequest);

        mockMvc.perform(put("/PrestaBanco/loanRequest/acceptConditions/1/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanRequestId").value(1L));
    }
}
