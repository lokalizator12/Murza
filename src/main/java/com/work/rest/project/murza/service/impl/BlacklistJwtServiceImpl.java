package com.work.rest.project.murza.service.impl;

import com.work.rest.project.murza.entity.BlacklistToken;
import com.work.rest.project.murza.repository.BlacklistTokenRepository;
import com.work.rest.project.murza.service.BlacklistJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlacklistJwtServiceImpl implements BlacklistJwtService {
    private final BlacklistTokenRepository blacklistTokenRepository;
    @Override
    public void addTokenToBlacklist(String token) {
        BlacklistToken blacklistedToken = new BlacklistToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60));
        blacklistTokenRepository.save(blacklistedToken);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistTokenRepository.existsByToken(token);
    }
}
