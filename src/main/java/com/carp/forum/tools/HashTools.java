package com.carp.forum.tools;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;



public class HashTools {

	//chaine =hachage=> chaine non déchiffrable
	public static String hashSHA512(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	
		MessageDigest md = MessageDigest.getInstance("SHA-512");//singleton unique
		md.reset(); //réinitialiser le contenu du msg digest
		
		//application de l'algorithme choisis
		byte[] hachedArray = md.digest(input.getBytes("utf-8"));
		
		//conversion du message haché (tab de bytes) en une représentation numérique signée
		BigInteger bi = new BigInteger(1, hachedArray);
		return String.format("%0128x", bi);	
		
	}
	
}
