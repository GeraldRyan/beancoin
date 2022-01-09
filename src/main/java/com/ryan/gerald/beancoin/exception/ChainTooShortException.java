package com.ryan.gerald.beancoin.exception;

public class ChainTooShortException extends Exception {
	public ChainTooShortException(String errorMessage) {
		super(errorMessage);
	}
}
