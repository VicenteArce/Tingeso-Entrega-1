package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.DocumentsEntity;
import com.Tingeso.prestabanco.Repositories.DocumentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentsServiceTest {

    @Mock
    private DocumentsRepository documentsRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private DocumentsService documentsService;

    private final Long loanRequestId = 1L;
    private final Long userId = 1L;
    private final String fileType = "incomeProof";
    private final byte[] documentContent = {1, 2, 3, 4, 5};

    @BeforeEach
    void setUp() {
        lenient().when(file.isEmpty()).thenReturn(false);
    }

    @Test
    void testSaveLoanRequestDocument_Success() throws IOException {
        // Configurar el archivo simulado para devolver un contenido específico
        when(file.getBytes()).thenReturn(documentContent);

        // Llamar al método que se va a probar
        boolean result = documentsService.saveLoanRequestDocument(file, loanRequestId, userId, fileType);

        // Verificar que el resultado es verdadero
        assertThat(result).isTrue();

        // Verificar que se ha guardado la entidad en el repositorio
        verify(documentsRepository, times(1)).save(any(DocumentsEntity.class));
    }

    @Test
    void testSaveLoanRequestDocument_EmptyFile() {
        // Configurar el archivo simulado para estar vacío
        when(file.isEmpty()).thenReturn(true);

        // Llamar al método que se va a probar
        boolean result = documentsService.saveLoanRequestDocument(file, loanRequestId, userId, fileType);

        // Verificar que el resultado es falso
        assertThat(result).isFalse();

        // Verificar que no se intenta guardar nada en el repositorio
        verify(documentsRepository, never()).save(any(DocumentsEntity.class));
    }

    @Test
    void testSaveLoanRequestDocument_Exception() throws IOException {
        // Configurar el archivo simulado para lanzar una IOException
        when(file.getBytes()).thenThrow(new IOException("Error al leer el archivo"));

        // Llamar al método que se va a probar
        boolean result = documentsService.saveLoanRequestDocument(file, loanRequestId, userId, fileType);

        // Verificar que el resultado es falso
        assertThat(result).isFalse();

        // Verificar que no se intenta guardar nada en el repositorio
        verify(documentsRepository, never()).save(any(DocumentsEntity.class));
    }

    @Test
    void testGetDocumentsByLoanRequestId_Success() {
        // Crear documentos de prueba
        DocumentsEntity document1 = new DocumentsEntity();
        document1.setDocumentId(1L);
        document1.setLoanRequestId(loanRequestId);
        document1.setUserId(userId);
        document1.setDocument(documentContent);
        document1.setIsUploaded(true);
        document1.setType(fileType);

        DocumentsEntity document2 = new DocumentsEntity();
        document2.setDocumentId(2L);
        document2.setLoanRequestId(loanRequestId);
        document2.setUserId(userId);
        document2.setDocument(documentContent);
        document2.setIsUploaded(true);
        document2.setType(fileType);

        List<DocumentsEntity> expectedDocuments = Arrays.asList(document1, document2);

        // Configurar el mock para devolver la lista de documentos
        when(documentsRepository.findByLoanRequestId(loanRequestId)).thenReturn(expectedDocuments);

        // Llamar al método que se va a probar
        List<DocumentsEntity> actualDocuments = documentsService.getDocumentsByLoanRequestId(loanRequestId);

        // Verificar que se devuelve la lista esperada
        assertThat(actualDocuments).isEqualTo(expectedDocuments);

        // Verificar que el método findByLoanRequestId se llamó una vez con el ID correcto
        verify(documentsRepository, times(1)).findByLoanRequestId(loanRequestId);
    }



}
