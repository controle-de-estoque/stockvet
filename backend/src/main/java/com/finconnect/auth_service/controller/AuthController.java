package com.finconnect.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductsService productsService;
    
    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> authenticateUser(@RequestBody SignInRequest request) throws BadRequestException {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok().body(new SignInResponse(jwt, this.usersRepository.findEstoqueByEmail(request.username()).orElseThrow(() -> new BadRequestException("Usuário não cadastrado"))));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignUpRequest request) throws BadRequestException {
        if(usersRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlredyExistsException("Usuário já cadastrado");
        }

        Users newUser = new Users();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(encoder.encode(request.password()));
        newUser.setFirstPetName(request.firstPetName());
        newUser.setEstoque(productsService.salvarEstoque(new SalvarEstoque("StockVet")));

        usersRepository.save(newUser);

        return ResponseEntity.ok("Estoque criado: " + newUser.getEstoque().toString());
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) throws Exception {
        Users user = usersRepository.findByEmail(request.email()).orElseThrow(() -> new Exception("User not found"));
        
        if(!user.getFirstPetName().equalsIgnoreCase(request.firstPetName())) {
            throw new PetNameIsIncorrectException("Nome do pet está incorreto");
        }

        user.setPassword(encoder.encode(request.password()));

        this.usersRepository.save(user);
        return ResponseEntity.ok("Password changed successfully");
    }
}
