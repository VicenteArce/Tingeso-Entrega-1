package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Services.LoanEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/PrestaBanco/loanEvaluation")
@CrossOrigin("*")
public class LoanEvaluationController {
    @Autowired
    LoanEvaluationService loanEvaluationService;

    @PostMapping("/createLoanEvaluation")
    public ResponseEntity<LoanEvaluationEntity> createLoanEvaluation(@RequestBody LoanEvaluationEntity loanEvaluation) {
        LoanEvaluationEntity newLoanEvaluation = loanEvaluationService.createLoanEvaluation(loanEvaluation);
        if(newLoanEvaluation == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newLoanEvaluation);
    }
}
