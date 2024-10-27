package com.Tingeso.prestabanco.Controllers;

import com.Tingeso.prestabanco.Entities.UserEntity;
import com.Tingeso.prestabanco.Services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setName("Vicente");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/PrestaBanco/user/getUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.name").value("Vicente"));
    }

    @Test
    void whenGetUserByRut_thenReturnUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setRut("21538935-9");

        when(userService.getUserByRut("21538935-9")).thenReturn(user);

        mockMvc.perform(get("/PrestaBanco/user/getUserByRut/21538935-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("21538935-9"));
    }

    @Test
    void whenSaveUser_thenReturnSavedUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setRut("21538935-9");
        user.setEmail("vicente.arce@example.com");

        when(userService.saveUser(Mockito.any(UserEntity.class))).thenReturn(user);

        String userJson = "{\"rut\":\"21538935-9\",\"email\":\"vicente.arce@example.com\"}";

        mockMvc.perform(post("/PrestaBanco/user/saveUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("21538935-9"))
                .andExpect(jsonPath("$.email").value("vicente.arce@example.com"));
    }

    @Test
    void whenSaveUserWithInvalidData_thenReturnBadRequest() throws Exception {
        when(userService.saveUser(Mockito.any(UserEntity.class))).thenReturn(null);

        String userJson = "{\"rut\":\"invalid-rut\",\"email\":\"invalid@example.com\"}";

        mockMvc.perform(post("/PrestaBanco/user/saveUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenLoginWithValidCredentials_thenReturnUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail("vicente.arce@example.com");
        user.setPassword("securePass123");

        when(userService.login(Mockito.any(UserEntity.class))).thenReturn(user);

        String userJson = "{\"email\":\"vicente.arce@example.com\",\"password\":\"securePass123\"}";

        mockMvc.perform(post("/PrestaBanco/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("vicente.arce@example.com"));
    }

    @Test
    void whenLoginWithInvalidCredentials_thenReturnBadRequest() throws Exception {
        when(userService.login(Mockito.any(UserEntity.class))).thenReturn(null);

        String userJson = "{\"email\":\"invalid@example.com\",\"password\":\"wrongPass\"}";

        mockMvc.perform(post("/PrestaBanco/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetRol_thenReturnUserRole() throws Exception {
        when(userService.findRolByUserId(1L)).thenReturn(1);

        mockMvc.perform(get("/PrestaBanco/user/getRol/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
