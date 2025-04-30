package com.ChessAI.services;

import com.ChessAI.dto.UserDTO;
import com.ChessAI.exceptions.AuthenticationFailedException;
import com.ChessAI.models.User;
import com.ChessAI.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public UserDTO register(UserDTO userDTO) {
        User user = new User(userDTO);
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user = repo.save(user);
        return new UserDTO(user);
    }

    public String verify(UserDTO userDTO) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userDTO.getUsername());
        } else {
            throw new AuthenticationFailedException("User Authentication Failed");
        }
    }
}
