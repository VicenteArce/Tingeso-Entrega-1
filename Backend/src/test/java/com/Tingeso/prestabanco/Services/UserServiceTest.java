package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.UserEntity;
import com.Tingeso.prestabanco.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setRut("21538935-9");
        user.setName("Vicente");
        user.setLastName("Arce");
        user.setEmail("vicente.arce@example.com");
        user.setPassword("securePass123");
        user.setMonthlyIncome(1500000L);
        user.setRol(1);  // Cliente
        user.setBirthDate("30-03-2004");
    }

    @Test
    void whenSaveUserWithValidData_thenUserIsSaved() {
        when(userRepository.findByRut(user.getRut())).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        UserEntity savedUser = userService.saveUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getRut()).isEqualTo("21538935-9");
        verify(userRepository).save(user);
    }

    @Test
    void whenSaveUserWithInvalidRut_thenUserIsNotSaved() {
        user.setRut("12345678-5");  // RUT inválido
        when(userRepository.findByRut(user.getRut())).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        UserEntity savedUser = userService.saveUser(user);

        assertThat(savedUser).isNull();  // El usuario no debería guardarse
    }

    @Test
    void whenSaveUserWithInvalidEmail_thenUserIsNotSaved() {
        user.setEmail("invalid-email");  // Email inválido
        when(userRepository.findByRut(user.getRut())).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        UserEntity savedUser = userService.saveUser(user);

        assertThat(savedUser).isNull();  // El usuario no debería guardarse
    }

    @Test
    void whenSaveUserWithDuplicateRut_thenUserIsNotSaved() {
        when(userRepository.findByRut(user.getRut())).thenReturn(user);  // RUT ya existe

        UserEntity savedUser = userService.saveUser(user);

        assertThat(savedUser).isNull();  // El usuario no debería guardarse
    }

    @Test
    void whenSaveUserWithDuplicateEmail_thenUserIsNotSaved() {
        when(userRepository.findByRut(user.getRut())).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);  // Email ya existe

        UserEntity savedUser = userService.saveUser(user);

        assertThat(savedUser).isNull();  // El usuario no debería guardarse
    }

    @Test
    void whenGetUserById_thenUserIsReturned() {
        Long userId = 1L;
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserEntity foundUser = userService.getUserById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(userId);
    }

    @Test
    void whenGetUserByRut_thenUserIsReturned() {
        when(userRepository.findByRut(user.getRut())).thenReturn(user);

        UserEntity foundUser = userService.getUserByRut(user.getRut());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getRut()).isEqualTo("21538935-9");
    }

    @Test
    void whenLoginWithValidCredentials_thenUserIsReturned() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        UserEntity loggedInUser = userService.login(user);

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(loggedInUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void whenLoginWithInvalidCredentials_thenReturnNull() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // Cambiamos la contraseña del usuario ingresado para que no coincida
        UserEntity userWithInvalidPassword = new UserEntity();
        userWithInvalidPassword.setEmail(user.getEmail());
        userWithInvalidPassword.setPassword("incorrectPassword");  // Contraseña incorrecta

        UserEntity loggedInUser = userService.login(userWithInvalidPassword);

        assertThat(loggedInUser).isNull();  // La autenticación debería fallar
    }

    @Test
    void whenFindRolByUserId_thenRolIsReturned() {
        Long userId = 1L;
        user.setUserId(userId);
        when(userRepository.findRolByUserId(userId)).thenReturn(user.getRol());

        Integer role = userService.findRolByUserId(userId);

        assertThat(role).isEqualTo(user.getRol());
    }

    @Test
    void whenSaveUserWithInvalidRutCharacters_thenReturnNull() {
        // Caso de prueba donde el RUT contiene caracteres no válidos
        user.setRut("21a538935-9");  // RUT inválido con caracteres alfabéticos

        when(userRepository.findByRut(user.getRut())).thenReturn(null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        UserEntity savedUser = userService.saveUser(user);

        // El método saveUser debe retornar null ya que el RUT no es válido
        assertThat(savedUser).isNull();
    }
}
