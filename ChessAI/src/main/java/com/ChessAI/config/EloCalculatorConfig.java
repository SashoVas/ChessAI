package com.ChessAI.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elo")
public class EloCalculatorConfig {
    //FIDE ELO rating system constants
    private Integer minimumGamesForElo;
    private Integer minGameKFactor;
    private Integer provisionalScalingConstant;
    private Integer maxProvisionalElo;
    private Integer minProvisionalElo;
    private Integer bigKFactor;
    private Integer midKFactor;
    private Integer smallKFactor;

    // Getters and setters
    public Integer getMinimumGamesForElo() {
        return minimumGamesForElo;
    }

    public void setMinimumGamesForElo(Integer minimumGamesForElo) {
        this.minimumGamesForElo = minimumGamesForElo;
    }

    public Integer getMinGameKFactor() {
        return minGameKFactor;
    }

    public void setMinGameKFactor(Integer minGameKFactor) {
        this.minGameKFactor = minGameKFactor;
    }

    public Integer getProvisionalScalingConstant() {
        return provisionalScalingConstant;
    }

    public void setProvisionalScalingConstant(Integer provisionalScalingConstant) {
        this.provisionalScalingConstant = provisionalScalingConstant;
    }

    public Integer getMaxProvisionalElo() {
        return maxProvisionalElo;
    }

    public void setMaxProvisionalElo(Integer maxProvisionalElo) {
        this.maxProvisionalElo = maxProvisionalElo;
    }

    public Integer getMinProvisionalElo() {
        return minProvisionalElo;
    }

    public void setMinProvisionalElo(Integer minProvisionalElo) {
        this.minProvisionalElo = minProvisionalElo;
    }

    public Integer getBigKFactor() {
        return bigKFactor;
    }

    public void setBigKFactor(Integer bigKFactor) {
        this.bigKFactor = bigKFactor;
    }

    public Integer getMidKFactor() {
        return midKFactor;
    }

    public void setMidKFactor(Integer midKFactor) {
        this.midKFactor = midKFactor;
    }

    public Integer getSmallKFactor() {
        return smallKFactor;
    }

    public void setSmallKFactor(Integer smallKFactor) {
        this.smallKFactor = smallKFactor;
    }
}
