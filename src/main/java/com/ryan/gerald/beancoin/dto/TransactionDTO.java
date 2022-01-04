package com.ryan.gerald.beancoin.dto;


public class TransactionDTO {

    private String fromAddress;
    private double fromBalance;
    private String toAddress;
    private double toAmount;
    private String publickey;
    private String signature;
    private String format;

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public double getToAmount() {
        return toAmount;
    }

    public void setToAmount(double toAmount) {
        this.toAmount = toAmount;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public double getFromBalance() {
        return fromBalance;
    }

    public void setFromBalance(double fromBalance) {
        this.fromBalance = fromBalance;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
