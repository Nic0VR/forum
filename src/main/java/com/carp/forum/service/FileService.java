package com.carp.forum.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.FileInfo;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.Thread;
import com.carp.forum.exception.UnsupportedFileTypeException;

public interface FileService {
	

	Resource loadFileAsResource(String fileName) throws FileNotFoundException, MalformedURLException;

	FileInfo saveFile(MultipartFile file, Post post) throws IOException, UnsupportedFileTypeException;
	
	boolean delete(String filename);

	Object saveFile(MultipartFile multipartFile, Thread entityToSave)throws IOException, UnsupportedFileTypeException;

}
