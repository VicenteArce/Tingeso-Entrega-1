package com.Tingeso.prestabanco.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long documentId;
    private Long loanRequestId;     // Foreign key from LoanRequestEntity
    private Long userId;            // Foreign key from UserEntity

    @Lob
    private byte[] document;        // Document in bytes
    private Boolean isUploaded;     // True if the document is uploaded
    private String type;            // Document type


}
