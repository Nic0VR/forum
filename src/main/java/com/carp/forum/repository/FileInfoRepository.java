package com.carp.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carp.forum.entities.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo,Long>{

	boolean existsByFileName(String fileName);
	
	@Query("FROM FileInfo f WHERE f.post.id=:postId")
	List<FileInfo> findFileInfoByPostId(@Param("postId")long postId);
	
	@Query("FROM FileInfo f WHERE f.thread.id=:threadId")
	List<FileInfo> findFileInfoByThreadId(@Param("threadId")long threadId);
	
	@Query(nativeQuery=true,value="DELETE FROM file_info WHERE post_id=:postId")
	void deleteByPostId(@Param("postId")long postId);
	
	@Query(nativeQuery=true,value="DELETE FROM file_info WHERE thread_id=:threadId")
	void deleteByThreadId(@Param("threadId")long threadId);
	

}
