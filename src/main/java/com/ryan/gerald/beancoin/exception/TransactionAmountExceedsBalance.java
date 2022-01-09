package com.ryan.gerald.beancoin.exception;

public class TransactionAmountExceedsBalance extends Exception {
	public TransactionAmountExceedsBalance(String errorMessage) {
		super(errorMessage);
	}
}
