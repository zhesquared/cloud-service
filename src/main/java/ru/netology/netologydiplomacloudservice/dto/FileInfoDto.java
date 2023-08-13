package ru.netology.netologydiplomacloudservice.dto;

import lombok.Data;

//@Data
//public class FileInfoDto {
//
//    private String filename;
//    private String size;
//}

public record FileInfoDto(String filename, String size) {
}
