package jpa.sideStudy.repository.file;

import jpa.sideStudy.domain.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity,Long> {
}
