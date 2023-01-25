package com.carp.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carp.forum.entities.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo,Long>{

	boolean existsByFileName(String fileName);
}
