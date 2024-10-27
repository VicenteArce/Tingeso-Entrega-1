package com.Tingeso.prestabanco.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan_evaluation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanEvaluationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long evaluationId;
    private Long loanRequestId;     // Foreign key from LoanRequestEntity

    boolean dicom;
    boolean stability;
    boolean savingsHistory;
    boolean periodicSavings;
    boolean recentsWithdrawals;
    Integer savingAntiquity;
    Long monthlyDebt;
    Long savings;


}
