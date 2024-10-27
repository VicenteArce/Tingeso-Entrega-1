package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.LoanEvaluationEntity;
import com.Tingeso.prestabanco.Entities.LoanRequestEntity;
import com.Tingeso.prestabanco.Entities.UserEntity;
import com.Tingeso.prestabanco.Repositories.LoanEvaluationRepository;
import com.Tingeso.prestabanco.Repositories.DocumentsRepository;
import com.Tingeso.prestabanco.Repositories.LoanRequestRepository;
import com.Tingeso.prestabanco.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanRequestServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanRequestRepository loanRequestRepository;
    @Mock
    private DocumentsRepository loanRequestDocumentsRepository;
    @Mock
    private LoanEvaluationRepository loanEvaluationRepository;

    @InjectMocks
    private LoanRequestService loanRequestService;

    private LoanRequestEntity loanRequest;
    private UserEntity user;
    private LoanEvaluationEntity loanEvaluation;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUserId(1L);
        user.setRut("21538935-9");
        user.setName("Vicente");
        user.setLastName("Arce");
        user.setEmail("vicente.arce@example.com");
        user.setPassword("securePass123");
        user.setMonthlyIncome(1500000L);
        user.setRol(1);
        user.setBirthDate("30-03-2004");

        loanRequest = new LoanRequestEntity();
        loanRequest.setUserId(1L);
        loanRequest.setLoanAmount(2000000L);
        loanRequest.setPropertyValue(5000000L);
        loanRequest.setLoanTime(20);
        loanRequest.setLoanType(1);
        loanRequest.setLoanStatus(1);

        loanEvaluation = new LoanEvaluationEntity();
        loanEvaluation.setLoanRequestId(1L);
        loanEvaluation.setMonthlyDebt(0L);
        loanEvaluation.setDicom(false);
        loanEvaluation.setStability(true);
        loanEvaluation.setSavings(500000L);
        loanEvaluation.setSavingsHistory(true);
        loanEvaluation.setPeriodicSavings(true);
        loanEvaluation.setSavingAntiquity(3);
        loanEvaluation.setRecentsWithdrawals(false);
    }

    @Test
    void whenRequestMortgageWithValidData_thenSaveLoanRequest() {
        // Configurar mocks
        when(loanRequestRepository.save(loanRequest)).thenReturn(loanRequest);

        // Ejecutar
        LoanRequestEntity savedLoanRequest = loanRequestService.requestMortgage(loanRequest);

        // Verificar
        assertThat(savedLoanRequest).isNotNull();
        verify(loanRequestRepository).save(loanRequest);
    }

    @Test
    void whenRequestMortgageWithInvalidLoanAmount_thenReturnNull() {
        loanRequest.setLoanAmount(-5000L);

        LoanRequestEntity result = loanRequestService.requestMortgage(loanRequest);

        assertThat(result).isNull();
        verify(loanRequestRepository, never()).save(any());
    }

    @Test
    void whenRequestMortgageWithInvalidLoanType_thenReturnNull() {
        loanRequest.setLoanType(5); // Tipo inválido

        LoanRequestEntity result = loanRequestService.requestMortgage(loanRequest);

        assertThat(result).isNull();
        verify(loanRequestRepository, never()).save(any());
    }

    @Test
    void whenGetLoanRequests_thenReturnAllRequests() {
        List<LoanRequestEntity> loanRequests = new ArrayList<>();
        loanRequests.add(loanRequest);
        when(loanRequestRepository.findAll()).thenReturn(loanRequests);

        List<LoanRequestEntity> result = loanRequestService.getLoanRequests();

        assertThat(result).isEqualTo(loanRequests);
    }

    @Test
    void whenGetLoanRequestsByStatus_thenReturnFilteredRequests() {
        List<LoanRequestEntity> loanRequests = new ArrayList<>();
        loanRequests.add(loanRequest);
        when(loanRequestRepository.findAllByLoanStatus(1)).thenReturn(loanRequests);

        List<LoanRequestEntity> result = loanRequestService.getLoanRequestsByStatus(1);

        assertThat(result).isEqualTo(loanRequests);
    }

    @Test
    void whenEvaluateLoanRequestWithValidConditions_thenPreApproveLoan() {
        // Configurar `loanRequest` para cumplir condiciones de pre-aprobación
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        // Configurar `user` con ingresos adecuados y edad dentro del rango
        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        // Configurar `loanEvaluation` para cumplir todas las condiciones de aprobación
        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos
        loanEvaluation.setSavings(250000L); // Ahorros suficientes (al menos 10% del préstamo)
        loanEvaluation.setSavingsHistory(true); // Historial de ahorro de 12 meses
        loanEvaluation.setPeriodicSavings(true); // Ahorros periódicos de al menos 6 meses
        loanEvaluation.setSavingAntiquity(2); // Antigüedad de ahorros >= 2 años
        loanEvaluation.setRecentsWithdrawals(false); // Sin retiros recientes

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea pre-aprobado (estado 4)
        assertThat(result.getLoanStatus()).isEqualTo(4);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR1_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R1
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        // Configurar `user` con ingresos adecuados y edad dentro del rango
        user.setMonthlyIncome(10000L); // Ingreso mensual insuficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        // Configurar `loanEvaluation` para cumplir todas las condiciones de aprobación
        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos
        loanEvaluation.setSavings(250000L); // Ahorros suficientes (al menos 10% del préstamo)
        loanEvaluation.setSavingsHistory(true); // Historial de ahorro de 12 meses
        loanEvaluation.setPeriodicSavings(true); // Ahorros periódicos de al menos 6 meses
        loanEvaluation.setSavingAntiquity(2); // Antigüedad de ahorros >= 2 años
        loanEvaluation.setRecentsWithdrawals(false); // Sin retiros recientes

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR2_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R2
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango


        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        // Configurar `loanEvaluation` para cumplir todas las condiciones de aprobación menos R2
        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(true); // Con historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos
        loanEvaluation.setSavings(250000L); // Ahorros suficientes (al menos 10% del préstamo)
        loanEvaluation.setSavingsHistory(true); // Historial de ahorro de 12 meses
        loanEvaluation.setPeriodicSavings(true); // Ahorros periódicos de al menos 6 meses
        loanEvaluation.setSavingAntiquity(2); // Antigüedad de ahorros >= 2 años
        loanEvaluation.setRecentsWithdrawals(false); // Sin retiros recientes

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);

    }

    @Test
    void whenEvaluateLoanRequestWithWrongR3_thenRejactTheLoan() {
        // Configurar `loanRequest` para Rechazar R3
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(false); // Empleo inestable

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);

    }

    @Test
    void whenEvaluateLoanRequestWithWrongR4_thenRejactTheLoan() {
        // Configurar `loanRequest` para Rechazar R4
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(1000000L); // Deuda que no permite pasar el límite del 50% de ingresos

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR5firstHome_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R5
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(27000000L); // Cantidad del préstamo mayor del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos


        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR6OlderPerson_thenRejactTheLoan(){
        // Configurar `loanRequest` para cumplir condiciones de pre-aprobación
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        // Configurar `user` con ingresos adecuados y edad dentro del rango
        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1925"); // Edad mayor a 18 y 70 años

        // Configurar `loanEvaluation` para cumplir todas las condiciones de aprobación
        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos
        loanEvaluation.setSavings(250000L); // Ahorros suficientes (al menos 10% del préstamo)
        loanEvaluation.setSavingsHistory(true); // Historial de ahorro de 12 meses
        loanEvaluation.setPeriodicSavings(true); // Ahorros periódicos de al menos 6 meses
        loanEvaluation.setSavingAntiquity(2); // Antigüedad de ahorros >= 2 años
        loanEvaluation.setRecentsWithdrawals(false); // Sin retiros recientes

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }


    @Test
    void whenEvaluateLoanRequestWithWrongR5secondHome_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R5
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(2); // Second home
        loanRequest.setLoanAmount(27000000L); // Cantidad del préstamo mayor del 70% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos


        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR5ComercialProperty_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R5
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(3); // Comercial
        loanRequest.setLoanAmount(27000000L); // Cantidad del préstamo mayor del 60% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos


        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR5remodelation_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R5
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(4); // Remodelación
        loanRequest.setLoanAmount(27000000L); // Cantidad del préstamo mayor del 50% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la remodelacion
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos


        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestWithWrongR5defaultCase_thenRejactTheLoan(){
        // Configurar `loanRequest` para Rechazar R5
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(5); // no valido
        loanRequest.setLoanAmount(27000000L); // Cantidad del préstamo mayor del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos


        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea rechazado (estado 7)
        assertThat(result.getLoanStatus()).isEqualTo(7);
    }

    @Test
    void whenEvaluateLoanRequestCounterIs3Or4(){
        // Configurar `loanRequest` para cumplir condiciones de pre-aprobación
        loanRequest.setLoanStatus(1); // Estatus inicial, por ejemplo, "pendiente"
        loanRequest.setLoanType(1); // Primer hogar
        loanRequest.setLoanAmount(2000000L); // Cantidad del préstamo dentro del 80% del valor de la propiedad
        loanRequest.setPropertyValue(3000000L); // Valor de la propiedad mayor al 80%
        loanRequest.setLoanTime(20); // Plazo del préstamo dentro del rango

        // Configurar `user` con ingresos adecuados y edad dentro del rango
        user.setMonthlyIncome(500000L); // Ingreso mensual suficiente para la relación de pago mensual
        user.setBirthDate("30-03-1975"); // Edad entre 18 y 70 años

        // Configurar `loanEvaluation` para cumplir todas las condiciones de aprobación
        loanEvaluation.setLoanRequestId(loanRequest.getLoanRequestId());
        loanEvaluation.setDicom(false); // Sin historial negativo
        loanEvaluation.setStability(true); // Empleo estable
        loanEvaluation.setMonthlyDebt(100000L); // Deuda que permite pasar el límite del 50% de ingresos
        loanEvaluation.setSavings(250000L); // Ahorros suficientes (al menos 10% del préstamo)
        loanEvaluation.setSavingsHistory(false); // Historial de ahorro de 12 meses
        loanEvaluation.setPeriodicSavings(true); // Ahorros periódicos de al menos 6 meses
        loanEvaluation.setSavingAntiquity(1); // Antigüedad de ahorros >= 2 años
        loanEvaluation.setRecentsWithdrawals(false); // Sin retiros recientes

        // Stubs de Mockito para repositorios
        when(loanRequestRepository.findById(loanEvaluation.getLoanRequestId())).thenReturn(Optional.of(loanRequest));
        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.of(user));
        when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Llamar al método y verificar resultado
        LoanRequestEntity result = loanRequestService.evaluateLoanRequest(loanEvaluation);

        // Verificar que el estado del préstamo sea pre-aprobado (estado 4)
        assertThat(result.getLoanStatus()).isEqualTo(4);
    }
    @Test
    void whenCalculateMonthlyPayment_thenReturnCorrectAmount() {
        // Configuración con lenient para evitar la excepción
        lenient().when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Suponiendo que `calculateMonthlyPayment` solo necesita un `loanRequest` ya configurado
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanAmount(2000000L);
        loanRequest.setPropertyValue(5000000L);
        loanRequest.setLoanTime(20);
        loanRequest.setLoanType(1);

        // Ejecutar
        Long monthlyPayment = loanRequestService.calculateMonthlyPayment(loanRequest);

        // Verificar
        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment > 0);
    }

    @Test
    void whenCalculateTotalLoanAmount_thenReturnCorrectTotal() {
        // Configuración usando lenient() para evitar la excepción
        lenient().when(loanRequestRepository.save(any(LoanRequestEntity.class))).thenReturn(loanRequest);

        // Crear un LoanRequestEntity con datos relevantes
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setLoanAmount(2000000L);
        loanRequest.setPropertyValue(5000000L);
        loanRequest.setLoanTime(20);
        loanRequest.setLoanType(1);

        // Ejecutar el método de prueba
        Long totalAmount = loanRequestService.calculateTotal(loanRequest);

        // Verificar los resultados esperados
        assertNotNull(totalAmount);
        assertTrue(totalAmount > 0);
    }


    @Test
    void whenSimulateMortgageWithValidData_thenReturnMonthlyPayment() {
        double monthlyPayment = loanRequestService.simulateMortgage(loanRequest.getLoanAmount(), loanRequest.getPropertyValue(), loanRequest.getLoanTime(), loanRequest.getLoanType());

        assertThat(monthlyPayment).isGreaterThan(0);
    }

    @Test
    void whenSimulateMortgageWithInvalidLoanType_thenReturnErrorValue() {
        double monthlyPayment = loanRequestService.simulateMortgage(loanRequest.getLoanAmount(), loanRequest.getPropertyValue(), loanRequest.getLoanTime(), 5);

        assertThat(monthlyPayment).isEqualTo(-2); // Código de error para tipo de préstamo inválido
    }


    @Test
    void whenGetClientLoanRequests_thenReturnFilteredRequests() {
        List<LoanRequestEntity> loanRequests = new ArrayList<>();
        loanRequests.add(loanRequest);

    }

    @Test
    void testGetClientRequests() {
        // Configurar datos de prueba
        List<LoanRequestEntity> loanRequests = new ArrayList<>();
        loanRequests.add(loanRequest);

        // Configurar comportamiento simulado del repositorio
        when(loanRequestRepository.findByUserId(user.getUserId())).thenReturn(loanRequests);

        // Llamar al método que se va a probar
        List<LoanRequestEntity> result = loanRequestService.getClientRequests(user.getUserId());

        // Verificar resultados
        assertNotNull(result);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(user.getUserId());
        verify(loanRequestRepository, times(1)).findByUserId(user.getUserId());
    }

    @Test
    void testGetLoanRequestById() {
        // Configurar datos de prueba
        Long loanRequestId = 1L;

        // Configurar comportamiento simulado del repositorio
        when(loanRequestRepository.findById(loanRequestId)).thenReturn(Optional.of(loanRequest));

        // Llamar al método que se va a probar
        LoanRequestEntity result = loanRequestService.getLoanRequestById(loanRequestId);

        // Verificar resultados
        assertNotNull(result);
        assertThat(result.getLoanAmount()).isEqualTo(loanRequest.getLoanAmount());
        assertThat(result.getLoanType()).isEqualTo(loanRequest.getLoanType());
        verify(loanRequestRepository, times(1)).findById(loanRequestId);
    }

    @Test
    void whenAcceptConditionsIsTrue_thenSetLoanStatusTo5() {
        // Configurar mocks
        when(loanRequestRepository.findById(1L)).thenReturn(Optional.of(loanRequest));
        when(loanRequestRepository.save(loanRequest)).thenReturn(loanRequest);

        // Ejecutar
        LoanRequestEntity updatedLoanRequest = loanRequestService.acceptConditions(1L, true);

        // Verificar
        assertThat(updatedLoanRequest).isNotNull();
        assertThat(updatedLoanRequest.getLoanStatus()).isEqualTo(5);
        verify(loanRequestRepository).save(loanRequest);
    }

    @Test
    void whenAcceptConditionsIsFalse_thenSetLoanStatusTo8() {
        // Configurar mocks
        when(loanRequestRepository.findById(1L)).thenReturn(Optional.of(loanRequest));
        when(loanRequestRepository.save(loanRequest)).thenReturn(loanRequest);

        // Ejecutar
        LoanRequestEntity updatedLoanRequest = loanRequestService.acceptConditions(1L, false);

        // Verificar
        assertThat(updatedLoanRequest).isNotNull();
        assertThat(updatedLoanRequest.getLoanStatus()).isEqualTo(8);
        verify(loanRequestRepository).save(loanRequest);
    }

    @Test
    void whenRequestMortgageWithInvalidTypeOfLoan_thenReturnNull(){
        loanRequest.setLoanType(5);

        LoanRequestEntity result = loanRequestService.requestMortgage(loanRequest);

        assertThat(result).isNull();
        verify(loanRequestRepository, never()).save(any());
    }


}
