package com.example.service;

import com.example.model.NthLevelFollowersInput;
import com.example.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NthLevelFollowersService {
    private static final Logger log = LoggerFactory.getLogger(NthLevelFollowersService.class);
    
    public List<Integer> findNthLevelFollowers(NthLevelFollowersInput input) {
        int n = input.getN();
        int findId = input.getFindId();
        List<User> users = input.getUsers();
        
        log.info("Finding followers at level {} for user {}", n, findId);
        
        // Create a map of user IDs to their follow lists for easier lookup
        Map<Integer, List<Integer>> followsMap = new HashMap<>();
        for (User user : users) {
            followsMap.put(user.getId(), 
                          user.getFollows() != null ? user.getFollows() : Collections.emptyList());
        }
        
        // Users at current level
        Set<Integer> currentLevel = new HashSet<>();
        currentLevel.add(findId);
        
        log.debug("Level 0: {}", currentLevel);
        
        // Find followers at each level up to n
        for (int level = 1; level <= n; level++) {
            Set<Integer> nextLevel = new HashSet<>();
            
            for (Integer userId : currentLevel) {
                List<Integer> follows = followsMap.getOrDefault(userId, Collections.emptyList());
                nextLevel.addAll(follows);
            }
            
            log.debug("Level {}: {}", level, nextLevel);
            
            // If we've reached the target level, return the followers
            if (level == n) {
                List<Integer> result = new ArrayList<>(nextLevel);
                Collections.sort(result);
                return result;
            }
            
            currentLevel = nextLevel;
        }
        
        // If n is 0 or somehow we didn't return earlier
        return Collections.emptyList();
    }
}