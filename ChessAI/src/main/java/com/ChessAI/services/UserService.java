package com.ChessAI.services;

import com.ChessAI.dto.UserDTO;
import com.ChessAI.dto.UserLoginDTO;
import com.ChessAI.dto.UserStatisticsDTO;
import com.ChessAI.exceptions.AuthenticationFailedException;
import com.ChessAI.exceptions.UserException.UserNotFoundException;
import com.ChessAI.models.User;
import com.ChessAI.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserStatisticsService userStatisticsService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public boolean userExists(UserDTO userDTO) {
        return repo.findByUsername(userDTO.getUsername()).isPresent();
    }

    @Transactional
    public UserDTO register(UserDTO userDTO) {
        User user = new User(userDTO);
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user = repo.save(user);
        return new UserDTO(user);
    }

    public UserLoginDTO verify(UserDTO userDTO) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return new UserLoginDTO(userDTO.getUsername(),jwtService.generateToken(userDTO.getUsername()));
        } else {
            throw new AuthenticationFailedException("User Authentication Failed");
        }
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());
    }

    public UserStatisticsDTO getUserStatistics(String username) {
        return userStatisticsService.calculateUserStatistics(username);
    }
}
