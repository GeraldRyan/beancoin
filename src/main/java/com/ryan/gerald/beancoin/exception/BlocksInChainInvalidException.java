package com.ryan.gerald.beancoin.exception;

public class BlocksInChainInvalidException extends Exception {
	public BlocksInChainInvalidException(String errorMessage) {
		super(errorMessage);
	}
}
