package com.example.model;

import java.util.List;
import lombok.Data;

@Data
public class User {
    private int id;
    private String name;
    private List<Integer> follows;
}