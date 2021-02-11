package com.user.auth.utils;

import com.user.auth.model.Token;
import com.user.auth.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ExpiredTokenDeletion {
    @Autowired
    private TokenRepository tokenRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredTokens(){
        logger.info("Cron job is called...");
        List<Token> tokens = tokenRepository.findAllExpired(new Date());
        tokenRepository.deleteAll(tokens);
        logger.info("All expired tokens deleted");
    }
}
