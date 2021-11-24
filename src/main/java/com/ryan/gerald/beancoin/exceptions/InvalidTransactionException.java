package com.ryan.gerald.beancoin.exceptions;

public class InvalidTransactionException extends Exception {
	public InvalidTransactionException(String errorMessage) {
		super(errorMessage);
	}

}
