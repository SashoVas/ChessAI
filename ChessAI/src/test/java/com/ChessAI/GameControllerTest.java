package com.ChessAI;

import com.ChessAI.dto.CreateGameDTO;
import com.ChessAI.dto.GameResultDTO;
import com.ChessAI.dto.UserDTO;
import com.ChessAI.exceptions.InvalidActionException.UnauthorizedGameAccessException;
import com.ChessAI.models.Game;
import com.ChessAI.models.GameStatus;
import com.ChessAI.models.GameType;
import com.ChessAI.repos.GameRepository;
import com.ChessAI.services.GameService;
import com.ChessAI.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "user1", password = "SecurePass23", roles = {"USER"})
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void clearDb() {
        gameRepository.deleteAll();
    }

    @Test
    void testCreateGame() throws Exception {
        assertThat(gameRepository.findAll()).hasSize(0);
        userService.register(new UserDTO("user1", "SecurePass23", "emailemail@abv.bg"));

        String jsonPayload = """
        {
            "gameType": "MULTIPLAYER",
            "gameTimeSeconds": 60
        }
        """;
        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated());

        List<Game> games = gameRepository.findAll();
        assertThat(games).hasSize(1);
        assertThat(games.get(0).getGameType()).isEqualTo(GameType.MULTIPLAYER);
        assertThat(games.get(0).getGameTimeSeconds()).isEqualTo(60);
        assertThat(games.get(0).getGameStatus()).isEqualTo(GameStatus.NOT_STARTED);
    }

    @Test
    void testGetFreeRooms() throws Exception {
        mockMvc.perform(get("/games?free=true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());


        UserDetails mockUser = User.withUsername("user1").password("SecurePass23").roles("USER").build();
        userService.register(new UserDTO("user1", "SecurePass23", "emailemail@abv.bg"));
        gameService.createGame(new CreateGameDTO(GameType.MULTIPLAYER, 60), mockUser);
        gameService.createGame(new CreateGameDTO(GameType.BOT, 60), mockUser);

        mockMvc.perform(get("/games?free=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "user2", password = "SecurePass23", roles = {"USER"})
    void joinRoomTest() throws Exception {
        UserDetails mockUser = User.withUsername("user1").password("SecurePass23").roles("USER").build();
        userService.register(new UserDTO("user1", "SecurePass23", "emailemail@abv.bg"));
        GameResultDTO game = gameService.createGame(new CreateGameDTO(GameType.MULTIPLAYER, 60), mockUser);

        userService.register(new UserDTO("user2", "Pass1234", "myemail@gmail.com"));
        String roomId = String.valueOf(game.getGameId());

        mockMvc.perform(post("/games/42069/join")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertNull(game.getUser2Username());

        assertThrows(UnauthorizedGameAccessException.class, () -> {
            gameService.joinRoom(roomId, "user1");
        });

        assertNull(game.getUser2Username());

        mockMvc.perform(post("/games/" + roomId + "/join")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
