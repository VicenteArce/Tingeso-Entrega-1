package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.DocumentsEntity;
import com.Tingeso.prestabanco.Repositories.DocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentsService {
    @Autowired
    DocumentsRepository documentsRepository;

    public boolean saveLoanRequestDocument(MultipartFile file, Long loanRequestId, Long userId, String fileType) {
        if(file.isEmpty()){
            return false;
        }
        try{
            // Convert the MultipartFile to a byte array
            byte[] document = file.getBytes();

            // Create a new LoanRequestDocumentsEntity
            DocumentsEntity loanRequestDocument = new DocumentsEntity();
            loanRequestDocument.setUserId(userId);
            loanRequestDocument.setLoanRequestId(loanRequestId);
            loanRequestDocument.setDocument(document);
            loanRequestDocument.setType(fileType);
            loanRequestDocument.setIsUploaded(true);

            // Save the LoanRequestDocumentsEntity in the repository -> Database
            documentsRepository.save(loanRequestDocument);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DocumentsEntity> getDocumentsByLoanRequestId(Long loanRequestId) {
        return documentsRepository.findByLoanRequestId(loanRequestId);
    }


}
