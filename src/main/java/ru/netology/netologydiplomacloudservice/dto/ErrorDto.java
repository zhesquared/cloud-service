package ru.netology.netologydiplomacloudservice.dto;

import lombok.Data;
import ru.netology.netologydiplomacloudservice.utils.IdGenerator;

@Data
public class ErrorDto {

    private Integer id;
    private String message;

    public ErrorDto(String message) {
        this.id = IdGenerator.generateId();
        this.message = message;
    }
}
