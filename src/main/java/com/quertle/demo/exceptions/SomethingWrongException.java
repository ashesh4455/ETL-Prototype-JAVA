package com.quertle.demo.exceptions;

public class SomethingWrongException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SomethingWrongException(String message, String errorMessage){
		super(message);
	}
}
