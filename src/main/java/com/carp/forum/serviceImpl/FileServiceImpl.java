package com.carp.forum.serviceImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.entities.FileInfo;
import com.carp.forum.entities.Post;
import com.carp.forum.exception.UnsupportedFileTypeException;
import com.carp.forum.repository.FileInfoRepository;
import com.carp.forum.service.FileService;
import com.carp.forum.tools.FileTools;


@Service
public class FileServiceImpl implements FileService {

	@Value("${file.upload-dir}")
	private String storageFolder;


	@Autowired
	private FileInfoRepository fileInfoRepository;



	@Override
	public String saveFile(MultipartFile file, Post post) throws IOException, UnsupportedFileTypeException  {
		if(!FileTools.isTypeAllowed(file.getContentType())) {
			throw new UnsupportedFileTypeException("This file type is not supported");
		}
		String saveLocation = storageFolder.trim();
		Path storagePath = Paths.get(saveLocation).toAbsolutePath().normalize();
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		String fileExtension = FileTools.getExtensionFromType( file.getContentType());
		
		String fileName = RandomStringUtils.random(12, true, true);
		while(fileInfoRepository.existsByFileName(fileName)) {
			fileName = RandomStringUtils.random(12, true, true);
		}
		
		FileInfo fileInfo = new FileInfo();
		Post p = new Post();
		p.setId(post.getId());
		fileInfo.setPost(p); // test if hibernate can do the mapping with just the post id
		fileInfo.setFileType(file.getContentType());
		
		
		fileInfo.setFileName(fileName);
		
		Path targetLocation = storagePath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		
		fileInfoRepository.saveAndFlush(fileInfo);
        
		return fileName;
	}

	@Override
	public Resource loadFileAsResource(String fileName) throws FileNotFoundException, MalformedURLException{
			
			Path storagePath =Paths.get(storageFolder.trim()).toAbsolutePath();
			Path filePath =storagePath.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if(resource.exists()) {
				return resource;
			}else {
				throw new FileNotFoundException("File not found: "+ fileName);
			}

	}

}
