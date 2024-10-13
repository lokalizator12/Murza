package com.work.rest.project.murza.service;

public interface BlacklistJwtService {
     void addTokenToBlacklist(String token);
     boolean isTokenBlacklisted(String token);

}
