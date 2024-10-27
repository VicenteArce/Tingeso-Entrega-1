package com.Tingeso.prestabanco.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long userId;

    private String rut;                 // Unique in format XXXXXXXX-X
    private String name;
    private String lastName;
    private String email;               // Unique in format [a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}
    private String password;
    private Long monthlyIncome;         // In CLP
    private Integer rol;                // 0: Executive, 1: Client
    private String birthDate;           // Date of birth in format dd-MM-yyyy

}
