package com.example.model;

import java.util.List;
import lombok.Data;

@Data
public class ResultOutput {
    private String regNo;
    private List<Integer> outcome;
}