package ru.netology.netologydiplomacloudservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class LoginDto {
//
//    @JsonProperty("auth-token")
//    private String authToken;
//}

public record LoginDto(@JsonProperty("auth-token") String authToken) {
}
