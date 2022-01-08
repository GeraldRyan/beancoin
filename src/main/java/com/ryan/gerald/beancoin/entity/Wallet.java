package com.ryan.gerald.beancoin.entity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import javax.persistence.*;

import com.ryan.gerald.beancoin.utils.StringUtils;
import com.ryan.gerald.beancoin.utils.TransactionRepr;

/**
 * An individual wallet for a miner. Keeps track of miner's balance. Allows
 * miner to authorize Transactions via application logic.
 *
 * @author Gerald Ryan
 */
@Entity
public class Wallet {

    @Id String ownerId; // ==> User.username
    @Lob PrivateKey privatekey;  // keep for now- but maybe even immediately can just store as string base 64
    @Lob PublicKey publickey;
    double balance;
    String address;
    double balanceAsMined;
    final static double STARTING_BALANCE = 0;
    final static String PROVIDER = "SunEC";
    final static String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    final static String KEYPAIR_GEN_ALGORITHM = "EC";
    final static String PARAMETER_SPEC = "secp256k1";


    public Wallet() {}

    public Wallet(double balance, PrivateKey privatekey, PublicKey publickey, String address, String ownerId) {
        this.balance = balance;
        this.privatekey = privatekey;
        this.publickey = publickey;
        this.address = address;
        this.ownerId = ownerId;
    }

    public Transaction createTransaction(String toAddress, double toAmount) throws NoSuchAlgorithmException, SignatureException, IOException, NoSuchProviderException, InvalidKeyException {
        if (this.getBalance() < toAmount){
            System.err.println("Transaction Amount exceeds balance");
            return null;
        }
        // get output (receipt or agreement) to sign
        HashMap<String, Object> outputMap = Transaction.createOutputMap(this.getAddress(), toAddress, this.getBalance(), toAmount);
        String signatureB64 = this.sign(outputMap);
        String publicKeyString = Base64.getEncoder().encodeToString(this.getPublickey().getEncoded());
        String publicKeyFormat = this.getPublickey().getFormat();
        return Transaction.createTransaction(toAddress, toAmount, this.getAddress(), this.getBalance(), outputMap, signatureB64, publicKeyString, publicKeyFormat);
    }

    public static Wallet createWallet(String ownerId) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Wallet.createWallet(ownerId, STARTING_BALANCE);
    }

    public static Wallet createWallet(String ownerId, double startingBalance)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String address = String.valueOf(UUID.randomUUID()).substring(0, 8);
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEYPAIR_GEN_ALGORITHM, PROVIDER);
        keyGen.initialize(new ECGenParameterSpec(PARAMETER_SPEC));
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        if (ownerId == "admin") {return new Wallet(1000000, privateKey, publicKey, address, ownerId);}
        System.out.println("NEW WALLET CREATED");
        return new Wallet(startingBalance, privateKey, publicKey, address, ownerId);
    }



    /**
     * Generate a signature based on data using local private key
     */
    public String sign(HashMap<?, ?> dataObj) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            SignatureException, IOException {
        byte[] data = StringUtils.objectToByteArray(dataObj);
        return sign(data);
    }

    public String sign(byte[] data)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
        signature.initSign(privatekey);
        signature.update(data);
        byte[] signatureBytes = signature.sign();
        System.out.println("Data has been successfully signed");
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * Verifies signature of given data given publicKey and algorithm/provider info
     */
    public static boolean verifySignature(String signatureToCheck, byte[] data, PublicKey publickey)
            throws SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
        signature.initVerify(publickey);
        signature.update(data);
        byte[] sigbytes = Base64.getDecoder().decode(signatureToCheck);
        return signature.verify(sigbytes);
    }

    @Override
    public String toString() {return "Wallet [balance=" + balance + ", publickey=" + publickey + ", address=" + address + "]";}
    public double getBalance() {return balance;}
    public void setBalance(double balance) {this.balance = balance;}
    public String getAddress() {return address;}
    public PublicKey getPublickey() {return publickey;}
    public PrivateKey getPrivatekey() {return privatekey;}
    public double getBalanceAsMined() {return balanceAsMined;}
    public void setBalanceAsMined(double balanceAsMined) {this.balanceAsMined = balanceAsMined;}

    /**
     * TODO Belongs in src/main/test
     */
    public static void testVerifySignature() {
        try {
            Wallet w = Wallet.createWallet("joe");
            byte[] data = "cats_meow".getBytes("UTF-8");
            String signatureToCheck = w.sign(data);
            boolean verified = w.verifySignature(signatureToCheck, data, w.getPublickey());
            System.out.println("Signature validity:: " + verified);
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, InvalidKeyException, IOException, SignatureException {
        Wallet.testVerifySignature();
    }
}