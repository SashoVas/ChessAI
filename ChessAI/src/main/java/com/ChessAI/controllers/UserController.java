package com.ChessAI.controllers;

import com.ChessAI.config.EloCalculatorConfig;
import com.ChessAI.dto.LeaderboardUserDTO;
import com.ChessAI.dto.UserDTO;
import com.ChessAI.dto.UserLoginDTO;
import com.ChessAI.dto.UserStatisticsDTO;
import com.ChessAI.exceptions.UserException.UserAlreadyExistsException;
import com.ChessAI.exceptions.UserException.UserNotFoundException;
import com.ChessAI.services.UserService;
import com.ChessAI.services.UserStatisticsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserStatisticsService userStatisticsService;

    @Autowired
    private EloCalculatorConfig eloCalculatorConfig;

    //tested by postman collection
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO userDTO) {
        if (userService.userExists(userDTO)) {
            throw new UserAlreadyExistsException();
        }
        UserDTO registeredUser = userService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    //tested by postman collection
    @PostMapping("/login")
    public ResponseEntity<UserLoginDTO> login(@RequestBody @Valid UserDTO user) {
        if (!userService.userExists(user)) {
            throw new UserNotFoundException();
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.verify(user));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardUserDTO>> getLeaderboard(@RequestParam(defaultValue = "5") int limit) {
        List<LeaderboardUserDTO> topUsers = userStatisticsService.getTopUsers(limit);
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserStatisticsDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserStatisticsDTO profile = userService.getUserStatistics(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/elo-config")
    public ResponseEntity<Map<String, Integer>> getEloConfig() {
        Map<String, Integer> config = Map.of(
            "minimumGamesForElo", eloCalculatorConfig.getMinimumGamesForElo()
        );
        return ResponseEntity.ok(config);
    }
}
