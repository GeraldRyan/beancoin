package com.ryan.gerald.beancoin.entity;

import com.ryan.gerald.beancoin.exceptions.InvalidTransactionException;
import com.ryan.gerald.beancoin.utils.StringUtils;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class AbstractTransaction {

    /**
     * Validate a transaction. For invalid transactions, raises
     * InvalidTransactionException
     */
    public static boolean is_valid_transaction(Transaction transaction) throws InvalidTransactionException,
            InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException {
        String signatureString = (String) transaction.getInput().get("signatureB64");
        String publicKeyString = (String) transaction.getInput().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = Wallet.restorePublicKey(publicKeyByte);
//		PublicKey restoredPK = Wallet.restorePK((String) transaction.getInput().get("publicKeyB64"));
//		PublicKey originalPK = (PublicKey) transaction.input.get("publicKey");
        double sumOfTransactions = transaction.getOutput().values().stream().mapToDouble(v -> (double) v).sum();
        System.out.println("Sum of values " + sumOfTransactions);
        if (sumOfTransactions != (double) transaction.getInput().get("amount")) {
            throw new InvalidTransactionException("TRANSACTION OUTPUT DOESN'T MATCH INPUT");
        }
//		if (!Wallet.verifySignature((byte[]) transaction.input.get("signature"), transaction.output,
//				originalPK)) {
//			System.err.println("Signature not valid!");
//			throw new InvalidTransactionException("Invalid Signature");
//		}
        byte[] data = StringUtils.objectToByteArray(transaction.getOutput());
        if (!Wallet.verifySignature(signatureString, data, reconstructedPK)) {
            System.err.println("SIGNATURE NOT VALID!");
            throw new InvalidTransactionException("INVALID SIGNATURE");
        }
        return true;
    }


    public static boolean is_valid_transactionReconstructPK(Transaction transaction)
            throws InvalidTransactionException, InvalidKeyException, SignatureException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException, InvalidKeySpecException {
        StringUtils.mapKeyValue(transaction.getOutput(), "Line 289");

        double sumOfTransactions = transaction.getOutput().values().stream().mapToDouble(t -> (double) t).sum();
        System.out.println("Sum of values " + sumOfTransactions);
        String signatureString = (String) transaction.getInput().get("signatureString");
        String publicKeyString = (String) transaction.getInput().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = Wallet.restorePublicKey(publicKeyByte);

        System.out.println("signature string: " + signatureString);
        System.out.println("PKSTring string: " + publicKeyString);
        System.out.println("signature byte: " + signatureByte);
        System.out.println("PK Byte: " + publicKeyByte);
        if (sumOfTransactions != (double) transaction.getInput().get("amount")) {
            throw new InvalidTransactionException("Value mismatch of propsed transactions");
        }
        System.out.println(transaction.getInput().get("signature"));
        byte[] data = StringUtils.objectToByteArray(transaction.getOutput());
        if (!Wallet.verifySignature(signatureString, data, reconstructedPK)) {
            System.err.println("Signature not valid!");
            throw new InvalidTransactionException("Invalid Signature");
        }
        return true;
    }
}
