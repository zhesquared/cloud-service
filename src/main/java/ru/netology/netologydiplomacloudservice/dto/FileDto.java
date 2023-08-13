package ru.netology.netologydiplomacloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

//@Data
@Builder
//@AllArgsConstructor
//public class FileDto {
//
//    private String hash;
//    private String file;
//}
public record FileDto(String hash, String file) {
}
