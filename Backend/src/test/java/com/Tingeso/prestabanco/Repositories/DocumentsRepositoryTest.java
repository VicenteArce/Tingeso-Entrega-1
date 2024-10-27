package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.DocumentsEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class DocumentsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Test
    public void testFindByLoanRequestId() {
        // Crear y guardar documentos
        DocumentsEntity document1 = new DocumentsEntity();
        document1.setLoanRequestId(1L);
        document1.setUserId(1L);
        document1.setDocument(new byte[]{1, 2, 3}); // Ejemplo de contenido del documento
        document1.setIsUploaded(true);
        document1.setType("Income Proof");

        DocumentsEntity document2 = new DocumentsEntity();
        document2.setLoanRequestId(1L);
        document2.setUserId(1L);
        document2.setDocument(new byte[]{4, 5, 6}); // Otro documento
        document2.setIsUploaded(true);
        document2.setType("Appraisal Certificate");

        DocumentsEntity document3 = new DocumentsEntity();
        document3.setLoanRequestId(2L);
        document3.setUserId(2L);
        document3.setDocument(new byte[]{7, 8, 9});
        document3.setIsUploaded(false);
        document3.setType("Credit History");

        entityManager.persist(document1);
        entityManager.persist(document2);
        entityManager.persist(document3);
        entityManager.flush();

        // Buscar documentos por loanRequestId
        List<DocumentsEntity> foundDocuments = documentsRepository.findByLoanRequestId(1L);

        // Verificar que se encontraron los documentos correctos
        assertEquals(2, foundDocuments.size());
        assertTrue(foundDocuments.contains(document1));
        assertTrue(foundDocuments.contains(document2));
        assertFalse(foundDocuments.contains(document3));
    }
}
