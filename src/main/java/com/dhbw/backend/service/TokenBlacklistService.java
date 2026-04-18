package com.dhbw.backend.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory Token-Blacklist für Logout.
 * In Produktion sollte dies z.B. über Redis gelöst werden.
 */
@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
