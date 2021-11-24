package com.ryan.gerald.beancoin.exceptions;

public class TransactionAmountExceedsBalance extends Exception {
	public TransactionAmountExceedsBalance(String errorMessage) {
		super(errorMessage);
	}
}
