package com.ChessAI.dto;

import com.ChessAI.models.User;

public class LeaderboardUserDTO {
    private String username;
    private Integer eloRating;
    private Boolean isEloProvisional;

    public LeaderboardUserDTO() {}

    public LeaderboardUserDTO(String username, Integer eloRating, Boolean isEloProvisional) {
        this.username = username;
        this.eloRating = eloRating;
        this.isEloProvisional = isEloProvisional;
    }

    public static LeaderboardUserDTO fromUser(User user) {
        return new LeaderboardUserDTO(
            user.getUsername(),
            user.getEloRating(),
            user.IsEloProvisional()
        );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEloRating() {
        return eloRating;
    }

    public void setEloRating(Integer eloRating) {
        this.eloRating = eloRating;
    }

    public Boolean getIsEloProvisional() {
        return isEloProvisional;
    }

    public void setIsEloProvisional(Boolean isEloProvisional) {
        this.isEloProvisional = isEloProvisional;
    }
} 