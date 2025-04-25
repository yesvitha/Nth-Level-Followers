package com.example.model;

import java.util.List;
import lombok.Data;

@Data
public class NthLevelFollowersInput {
    private int n;
    private int findId;
    private List<User> users;
}