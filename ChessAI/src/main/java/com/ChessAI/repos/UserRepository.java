package com.ChessAI.repos;

import com.ChessAI.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u.elo FROM User u WHERE u.username = :username")
    Integer getEloByUsername(String username);

    //NOTE: Run entityManager.clear(); after @Modifying queries!
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isEloProvisional = :isProvisional WHERE u.username = :username")
    void updateEloIsProvisionalByUsername(Boolean isProvisional, String username);

    //NOTE: Run entityManager.clear(); after @Modifying queries!
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.elo = :elo WHERE u.username = :username")
    void updateEloByUsername(Integer elo, String username);

    @Query("SELECT u FROM User u ORDER BY u.elo DESC LIMIT :limit")
    List<User> findTopUsersByElo(Integer limit);
}
