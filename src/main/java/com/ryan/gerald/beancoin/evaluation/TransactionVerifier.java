package com.ryan.gerald.beancoin.evaluation;

import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.exceptions.InvalidTransactionException;
import com.ryan.gerald.beancoin.utils.StringUtils;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * TODO Completely fix and clean up and nuke these. MOST LIKELY GARBAGE.
 * Verify Incoming Blocks and Transactions from the blockchain
 */
public class TransactionVerifier {

    /**
     * Validate a transaction. For invalid transactions, raises
     * InvalidTransactionException
     */
    public boolean isTransactionValid(Transaction transaction) throws InvalidTransactionException,
            InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException {
        String signatureString = (String) transaction.getInputMap().get("signatureB64");
        String publicKeyString = (String) transaction.getInputMap().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = new KeyUtils().getPublicKeyObj(publicKeyByte);
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


    public boolean is_valid_transactionReconstructPK(Transaction transaction)
            throws InvalidTransactionException, InvalidKeyException, SignatureException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException, InvalidKeySpecException {
        StringUtils.mapKeyValue(transaction.getOutputMap(), "Line 289");

        double sumOfTransactions = transaction.getOutputMap().values().stream().mapToDouble(t -> (double) t).sum();
        System.out.println("Sum of values " + sumOfTransactions);
        String signatureString = (String) transaction.getInputMap().get("signatureString");
        String publicKeyString = (String) transaction.getInputMap().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = new KeyUtils().getPublicKeyObj(publicKeyByte);

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
