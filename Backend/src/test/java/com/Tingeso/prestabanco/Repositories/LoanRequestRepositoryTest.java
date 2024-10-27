package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.LoanRequestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class LoanRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Test
    public void testFindByLoanRequestId() {
        // Crear y guardar una solicitud de préstamo
        LoanRequestEntity loanRequest = new LoanRequestEntity();
        loanRequest.setUserId(1L);
        loanRequest.setLoanAmount(1000000L);
        loanRequest.setPropertyValue(1500000L);
        loanRequest.setLoanType(1);
        loanRequest.setLoanTime(20);
        loanRequest.setLoanStatus(1);

        entityManager.persist(loanRequest);
        entityManager.flush();

        // Buscar la solicitud de préstamo por ID
        LoanRequestEntity foundRequest = loanRequestRepository.findByLoanRequestId(loanRequest.getLoanRequestId());

        // Verificar que la solicitud de préstamo fue encontrada
        assertNotNull(foundRequest);
        assertEquals(loanRequest.getLoanRequestId(), foundRequest.getLoanRequestId());
    }

    @Test
    public void testFindAllByLoanStatus() {
        // Crear y guardar varias solicitudes de préstamo
        LoanRequestEntity loanRequest1 = new LoanRequestEntity();
        loanRequest1.setUserId(1L);
        loanRequest1.setLoanAmount(1000000L);
        loanRequest1.setPropertyValue(1500000L);
        loanRequest1.setLoanType(1);
        loanRequest1.setLoanTime(20);
        loanRequest1.setLoanStatus(1); // Estado 1

        LoanRequestEntity loanRequest2 = new LoanRequestEntity();
        loanRequest2.setUserId(2L);
        loanRequest2.setLoanAmount(2000000L);
        loanRequest2.setPropertyValue(2500000L);
        loanRequest2.setLoanType(2);
        loanRequest2.setLoanTime(15);
        loanRequest2.setLoanStatus(1); // Estado 1

        LoanRequestEntity loanRequest3 = new LoanRequestEntity();
        loanRequest3.setUserId(3L);
        loanRequest3.setLoanAmount(3000000L);
        loanRequest3.setPropertyValue(3500000L);
        loanRequest3.setLoanType(1);
        loanRequest3.setLoanTime(10);
        loanRequest3.setLoanStatus(2); // Estado 2

        entityManager.persist(loanRequest1);
        entityManager.persist(loanRequest2);
        entityManager.persist(loanRequest3);
        entityManager.flush();

        // Buscar todas las solicitudes con estado 1
        List<LoanRequestEntity> foundRequests = loanRequestRepository.findAllByLoanStatus(1);

        // Verificar que se encontraron las solicitudes correctas
        assertEquals(2, foundRequests.size());
        assertTrue(foundRequests.contains(loanRequest1));
        assertTrue(foundRequests.contains(loanRequest2));
    }

    @Test
    public void testFindByUserId() {
        // Crear y guardar solicitudes de préstamo
        LoanRequestEntity loanRequest1 = new LoanRequestEntity();
        loanRequest1.setUserId(1L);
        loanRequest1.setLoanAmount(1000000L);
        loanRequest1.setPropertyValue(1500000L);
        loanRequest1.setLoanType(1);
        loanRequest1.setLoanTime(20);
        loanRequest1.setLoanStatus(1);

        LoanRequestEntity loanRequest2 = new LoanRequestEntity();
        loanRequest2.setUserId(1L);
        loanRequest2.setLoanAmount(2000000L);
        loanRequest2.setPropertyValue(2500000L);
        loanRequest2.setLoanType(2);
        loanRequest2.setLoanTime(15);
        loanRequest2.setLoanStatus(1);

        LoanRequestEntity loanRequest3 = new LoanRequestEntity();
        loanRequest3.setUserId(2L);
        loanRequest3.setLoanAmount(3000000L);
        loanRequest3.setPropertyValue(3500000L);
        loanRequest3.setLoanType(1);
        loanRequest3.setLoanTime(10);
        loanRequest3.setLoanStatus(1);

        entityManager.persist(loanRequest1);
        entityManager.persist(loanRequest2);
        entityManager.persist(loanRequest3);
        entityManager.flush();

        // Buscar las solicitudes de préstamo por userId
        List<LoanRequestEntity> foundRequests = loanRequestRepository.findByUserId(1L);

        // Verificar que se encontraron las solicitudes correctas
        assertEquals(2, foundRequests.size());
        assertTrue(foundRequests.contains(loanRequest1));
        assertTrue(foundRequests.contains(loanRequest2));
    }
}
