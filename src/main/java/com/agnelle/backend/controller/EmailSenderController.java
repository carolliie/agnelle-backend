package com.agnelle.backend.controller;

import com.agnelle.backend.entity.AuthResetPasswordDTO;
import com.agnelle.backend.entity.CodeGenerator;
import com.agnelle.backend.entity.User;
import com.agnelle.backend.repository.UserRepository;
import com.agnelle.backend.security.TokenResponse;
import com.agnelle.backend.security.TokenService;
import com.agnelle.backend.service.EmailSenderService;
import com.agnelle.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailSenderController {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    private static final Map<String, Long> emailVerificationCodes = new HashMap<>();
    private static final long CODE_EXPIRATION_TIME = 5 * 60 * 1000;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody String email) {
        try {
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
            }

            String mailTo = user.getEmail();
            String subject = "Olá! Veja seu código para confirmação de email";
            String code = generateCode();
            String body = "Insira este código para confirmar seu email e mudar a senha:\n" + code;

            emailSenderService.sendEmail(mailTo, subject, body);

            emailVerificationCodes.put(code, System.currentTimeMillis());
            return ResponseEntity.ok("Email sent successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody String code) {
        Long timestamp = emailVerificationCodes.get(code);
        if (timestamp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido.");
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - timestamp > CODE_EXPIRATION_TIME) {
            emailVerificationCodes.remove(code);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O código expirou.");
        }

        return ResponseEntity.ok("Código válido.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid AuthResetPasswordDTO authenticationDTO) {
        try {
            Long timestamp = emailVerificationCodes.get(authenticationDTO.code());
            if (timestamp == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido.");
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > CODE_EXPIRATION_TIME) {
                emailVerificationCodes.remove(authenticationDTO.code());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O código expirou.");
            }

            var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.code());
            var auth = authManager.authenticate(usernamePassword);

            TokenResponse tokenResponse = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.status(HttpStatus.OK).body(tokenResponse.getToken());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao tentar redefinir a senha: " + e.getMessage());
        }
    }


    @PatchMapping("/edit-password/{email}")
    public ResponseEntity<?> editUserPassword(@PathVariable String email, @RequestBody User user) {
        try {
            User editUser = userService.editRegisteredUserPassword(email, user);
            return ResponseEntity.ok("User edited successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String generateCode() {
        List<Integer> codeNumbers = CodeGenerator.generateCode();
        StringBuilder code = new StringBuilder();
        for (Integer num : codeNumbers) {
            code.append(num);
        }
        return code.toString();
    }
}