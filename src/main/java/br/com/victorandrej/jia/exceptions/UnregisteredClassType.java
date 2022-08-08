package br.com.victorandrej.jia.exceptions;

public class UnregisteredClassType extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnregisteredClassType(String message) {
		super(message);
	}



}
