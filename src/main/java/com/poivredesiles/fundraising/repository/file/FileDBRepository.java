package com.poivredesiles.fundraising.repository.file;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poivredesiles.fundraising.model.file.FileDB;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

	Optional<FileDB> findTop1ByDescriptionOrderByUpdatedTimeDesc(String description);
}
