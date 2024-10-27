package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.DocumentsEntity;
import com.Tingeso.prestabanco.Services.DocumentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentsController.class)
public class DocumentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentsService loanRequestDocumentService;

    @Test
    void whenSaveLoanRequestDocument_thenReturnIsSaved() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Dummy Content".getBytes()
        );

        Long loanRequestId = 1L;
        Long userId = 1L;
        String fileType = "incomeProof";  // Document type set to "incomeProof"

        when(loanRequestDocumentService.saveLoanRequestDocument(file, loanRequestId, userId, fileType)).thenReturn(true);

        mockMvc.perform(multipart("/PrestaBanco/loanRequestDocument/upload")
                        .file(file)
                        .param("loanRequestId", String.valueOf(loanRequestId))
                        .param("userId", String.valueOf(userId))
                        .param("fileType", fileType))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void whenGetDocumentsByLoanRequestId_thenReturnDocumentsList() throws Exception {
        Long loanRequestId = 1L;
        DocumentsEntity document = new DocumentsEntity();
        document.setDocumentId(1L);
        document.setLoanRequestId(loanRequestId);
        document.setUserId(1L);
        document.setType("incomeProof");

        when(loanRequestDocumentService.getDocumentsByLoanRequestId(loanRequestId)).thenReturn(List.of(document));

        mockMvc.perform(get("/PrestaBanco/loanRequestDocument/loanRequest/{loanRequestId}", loanRequestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentId").value(1L))
                .andExpect(jsonPath("$[0].loanRequestId").value(loanRequestId))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].type").value("incomeProof"));
    }
}
