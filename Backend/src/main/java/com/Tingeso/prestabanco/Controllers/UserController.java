    package com.Tingeso.prestabanco.Controllers;


    import com.Tingeso.prestabanco.Services.UserService;
    import com.Tingeso.prestabanco.Entities.UserEntity;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/PrestaBanco/user")
    @CrossOrigin("*")
    public class UserController {
        @Autowired
        UserService userService;


        @GetMapping("/getUser/{userId}")
        public ResponseEntity<UserEntity> getUserById(@PathVariable Long userId) {
            UserEntity user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        }

        @GetMapping("/getUserByRut/{rut}")
        public ResponseEntity<UserEntity> getUserByRut(@PathVariable String rut) {
            UserEntity user = userService.getUserByRut(rut);
            return ResponseEntity.ok(user);
        }

        @PostMapping("/saveUser")
        public ResponseEntity<UserEntity> saveUser(@RequestBody UserEntity user) {
            UserEntity newUser = userService.saveUser(user);
            if(newUser == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(newUser);
        }

        @PostMapping("/login")
        public ResponseEntity<UserEntity> login(@RequestBody UserEntity user) {
            UserEntity userLogged = userService.login(user);
            if(userLogged == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(userLogged);

        }

        @GetMapping("/getRol/{userId}")
        public ResponseEntity<Integer> getRol(@PathVariable Long userId) {
            Integer role = userService.findRolByUserId(userId);
            return ResponseEntity.ok(role);
        }
    }
