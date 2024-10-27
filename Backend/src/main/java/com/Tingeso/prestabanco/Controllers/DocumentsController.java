package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.DocumentsEntity;
import com.Tingeso.prestabanco.Services.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/PrestaBanco/loanRequestDocument")
@CrossOrigin("*")
public class DocumentsController {
    @Autowired
    DocumentsService loanRequestDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<Boolean> saveLoanRequestDocument(@RequestParam("file") MultipartFile file, @RequestParam("loanRequestId") Long loanRequestId, @RequestParam("userId") Long userId, @RequestParam("fileType") String fileType) {
        var isSaved = loanRequestDocumentService.saveLoanRequestDocument(file, loanRequestId, userId, fileType);
        return ResponseEntity.ok(isSaved);
    }

    @GetMapping("/loanRequest/{loanRequestId}")
    public List<DocumentsEntity> getDocumentsByLoanRequestId(@PathVariable Long loanRequestId) {
        return loanRequestDocumentService.getDocumentsByLoanRequestId(loanRequestId);
    }
}
