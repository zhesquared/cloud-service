package ru.netology.netologydiplomacloudservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiplomacloudservice.dto.FileDto;
import ru.netology.netologydiplomacloudservice.dto.FileInfoDto;
import ru.netology.netologydiplomacloudservice.entity.File;
import ru.netology.netologydiplomacloudservice.exception.FileProcessingException;
import ru.netology.netologydiplomacloudservice.exception.IncorrectFileDataException;
import ru.netology.netologydiplomacloudservice.mapper.FileInfoMapper;
import ru.netology.netologydiplomacloudservice.repository.FileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CloudService {

    private final FileRepository fileRepository;
    private final FileManager fileManager;
    private final FileInfoMapper mapper;

    public void saveFile(String filename, MultipartFile file) {
        try {
            log.info("Проверка существования файла: {}", filename);
            if (fileRepository.findByFilename(filename).isPresent()) {
                throw new IncorrectFileDataException(String.format("Файл с именем %s уже существует", filename));
            }

            if (file == null) {
                throw new IncorrectFileDataException("Выберите файл для загрузки");
            }

            File uploadedFile = createFileInfo(filename, file);

            log.info("Сохраняем данные файла {} в базу данных", filename);
            fileRepository.save(uploadedFile);
            log.info("Информация о файле {} сохранена", filename);

            log.info("Загружаем файл {} в облако", filename);
            try {
                fileManager.uploadFile(file.getBytes(), uploadedFile.getHash(), filename);
            } catch (IOException ex) {
                fileRepository.delete(uploadedFile);
                log.error("Очищаем данные о файле {} из базы данных", filename);
                throw new FileProcessingException(ex.getMessage());
            }
            log.info("Файл {} успешно загружен", filename);

        } catch (IOException ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    public void deleteFile(String filename) {
        File fileToDelete = getExistingFile(filename);

        try {
            log.info("Удаляем файл {} из облака", filename);
            fileManager.deleteFile(fileToDelete.getHash());
            log.info("Файл {} удален с облака", filename);

            log.info("Удаляем информацию о файле {} из базы данных", filename);
            fileRepository.delete(fileToDelete);
            log.info("Информация о файле {} удалена из базы данных", filename);

        } catch (Exception ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    public FileDto downloadFile(String filename) {
        File file = getExistingFile(filename);

        try {
            log.info("Скачивание файла {} из облака", filename);
            String hash = file.getHash();
            Resource fileContent = fileManager.downloadFile(hash);
            log.info("Файл {} скачан из облака", filename);

            return FileDto.builder()
                    .hash(hash)
                    .file(fileContent.toString())
                    .build();

        } catch (Exception ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    public void editFilename(String filename, String newName) {
        File file = getExistingFile(filename);
        file.setFilename(newName);
        fileRepository.save(file);
    }

    public List<FileInfoDto> getFiles(int limit) {
        log.info("Получение списка файлов");
        return fileRepository.findAll(Pageable.ofSize(limit))
                .map(mapper::fileToFileInfoDto)
                .toList();
    }

    private File createFileInfo(String filename, MultipartFile file) throws IOException {
        LocalDateTime createdTime = LocalDateTime.now();

        String hash = UUID.nameUUIDFromBytes(
                ArrayUtils.addAll(file.getBytes(), createdTime.toString().getBytes())).toString();

        return File.builder()
                .hash(hash)
                .filename(filename)
                .size(file.getSize())
                .createdTime(createdTime)
                .build();
    }

    private File getExistingFile(String filename) {
        return fileRepository.findByFilename(filename).orElseThrow(
                () -> new IncorrectFileDataException(String.format("Файла %s не существует", filename)));
    }
}
