package com.carp.forum.serviceImpl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.carp.forum.entities.FileInfo;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.Thread;
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
	public FileInfo saveFile(MultipartFile file, Post post) throws IOException, UnsupportedFileTypeException  {
		Tika tika = new Tika();
		String mimeType = tika.detect(file.getBytes());
		if(!FileTools.isTypeAllowed(mimeType)) {
			throw new UnsupportedFileTypeException("This file type is not supported: "+file.getContentType());
		}

		String saveLocation = storageFolder.trim();
		Path storagePath = Paths.get(saveLocation).toAbsolutePath().normalize();
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		String fileExtension = FileTools.getExtensionFromType( file.getContentType());
		
		String fileName = RandomStringUtils.random(12, true, true)+fileExtension;
		while(fileInfoRepository.existsByFileName(fileName)) {
			fileName = RandomStringUtils.random(12, true, true);
		}
		String fileNamePreview = RandomStringUtils.random(12, true, true)+".png";
		while(fileInfoRepository.existsByFileName(fileNamePreview)) {
			fileNamePreview = RandomStringUtils.random(12, true, true)+".png";
		}
		
		FileInfo fileInfo = new FileInfo();
		BufferedImage originalImage = ImageIO.read(file.getInputStream());
		int originalW = originalImage.getWidth();
		int originalH = originalImage.getHeight();
		double maxWidth=200;
		double maxHeight=200;
		double widthRatio = maxWidth / originalW;
	    double heightRatio = maxHeight /originalH;
	    double ratio = Math.min(widthRatio, heightRatio);
	    int previewWidth= (int) Math.round(  ratio*originalW);
	    int previewHeight= (int) Math.round(  ratio*originalH);
		BufferedImage resizedImage = resizeImage(originalImage, previewWidth, previewHeight);
		
		fileInfo.setPost(post);
		fileInfo.setFileType(file.getContentType());
		fileInfo.setFileName(fileName);
		fileInfo.setOriginalFileName(originalFileName);
		fileInfo.setPrefiewFileName(fileNamePreview);
		Path targetLocation = storagePath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        File minifiedImageFile= new File("forum/"+fileNamePreview);

        ImageIO.write(resizedImage,"png" , minifiedImageFile);
        fileInfo=fileInfoRepository.saveAndFlush(fileInfo);
        
		return fileInfo;
	}
	
	@Override
	public Object saveFile(MultipartFile file, Thread entityToSave)
			throws IOException, UnsupportedFileTypeException {
		Tika tika = new Tika();
		String mimeType = tika.detect(file.getBytes());
		if(!FileTools.isTypeAllowed(mimeType)) {
			throw new UnsupportedFileTypeException("This file type is not supported: "+file.getContentType());
		}
		String saveLocation = storageFolder.trim();
		Path storagePath = Paths.get(saveLocation).toAbsolutePath().normalize();
		String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		String fileExtension = FileTools.getExtensionFromType( file.getContentType());
		
		String fileName = RandomStringUtils.random(12, true, true)+fileExtension;
		while(fileInfoRepository.existsByFileName(fileName)) {
			fileName = RandomStringUtils.random(12, true, true)+fileExtension;
		}
		String fileNamePreview = RandomStringUtils.random(12, true, true)+".png";
		while(fileInfoRepository.existsByFileName(fileNamePreview)) {
			fileNamePreview = RandomStringUtils.random(12, true, true)+".png";
		}
		//TODO : clean this 
		FileInfo fileInfo = new FileInfo();
		BufferedImage originalImage = ImageIO.read(file.getInputStream());
		int originalW = originalImage.getWidth();
		int originalH = originalImage.getHeight();
		double maxWidth=200;
		double maxHeight=200;
		double widthRatio = maxWidth / originalW;
	    double heightRatio = maxHeight /originalH;
	    double ratio = Math.min(widthRatio, heightRatio);
	    int previewWidth= (int) Math.round(  ratio*originalW);
	    int previewHeight= (int) Math.round(  ratio*originalH);
		BufferedImage resizedImage = resizeImage(originalImage, previewWidth, previewHeight);
		

		fileInfo.setThread(entityToSave);
		fileInfo.setFileType(file.getContentType());
		fileInfo.setFileName(fileName);
		fileInfo.setOriginalFileName(originalFileName);
		fileInfo.setPrefiewFileName(fileNamePreview);
		Path targetLocation = storagePath.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        File minifiedImageFile= new File("forum/"+fileNamePreview);

        ImageIO.write(resizedImage,"png" , minifiedImageFile);
        fileInfo=fileInfoRepository.saveAndFlush(fileInfo);
        
		return fileInfo;
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
	
	public boolean delete(String filename) {
		
		try {
			Path storagePath = Paths.get(storageFolder.trim()).toAbsolutePath();
			Path filePath =storagePath.resolve(filename).normalize();
			File file = new File(filePath.toUri());
			file.delete();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	
	}
	
	BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
	    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
	    return outputImage;
	}

}
