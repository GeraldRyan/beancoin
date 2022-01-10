package com.ryan.gerald.beancoin.entity;

import com.ryan.gerald.beancoin.utils.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * An individual wallet for a miner. Keeps track of miner's balance. Allows
 * miner to authorize Transactions via application logic.
 *
 * @author Gerald Ryan
 */
@Entity
public class Wallet {

    public @Id String ownerId; // ==> User.username
    public @Lob PrivateKey privatekey;  // keep for now- but maybe even immediately can just store as string base 64
    public @Lob PublicKey publickey;
    public double balance;
    public String address;
    public double balanceAsMined;
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
        if (this.getBalance() < toAmount) {
            System.out.println("Transaction Amount " + toAmount + " exceeds balance of " + this.getBalance());
            return null;
        }
        if (this.getBalanceAsMined() < toAmount){
            System.out.println("Please wait till enough balance has been mined");
            return null;
        }
        // get output (receipt or agreement) to sign
        HashMap<String, Object> outputMap = Transaction.createOutputMap(this.getAddress(), toAddress, this.getBalance(), toAmount);
        String signatureB64 = this.sign(outputMap);
        String publicKeyString = Base64.getEncoder().encodeToString(this.getPublickey().getEncoded());
        String publicKeyFormat = this.getPublickey().getFormat();
        return Transaction.createTransaction(toAddress, toAmount, this.getAddress(), this.getBalance(), outputMap, signatureB64, publicKeyString, publicKeyFormat);
    }

    // Starting balance will reset to zero anyway if there's not a transfer reflected on the chain according to current consensus protocol (Except mint wallet that has a genesis magic transaction).
    public static Wallet createWallet(String ownerId) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return Wallet.createWallet(ownerId, STARTING_BALANCE);
    }

    private static Wallet createWallet(String ownerId, double startingBalance) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
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

    public static Wallet createAdminWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.createWallet("admin");
        w.address = "777mint777";
        return w;
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

    public String getAddress() {
        return this.address;
    }

    // NOTE when this is defined- even not called, something in spring via controler calls it and mutates the wallet address to the address of recipient!!!!!!!!!!!!!! So keep it LOCKED:D:D:D:D:D:D
    public void setAddress(String address) {return;}
    // DO NOTHING. EVEN DEFINING THIS METHOD IS BAD IN IoC PLACES- a spring loaded trap. Setter methods of lazy classes cannot be final.


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