package ru.netology.netologydiplomacloudservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginDto(@JsonProperty("auth-token") String authToken) {
}
