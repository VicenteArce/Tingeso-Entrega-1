package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Entities.LoanRequestEntity;
import com.Tingeso.prestabanco.Entities.UserEntity;
import com.Tingeso.prestabanco.Repositories.LoanEvaluationRepository;
import com.Tingeso.prestabanco.Repositories.DocumentsRepository;
import com.Tingeso.prestabanco.Repositories.LoanRequestRepository;
import com.Tingeso.prestabanco.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LoanRequestService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    LoanRequestRepository loanRequestRepository;

    @Autowired
    DocumentsRepository loanRequestDocumentsRepository;

    @Autowired
    LoanEvaluationRepository loanEvaluationRepository;

    public LoanRequestEntity requestMortgage(LoanRequestEntity loanRequest) {
        Long loanAmount = loanRequest.getLoanAmount();
        Long propertyValue = loanRequest.getPropertyValue();
        Integer loan_time = loanRequest.getLoanTime();
        Integer loan_type = loanRequest.getLoanType();

        // Validar valores
        if (loanAmount <= 0 || propertyValue <= 0 || loan_time <= 0) {
            return null;
        }
        if (loan_type < 1 || loan_type > 4) {
            return null;
        }

        // Establecer el estado del préstamo
        loanRequest.setLoanStatus(3);

        return loanRequestRepository.save(loanRequest);
    }




    // Method to get all loan requests

    /**
     * Method to get all loan requests
     * @return List<LoanRequestEntity> List of loan requests
     */
    public List<LoanRequestEntity> getLoanRequests() {
        return loanRequestRepository.findAll();
    }

    // Method to get all loan request by status

    /**
     * Method to get all loan requests by status
     * @param status Integer status
     * @return List<LoanRequestEntity> List of loan requests
     */
    public List<LoanRequestEntity> getLoanRequestsByStatus(Integer status) {
        return loanRequestRepository.findAllByLoanStatus(status);
    }

    // Method to get all loan requests from a client

    /**
     * Method to get all loan requests from a client
     * @param userId Long user id
     * @return List<LoanRequestEntity> List of loan requests
     */
    public List<LoanRequestEntity> getClientRequests(Long userId){
        return loanRequestRepository.findByUserId(userId);
    }

    /**
     * Method to get a loan request by id
     * @param loanRequestId Long loan request id
     * @return LoanRequestEntity loan request
     */
    public LoanRequestEntity getLoanRequestById(Long loanRequestId){
        return loanRequestRepository.findById(loanRequestId).orElse(null);
    }



    /**
     * P4: Method to evaluate a loan request
     * @param loanEvaluation LoanEvaluationEntity
     * @return LoanRequestEntity loan request
     */
    public LoanRequestEntity evaluateLoanRequest(LoanEvaluationEntity loanEvaluation){
        Long loanId = loanEvaluation.getLoanRequestId();
        LoanRequestEntity loanRequest = loanRequestRepository.findById(loanId).get();
        Long userId = loanRequest.getUserId();
        UserEntity user = userRepository.findById(userId).orElse(null);
        Long monthlyPayment = calculateMonthlyPayment(loanRequestRepository.findById(loanId).get());
        Long monthlyIncome = user.getMonthlyIncome();
        Long loanAmount = loanRequest.getLoanAmount();
        Long propertyValue = loanRequest.getPropertyValue();
        // R1
        if(((double) monthlyPayment / monthlyIncome) > 0.35){
            loanRequest.setLoanStatus(7);
            return loanRequestRepository.save(loanRequest);
        }

        // R2
        if(loanEvaluation.isDicom()){
            loanRequest.setLoanStatus(7);
            return loanRequestRepository.save(loanRequest);
        }

        // R3
        if(!loanEvaluation.isStability()){
            loanRequest.setLoanStatus(7);
            return loanRequestRepository.save(loanRequest);
        }

        // R4
        if(loanEvaluation.getMonthlyDebt() + monthlyPayment > monthlyIncome * 0.5){
            loanRequest.setLoanStatus(7);
            return loanRequestRepository.save(loanRequest);
        }

        // R5
        switch(loanRequest.getLoanType()){
            // First home
            case 1:
                if(loanAmount > propertyValue * 0.8){
                    loanRequest.setLoanStatus(7);
                    return loanRequestRepository.save(loanRequest);
                }
                break;
            // Second home
            case 2:
                if(loanAmount > propertyValue * 0.7){
                    loanRequest.setLoanStatus(7);
                    return loanRequestRepository.save(loanRequest);
                }
                break;
            // Comercial property
            case 3:
                if(loanAmount > propertyValue * 0.6){
                    loanRequest.setLoanStatus(7);
                    return loanRequestRepository.save(loanRequest);
                }
                break;
            // Remodeling
            case 4:
                if(loanAmount > propertyValue * 0.5){
                    loanRequest.setLoanStatus(7);
                    return loanRequestRepository.save(loanRequest);
                }
                break;
            default:
                loanRequest.setLoanStatus(7);
                return loanRequestRepository.save(loanRequest);
        }

        // R6
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthdate = LocalDate.parse(user.getBirthDate(), fmt);
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthdate.getYear();
        if(age > 70 || age < 18){
            loanRequest.setLoanStatus(7);
            return loanRequestRepository.save(loanRequest);
        }

        // R7
        int counter = 0;
        boolean minSaved;
        boolean balanceAntiquityRelation;

        // R71: min savings (10% of the loan amount)
        minSaved = loanEvaluation.getSavings() >= loanAmount * 0.1;
        if(minSaved){
            counter++;
        }

        // R72: savings history (at least 12 months)
        if(loanEvaluation.isSavingsHistory()){
            counter++;
        }

        // R73: periodic savings (at least 6 months)
        if(loanEvaluation.isPeriodicSavings()){
            counter++;
        }

        // R74: savings antiquity (at least 2 years)
        if(loanEvaluation.getSavingAntiquity() >= 2){
            balanceAntiquityRelation = !(loanEvaluation.getSavings() < loanAmount * 0.1);
        } else {
            balanceAntiquityRelation = !(loanEvaluation.getSavings() < loanAmount * 0.2);
        }
        if(balanceAntiquityRelation){
            counter++;
        }
        // R75: recent withdrawals (less than 3 in the last 6 months)
        if(!loanEvaluation.isRecentsWithdrawals()){
            counter++;
        }

        if(counter <= 4){
            if(counter <= 2){

                loanRequest.setLoanStatus(7);
                return loanRequestRepository.save(loanRequest);
            }
            // Lo pongo en 2 segun lo que dijo el cliente
            loanRequest.setLoanStatus(2);
            return loanRequestRepository.save(loanRequest);
        }

        // Si no se cumple ninguna de las reglas anteriores, entonces counter == 5, por lo que se pre-aprueba el credito
        loanRequest.setLoanStatus(4);
        return loanRequestRepository.save(loanRequest);
    }


    /**
     * Method to accept the conditions as a client for a loan request
     * @param loanRequestId Long loan request id
     * @return LoanRequestEntity loan request
     */
    public LoanRequestEntity acceptConditions(Long loanRequestId, boolean response){
        LoanRequestEntity loanRequest = loanRequestRepository.findById(loanRequestId).get();
        if(response){
            loanRequest.setLoanStatus(5);
            return loanRequestRepository.save(loanRequest);
        }
        loanRequest.setLoanStatus(8);
        return loanRequestRepository.save(loanRequest);
    }

    /**
     * P6: Method to calculate the total amount of the loan
     * @param loanRequest LoanRequestEntity
     * @return Long total amount
     */
    public Long calculateTotal(LoanRequestEntity loanRequest){
        double monthlyPayment = simulateMortgage(loanRequest.getLoanAmount(), loanRequest.getPropertyValue(), loanRequest.getLoanTime(), loanRequest.getLoanType());
        double loanAmount = loanRequest.getLoanAmount();
        double monthlyTotal = monthlyPayment + loanAmount * 0.0003 + 20000;

        return Math.round(monthlyTotal * loanRequest.getLoanTime() * 12 + loanAmount * 0.01);
    }

    /**
     * Method to calculate the monthly payment
     * @param loanRequest LoanRequestEntity
     * @return Long monthly payment
     */
    public Long calculateMonthlyPayment(LoanRequestEntity loanRequest){
        double monthlyPayment = simulateMortgage(loanRequest.getLoanAmount(), loanRequest.getPropertyValue(), loanRequest.getLoanTime(), loanRequest.getLoanType());
        double loanAmount = loanRequest.getLoanAmount();

        return Math.round(monthlyPayment + loanAmount * 0.0003 + 20000);
    }

    /**
     * Method to simulate a mortgage loan
     * @param loanAmount money requested
     * @param propertyValue property value
     * @param loan_time loan time in years
     * @param loan_type loan type (1: first home, 2: second home, 3: comercial property, 4: remmodeling)
     * @return double monthly payment
     */
    public double simulateMortgage(Long loanAmount, Long propertyValue, Integer loan_time, Integer loan_type){
        double interest = 0;
        double maxLoan = 0;
        // Debo verificar que el monto solicitado, el valor de la propiedad y el plazo sean validos (mayores a 0)
        if(loanAmount <= 0 || propertyValue <= 0 || loan_time <= 0){
            return -1;  // Valor para manejar errores en el front
        }
        // Verifico que el tipo de prestamo sea valido (1, 2, 3 o 4)
        if(loan_type < 1 || loan_type > 4){
            return -2;  // Valor para manejar errores en el front
        }
        switch (loan_type){
            case 1:
                interest = 0.045;
                maxLoan = propertyValue * 0.8;
                if(loan_time > 30){
                    return -3;
                }
                break;
            case 2:
                interest = 0.04;
                maxLoan = propertyValue * 0.7;
                if(loan_time > 20){
                    return -3;
                }
                break;
            case 3:
                interest = 0.05;
                maxLoan = propertyValue * 0.6;
                if(loan_time > 25){
                    return -3;
                }
                break;
            case 4:
                interest = 0.045;
                maxLoan = propertyValue * 0.5;
                if(loan_time > 15){
                    return -3;
                }
                break;
        }
        if(loanAmount > maxLoan){
            return -4;
        }
        double monthlyInterest = interest / 12;
        return loanAmount * (monthlyInterest * Math.pow(1 + monthlyInterest, loan_time * 12)) / (Math.pow(1 + monthlyInterest, loan_time * 12) - 1);
    }


}

