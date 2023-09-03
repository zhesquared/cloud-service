package ru.netology.netologydiplomacloudservice.dto;

import lombok.Builder;

@Builder
public record FileDto(String hash, String file) {
}
