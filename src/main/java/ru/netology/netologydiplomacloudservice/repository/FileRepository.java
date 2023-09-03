package ru.netology.netologydiplomacloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.netologydiplomacloudservice.entity.File;

import java.util.Optional;

@Repository
@Transactional
public interface FileRepository extends JpaRepository<File, String> {

    Optional<File> findByFilename(String filename);
}
