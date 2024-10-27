package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Entities.LoanRequestEntity;
import com.Tingeso.prestabanco.Services.LoanRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/PrestaBanco/loanRequest")
@CrossOrigin("*")
public class LoanRequestController {
    @Autowired
    LoanRequestService loanRequestService;

    @GetMapping("/getLoanRequestById/{loanRequestId}")
    public ResponseEntity<LoanRequestEntity> getLoanRequestById(@PathVariable Long loanRequestId) {
        LoanRequestEntity loanRequest = loanRequestService.getLoanRequestById(loanRequestId);
        return ResponseEntity.ok(loanRequest);
    }

    @GetMapping("/getClientRequests/{userId}")
    public ResponseEntity<List<LoanRequestEntity>> getClientRequests(@PathVariable Long userId) {
        List<LoanRequestEntity> loanRequests = loanRequestService.getClientRequests(userId);
        return ResponseEntity.ok(loanRequests);
    }

    @GetMapping("/getLoanRequestsByStatus/{loanStatus}")
    public ResponseEntity<List<LoanRequestEntity>> getLoanRequestsByStatus(@PathVariable Integer loanStatus) {
        List<LoanRequestEntity> loanRequests = loanRequestService.getLoanRequestsByStatus(loanStatus);
        return ResponseEntity.ok(loanRequests);
    }

    @GetMapping("/getLoanRequests")
    public ResponseEntity<List<LoanRequestEntity>> getLoanRequests() {
        List<LoanRequestEntity> loanRequests = loanRequestService.getLoanRequests();
        return ResponseEntity.ok(loanRequests);
    }

    @PostMapping("/simulateMortgage/{loanAmount}/{propertyValue}/{loan_time}/{loan_type}")
    public double simulateMortgage(@PathVariable Long loanAmount, @PathVariable Long propertyValue, @PathVariable Integer loan_time, @PathVariable Integer loan_type) {
        return loanRequestService.simulateMortgage(loanAmount, propertyValue, loan_time, loan_type);
    }

    @PostMapping("/calculateTotal")
    public Long calculateTotalCost(@RequestBody LoanRequestEntity loanRequest) {
        return loanRequestService.calculateTotal(loanRequest);
    }

    @PostMapping("/requestMortgage")
    public ResponseEntity<LoanRequestEntity> requestMortgage(@RequestBody LoanRequestEntity loanRequest) {

        // Llamar al servicio que maneja la lógica de conversión y procesamiento
        LoanRequestEntity newLoanRequest = loanRequestService.requestMortgage(loanRequest);

        if (newLoanRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newLoanRequest);
    }

    @PutMapping("/evaluateLoanRequest")
    public ResponseEntity<LoanRequestEntity> evaluateLoanRequest(@RequestBody LoanEvaluationEntity loanEvaluation) {
        LoanRequestEntity loanRequest = loanRequestService.evaluateLoanRequest(loanEvaluation);
        if(loanRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(loanRequest);
    }

    @PutMapping("/acceptConditions/{loanRequestId}/{response}")
    public ResponseEntity<LoanRequestEntity> acceptConditions(@PathVariable Long loanRequestId, @PathVariable Boolean response) {
        LoanRequestEntity loanRequest = loanRequestService.acceptConditions(loanRequestId, response);
        return ResponseEntity.ok(loanRequest);
    }

}
