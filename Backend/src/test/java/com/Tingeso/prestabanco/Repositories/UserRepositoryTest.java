package com.Tingeso.prestabanco.Repositories;

import com.Tingeso.prestabanco.Entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindRolByUserId_thenReturnRol() {
        // Arrange
        UserEntity user = new UserEntity(null, "12345678-9", "Juan", "Perez", "juan@example.com", "password123", 1000000L, 1, "01-01-1990");
        user = entityManager.persistAndFlush(user);

        // Act
        Integer foundRol = userRepository.findRolByUserId(user.getUserId());

        // Assert
        assertNotNull(foundRol);
        assertEquals(user.getRol(), foundRol);
    }

    @Test
    public void whenFindByRut_thenReturnUserEntity() {
        // Arrange
        UserEntity user = new UserEntity(null, "87654321-0", "Ana", "Gomez", "ana@example.com", "password456", 1200000L, 1, "02-02-1985");
        entityManager.persistAndFlush(user);

        // Act
        UserEntity foundUser = userRepository.findByRut("87654321-0");

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getRut(), foundUser.getRut());
        assertEquals(user.getName(), foundUser.getName());
    }

    @Test
    public void whenFindByEmail_thenReturnUserEntity() {
        // Arrange
        UserEntity user = new UserEntity(null, "13579246-8", "Carlos", "Lopez", "carlos@example.com", "password789", 900000L, 0, "03-03-1992");
        entityManager.persistAndFlush(user);

        // Act
        UserEntity foundUser = userRepository.findByEmail("carlos@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getName(), foundUser.getName());
    }
}
