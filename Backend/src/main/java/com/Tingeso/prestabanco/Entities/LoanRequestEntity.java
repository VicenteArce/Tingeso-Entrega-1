package com.Tingeso.prestabanco.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long loanRequestId;
    private Long userId;            // Foreign key from UserEntity

    private Long loanAmount;        // In CLP
    private Long propertyValue;     // In CLP
    private Integer loanType;       // 1: first home, 2: second home, 3: comercial property, 4: remmodeling
    private Integer loanTime;       // In years
    private Integer loanStatus;


    // Necesito los siguientes documentos para solicitar un crédito
    //private byte[] incomeProof;         // 1
    //private byte[] appraisalCertificate; // 2
    //private byte[] creditHistory;       // 3
    //private byte[] firstHomeDeed;       // 4
    //private byte[] businessFinancials;  // 5
    //private byte[] businessPlan;        // 6
    //private byte[] incomeCertificate;   // 7
    //private byte[] remodelingBudget;    // 8
    //private byte[] updatedAppraisal;    // 9

}
