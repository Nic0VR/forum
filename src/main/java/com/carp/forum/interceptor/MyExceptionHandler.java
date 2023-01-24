package com.carp.forum.interceptor;

import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.carp.forum.dto.ApiErrorDto;
import com.carp.forum.dto.ApiErrorDto.LogLevel;
import com.carp.forum.exception.BadPayloadException;
import com.carp.forum.exception.DuplicateEntryException;
import com.carp.forum.exception.EntityNotFoundException;
import com.carp.forum.exception.ForbiddenActionException;
import com.carp.forum.exception.InvalidUpdateException;
import com.carp.forum.exception.TokenException;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler  {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyExceptionHandler.class);
	@ExceptionHandler(value= {TokenException.class})
	protected ResponseEntity<?> handleTokenException(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(401);
		error.setMessage(ex.getMessage());
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(value= {ForbiddenActionException.class})
	protected ResponseEntity<?> handleForbiddenActionException(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(403);
		error.setMessage(ex.getMessage());
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.FORBIDDEN, request);
	}
	

	@ExceptionHandler(value= {InvalidUpdateException.class})
	protected ResponseEntity<?> handleIllegalUpdateException(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(400);
		error.setMessage(ex.getMessage());
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(value= {StaleObjectStateException.class})
	protected ResponseEntity<?> handleStaleObjectStateException(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(400);
		error.setMessage("Error on version field");
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(value= {DuplicateEntryException.class})
	protected ResponseEntity<?> handleDuplicateEntryException(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(400);
		error.setMessage(ex.getMessage());
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(value= {BadPayloadException.class})
	protected ResponseEntity<?> handleBadPayloadException(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(400);
		error.setMessage(ex.getMessage());
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(value= {EntityNotFoundException.class})
	protected ResponseEntity<?> handleEntityNotFound(Exception ex, WebRequest request){
		
		ApiErrorDto error = new ApiErrorDto();
		error.setErrorCode(400);
		error.setMessage(ex.getMessage());
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		error.setLevel(LogLevel.INFO);
		LOGGER.error("Exception: "+ex.getClass()+" "+ex.getMessage()+" AT PATH :"+error.getPath());
	
		return handleExceptionInternal(ex, error, new HttpHeaders(), 
				HttpStatus.NOT_FOUND, request);
	}
}
