package com.ryan.gerald.beancoin.exceptions;

public class BlocksInChainInvalidException extends Exception {
	public BlocksInChainInvalidException(String errorMessage) {
		super(errorMessage);
	}
}
