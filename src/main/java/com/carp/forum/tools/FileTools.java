package com.carp.forum.tools;

import java.util.Map;

public class FileTools {


	public static final Map<String,String> supportedFileTypes = Map.of(
			"image/bmp",".bmp",
			"image/gif",".gif",
			"image/jpeg",".jpg",
			"image/png",".png");

	public static boolean isTypeAllowed(String mimeType) {
		return supportedFileTypes.containsKey(mimeType);
	}
	
	public static boolean isExtensionAllowed(String extension) {
		return supportedFileTypes.containsValue(extension);
	}
	
	public static String getExtensionFromType(String type) {
		return supportedFileTypes.get(type);
		
	}
}
