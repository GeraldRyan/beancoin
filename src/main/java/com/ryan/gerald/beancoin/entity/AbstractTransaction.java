package com.ryan.gerald.beancoin.entity;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.exceptions.InvalidTransactionException;
import com.ryan.gerald.beancoin.utils.StringUtils;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class AbstractTransaction {
    String uuid;

    /**
     * Structures output data of wallet - a hashmap of two items, currency going to
     * recipient at address and change going back to sender
     */
    public static HashMap<String, Object> createOutputMap(String senderAddress, String recipientAddress, double fromBalance, double toAmount) {
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put(senderAddress, (fromBalance - toAmount));
        output.put(recipientAddress, toAmount);
        return output;
    }
    /**
     * Structured meta data about transaction, including digitial binding signature
     * of transaction from sender Includes senders public key for verification
     */
    protected static HashMap<String, Object> createInputMap(String senderAddress,
                                                         double senderBalance,
                                                         String signatureB64,
                                                         String publicKeyB64,
                                                         String pkFormat) {
        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("timestamp", new Date().getTime());
        input.put("amount", senderBalance);
        input.put("address", senderAddress);
        input.put("publicKeyB64", publicKeyB64);
        input.put("publicKeyFormat", pkFormat);
        input.put("signatureB64", signatureB64);
        return input;
    }

    protected HashMap<String, Object> deserializeOutputJson(String outputJson){
        return new Gson().fromJson(outputJson, HashMap.class);
    }
    protected HashMap<String, Object> deserializeInputJson(String inputJson){
        return new Gson().fromJson(inputJson, HashMap.class);
    }

    protected String serializeOutpuMap(HashMap<String, Object> outputMap){
        return new Gson().toJson(outputMap);
    }
    protected String serializeInputMap(HashMap<String, Object> inputMap){
        return new Gson().toJson(inputMap);
    }

    /**
     * Base class for Transactionm implementation. Contains static method to verify transaction.
     * @param transaction
     * @return
     * @throws InvalidTransactionException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public static boolean is_valid_transaction(Transaction transaction) throws InvalidTransactionException,
            InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException {
        String signatureString = (String) transaction.getInputMap().get("signatureB64");
        String publicKeyString = (String) transaction.getInputMap().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = Wallet.restorePublicKey(publicKeyByte);
//		PublicKey restoredPK = Wallet.restorePK((String) transaction.getInput().get("publicKeyB64"));
//		PublicKey originalPK = (PublicKey) transaction.input.get("publicKey");
        double sumOfTransactions = transaction.getOutputMap().values().stream().mapToDouble(v -> (double) v).sum();
        System.out.println("Sum of values " + sumOfTransactions);
        if (sumOfTransactions != (double) transaction.getInputMap().get("amount")) {
            throw new InvalidTransactionException("TRANSACTION OUTPUT DOESN'T MATCH INPUT");
        }
//		if (!Wallet.verifySignature((byte[]) transaction.input.get("signature"), transaction.output,
//				originalPK)) {
//			System.err.println("Signature not valid!");
//			throw new InvalidTransactionException("Invalid Signature");
//		}
        byte[] data = StringUtils.objectToByteArray(transaction.getOutputMap());
        if (!Wallet.verifySignature(signatureString, data, reconstructedPK)) {
            System.err.println("SIGNATURE NOT VALID!");
            throw new InvalidTransactionException("INVALID SIGNATURE");
        }
        return true;
    }


    public static boolean is_valid_transactionReconstructPK(Transaction transaction)
            throws InvalidTransactionException, InvalidKeyException, SignatureException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException, InvalidKeySpecException {
        StringUtils.mapKeyValue(transaction.getOutputMap(), "Line 289");

        double sumOfTransactions = transaction.getOutputMap().values().stream().mapToDouble(t -> (double) t).sum();
        System.out.println("Sum of values " + sumOfTransactions);
        String signatureString = (String) transaction.getInputMap().get("signatureString");
        String publicKeyString = (String) transaction.getInputMap().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = Wallet.restorePublicKey(publicKeyByte);

        System.out.println("signature string: " + signatureString);
        System.out.println("PKSTring string: " + publicKeyString);
        System.out.println("signature byte: " + signatureByte);
        System.out.println("PK Byte: " + publicKeyByte);
        if (sumOfTransactions != (double) transaction.getInputMap().get("amount")) {
            throw new InvalidTransactionException("Value mismatch of propsed transactions");
        }
        System.out.println(transaction.getInputMap().get("signature"));
        byte[] data = StringUtils.objectToByteArray(transaction.getOutputMap());
        if (!Wallet.verifySignature(signatureString, data, reconstructedPK)) {
            System.err.println("Signature not valid!");
            throw new InvalidTransactionException("Invalid Signature");
        }
        return true;
    }
}
