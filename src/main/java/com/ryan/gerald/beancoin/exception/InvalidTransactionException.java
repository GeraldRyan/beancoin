package com.ryan.gerald.beancoin.exception;

public class InvalidTransactionException extends Exception {
	public InvalidTransactionException(String errorMessage) {
		super(errorMessage);
	}

}
