package com.finconnect.auth_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.finconnect.auth_service.dto.ResetPasswordRequest;
import com.finconnect.auth_service.dto.SalvarEstoque;
import com.finconnect.auth_service.dto.SignInRequest;
import com.finconnect.auth_service.dto.SignInResponse;
import com.finconnect.auth_service.dto.SignUpRequest;
import com.finconnect.auth_service.entity.Users;
import com.finconnect.auth_service.exception_handler.exceptions.PetNameIsIncorrectException;
import com.finconnect.auth_service.exception_handler.exceptions.UserAlredyExistsException;
import com.finconnect.auth_service.repository.UsersRepository;
import com.finconnect.auth_service.service.ProductsService;
import com.finconnect.auth_service.util.JwtUtil;
import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ProductsService productsService;

    @InjectMocks
    private AuthController authController;

    @Test
    void authenticateUser_validCredentials_returnsTokenAndEstoque() throws Exception {
        SignInRequest request = signInRequest("usuario@teste.com", "Senha123");
        UUID estoqueId = UUID.randomUUID();
        Authentication authentication = authenticationForUsername(request.username());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtUtil.generateToken(request.username())).thenReturn("token-simulado");
        when(usersRepository.findEstoqueByEmail(request.username())).thenReturn(Optional.of(estoqueId));

        ResponseEntity<SignInResponse> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("token-simulado", response.getBody().jwt());
        assertEquals(estoqueId, response.getBody().estoque());
    }

    @Test
    void authenticateUser_emailNotRegistered_throwsBadCredentialsException() {
        SignInRequest request = signInRequest("inexistente@teste.com", "Senha123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.authenticateUser(request));
    }

    @Test
    void authenticateUser_missingEstoque_throwsBadRequestException() {
        SignInRequest request = signInRequest("semestoque@teste.com", "Senha123");
        Authentication authentication = authenticationForUsername(request.username());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtUtil.generateToken(request.username())).thenReturn("token-simulado");
        when(usersRepository.findEstoqueByEmail(request.username())).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> authController.authenticateUser(request)
        );

        assertEquals("Usuário não cadastrado", exception.getMessage());
    }

    @Test
    void authenticateUser_wrongPassword_throwsBadCredentialsException() {
        SignInRequest request = signInRequest("teste@teste.com", "Senhaincorreta");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.authenticateUser(request));
    }

    @Test
    void authenticateUser_emptyEmail_throwsBadCredentialsException() {
        SignInRequest request = signInRequest("", "Senha123");

        when(authenticationManager.authenticate(argThat(authentication ->
            authentication instanceof UsernamePasswordAuthenticationToken token
                && "".equals(token.getPrincipal())
        ))).thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class,
            () -> authController.authenticateUser(request)
        );

        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    void authenticateUser_emptyPassword_throwsBadCredentialsException() {
        SignInRequest request = signInRequest("teste@gmail.com", "");

        when(authenticationManager.authenticate(argThat(authentication ->
            authentication instanceof UsernamePasswordAuthenticationToken token
                && "".equals(token.getCredentials())
        ))).thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class,
            () -> authController.authenticateUser(request)
        );

        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    void registerUser_success_returnsEstoqueMessageAndSavesUser() throws Exception {
        SignUpRequest request = signUpRequest("Joao", "Silva", "joao@teste.com", "Senha123", "Rex");
        UUID estoqueId = stubRegisterUserPrereqs(request, "senha-criptografada");

        ResponseEntity<String> response = authController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Estoque criado: " + estoqueId, response.getBody());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void registerUser_duplicateEmail_throwsUserAlreadyExists() {
        SignUpRequest request = signUpRequest("Joao", "Silva", "existente@teste.com", "Senha123", "Rex");
        Users existingUser = new Users();
        existingUser.setEmail(request.email());

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        UserAlredyExistsException exception = assertThrows(
            UserAlredyExistsException.class,
            () -> authController.registerUser(request)
        );

        assertEquals("Usuário já cadastrado", exception.getMessage());
    }

    @Test
    void registerUser_emptyFirstName_throwsConstraintViolation() {
        SignUpRequest request = signUpRequest("", "Silva", "valido@teste.com", "Senha123", "Rex");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.registerUser(request));
    }

    @Test
    void registerUser_emptyLastName_throwsConstraintViolation() {
        SignUpRequest request = signUpRequest("Nome", "", "valido@teste.com", "Senha123", "Rex");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.registerUser(request));
    }

    @Test
    void registerUser_emptyPassword_throwsConstraintViolation() {
        SignUpRequest request = signUpRequest("Nome", "Sobrenome", "valido@teste.com", "", "Rex");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.registerUser(request));
    }

    @Test
    void registerUser_emptyFirstPetName_throwsConstraintViolation() {
        SignUpRequest request = signUpRequest("Nome", "Sobrenome", "valido@teste.com", "senha123", "");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.registerUser(request));
    }

    @Test
    void registerUser_emptyEmail_throwsConstraintViolation() {
        SignUpRequest request = signUpRequest("Nome", "Sobrenome", "", "senha123", "Rex");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.registerUser(request));
    }

    @Test
    void registerUser_invalidEmail_throwsConstraintViolation() {
        SignUpRequest request = signUpRequest("Nome", "Sobrenome", "email-sem-arroba", "senha123", "Rex");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.registerUser(request));
    }

    @Test
    void registerUser_adminIsNull() throws Exception {
        SignUpRequest request = signUpRequest("Nome", "Sobrenome", "teste@gmail.com", "senha123", "Rex");
        stubRegisterUserPrereqs(request, "senha-criptografada");

        ResponseEntity<String> response = authController.registerUser(request);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(captor.getValue().getAdmin());
    }

    @Test
    void resetPassword_success_updatesPassword() throws Exception {
        ResetPasswordRequest request = resetPasswordRequest("teste@gmail.com", "rex", "novasenha");
        Users user = userWithPetName("rex");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(encoder.encode(request.password())).thenReturn("nova_senha_hash");

        ResponseEntity<String> response = authController.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
        verify(usersRepository).save(user);
    }

    @Test
    void resetPassword_invalidEmail_throwsConstraintViolation() {
        ResetPasswordRequest request = resetPasswordRequest("email-sem-arroba", "rex", "novasenha");

        when(usersRepository.findByEmail(request.email())).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.resetPassword(request));
    }

    @Test
    void resetPassword_emptyEmail_throwsConstraintViolation() {
        ResetPasswordRequest request = resetPasswordRequest("", "rex", "novasenha");

        when(usersRepository.findByEmail(request.email())).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.resetPassword(request));
    }

    @Test
    void resetPassword_emptyPassword_throwsConstraintViolation() {
        ResetPasswordRequest request = resetPasswordRequest("teste@gmail.com", "rex", "");
        Users user = userWithPetName("rex");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(encoder.encode(request.password())).thenReturn("nova_senha_hash");
        when(usersRepository.save(any(Users.class))).thenThrow(constraintViolationException());

        assertThrows(ConstraintViolationException.class, () -> authController.resetPassword(request));
    }

    @Test
    void resetPassword_emptyPetName_throwsPetNameIsIncorrect() {
        ResetPasswordRequest request = resetPasswordRequest("teste@gmail.com", "", "novasenha");
        Users user = userWithPetName("rex");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        assertThrows(PetNameIsIncorrectException.class, () -> authController.resetPassword(request));
    }

    @Test
    void resetPassword_emailNotRegistered_throwsException() {
        ResetPasswordRequest request = resetPasswordRequest("nao-cadastrado@gmail.com", "rex", "novasenha");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> authController.resetPassword(request));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void resetPassword_wrongPetName_throwsPetNameIsIncorrect() {
        ResetPasswordRequest request = resetPasswordRequest("cadastrado@gmail.com", "nome-incorreto", "novasenha");
        Users user = userWithPetName("rex");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        assertThrows(PetNameIsIncorrectException.class, () -> authController.resetPassword(request));
    }

    @Test
    void resetPassword_caseInsensitivePetName_succeeds() throws Exception {
        ResetPasswordRequest request = resetPasswordRequest("cadastrado@gmail.com", "rex", "novasenha");
        Users user = userWithPetName("REX");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(encoder.encode(request.password())).thenReturn("nova_senha_hash");

        ResponseEntity<String> response = authController.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
        verify(usersRepository).save(user);
    }

    @Test
    void resetPassword_encryptsPassword() throws Exception {
        ResetPasswordRequest request = resetPasswordRequest("cadastrado@gmail.com", "rex", "novasenha");
        Users user = userWithPetName("rex");

        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(encoder.encode(request.password())).thenReturn("nova_senha_hash");

        ResponseEntity<String> response = authController.resetPassword(request);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
        assertEquals("nova_senha_hash", captor.getValue().getPassword());
    }

    @Test
    void registerUser_encryptsPassword() throws Exception {
        SignUpRequest request = signUpRequest("Joao", "Silva", "joao@teste.com", "senha_hash", "Rex");
        UUID estoqueId = stubRegisterUserPrereqs(request, "senha_hash");

        ResponseEntity<String> response = authController.registerUser(request);

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(captor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Estoque criado: " + estoqueId, response.getBody());
        assertEquals("senha_hash", captor.getValue().getPassword());
    }

    private Authentication authenticationForUsername(String username) {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UserDetails userDetails = org.mockito.Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        return authentication;
    }

    private SignInRequest signInRequest(String username, String password) {
        return new SignInRequest(username, password);
    }

    private SignUpRequest signUpRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String firstPetName
    ) {
        return new SignUpRequest(firstName, lastName, email, password, firstPetName);
    }

    private ResetPasswordRequest resetPasswordRequest(String email, String firstPetName, String password) {
        return new ResetPasswordRequest(email, password, firstPetName);
    }

    private Users userWithPetName(String petName) {
        Users user = new Users();
        user.setFirstPetName(petName);
        return user;
    }

    private UUID stubRegisterUserPrereqs(SignUpRequest request, String encodedPassword) {
        UUID estoqueId = UUID.randomUUID();
        when(usersRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(encoder.encode(request.password())).thenReturn(encodedPassword);
        when(productsService.salvarEstoque(any(SalvarEstoque.class))).thenReturn(estoqueId);
        return estoqueId;
    }

    private ConstraintViolationException constraintViolationException() {
        return new ConstraintViolationException("validation error", Collections.emptySet());
    }
}
