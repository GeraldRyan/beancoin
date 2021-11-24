package com.ryan.gerald.beancoin.exceptions;

public class ChainTooShortException extends Exception {
	public ChainTooShortException(String errorMessage) {
		super(errorMessage);
	}
}
