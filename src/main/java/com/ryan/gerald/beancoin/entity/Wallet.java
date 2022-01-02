package com.ryan.gerald.beancoin.entity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.utils.StringUtils;
import com.ryan.gerald.beancoin.utils.TransactionRepr;

/**
 * An individual wallet for a miner. Keeps track of miner's balance. Allows
 * miner to authorize Transactions via application logic.
 * (Public and Private keys handled implicitly by application)
 * <p>
 * NOTE- HAVING A SETTER FOR ADDRESS FIELD WILL BREAK THIS BEAN at present
 * related to instantiating new transactions with request param page.
 * setAddress() breaks bean. Maybe setThisAddress will work fine but setAddress
 * will break the
 *
 * @author Gerald Ryan
 */
@Entity
public class Wallet {
    public PrivateKey getPrivatekey() {
        return privatekey;
    }

    @Id String ownerId; // maps to username
    @Lob PrivateKey privatekey;
    @Lob PublicKey publickey;
    byte[] privatekeyByte; // for language agnosticism
    byte[] publickeyByte; // for language agnosticism
    double balance;
    String address;
    double balanceMined;

    static double STARTING_BALANCE = 0; // New method. Will create an admin wallet and transfer funds from that

    public Wallet() {
    }

    public Wallet(double balance, PrivateKey privatekey, PublicKey publickey, String address, String ownerId) {
        super();
//		Blockchain bc = new BlockchainService().getBlockchainService("beancoin"); // Old Code

        // can remove param 1 balance now or better yet add the blockchain as a
        // dependency injection as this is tight coupling
        this.balance = balance;
        this.privatekey = privatekey;
        this.publickey = publickey;
        this.address = address;
        this.ownerId = ownerId;
    }

    /**
     * TODO are we using this?
     * Refactor anyway
     */
    public Wallet(double balance, PublicKey publickey, String address) {
        super(); // huh
        balance = Wallet.calculateWalletBalanceByTraversingChain(new BlockchainService().getBlockchainByName("beancoin"), address);
        // can remove param 1 balance now or better yet add the blockchain as a
        // dependency injection as this is tight coupling
        this.balance = balance;
        this.privatekey = privatekey;
        this.publickey = null;
        this.address = address;
    }

    public static Wallet createWallet(String ownerId)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String address = String.valueOf(UUID.randomUUID()).substring(0, 8);
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "SunEC");
        keyGen.initialize(new ECGenParameterSpec("secp256k1"));
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        if (ownerId == "admin"){return new Wallet(1000000, privateKey, publicKey, address, ownerId);}
        Wallet wallet = new Wallet(STARTING_BALANCE, privateKey, publicKey, address, ownerId);
        System.out.println("NEW WALLET CREATED");
        return wallet;
    }

    /**
     * Generate a signature based on data using local private key
     */
    public byte[] sign(byte[] data)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withECDSA", "SunEC");
        sig.initSign(privatekey);
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        System.out.println("Data has been successfully signed");
        return signatureBytes;
    }

    public byte[] sign(Object dataObj) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            SignatureException, IOException {
        byte[] data = StringUtils.objectToByteArray(dataObj);
        Signature sig = Signature.getInstance("SHA256withECDSA", "SunEC");
        sig.initSign(privatekey);
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        System.out.println("Data has been successfully signed");
        return signatureBytes;
    }

    /**
     * Verifies signature of data of given public key
     */
    public static boolean verifySignature(byte[] signatureBytes, byte[] data, PublicKey publickey)
            throws SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        Signature sig = Signature.getInstance("SHA256withECDSA", "SunEC");
        sig.initVerify(publickey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    public static boolean verifySignature(byte[] signatureBytes, String[] dataStr, PublicKey publickey)
            throws SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            IOException {
        byte[] data = StringUtils.stringArrayToByteArray(dataStr);
        Signature sig = Signature.getInstance("SHA256withECDSA", "SunEC");
        sig.initVerify(publickey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    public static boolean verifySignature(byte[] signatureBytes, Object obj, PublicKey publickey)
            throws SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            IOException {
        byte[] data = StringUtils.objectToByteArray(obj);
        Signature sig = Signature.getInstance("SHA256withECDSA", "SunEC");
        sig.initVerify(publickey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    public static void testSign() throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, UnsupportedEncodingException, SignatureException, InvalidKeyException {

        Wallet wallet1 = Wallet.createWallet("joe");
        System.out.println(wallet1);
        System.out.println("_________________-");
        byte[] signatureBytes = wallet1.sign("CATSMEOW".getBytes("UTF-8"));
        System.out.println("Was it signed properly? Expect true. Drumroll... -> "
                + wallet1.verifySignature(signatureBytes, "CATSMEOW".getBytes("UTF-8"), wallet1.getPublickey()));
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
        Wallet.testSign();
    }

    /**
     * Restores a public key object in your chosen language from either a Base64
     * String or a byte[] as received over the wire (contains X.509 standard)
     */
    public static PublicKey restorePublicKey(byte[] publickey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
//		KeySpec ks = new X509EncodedKeySpec(pk, "EC");
        KeySpec ks = new X509EncodedKeySpec(publickey);
        PublicKey pkRestored = keyFactory.generatePublic(ks);
        return pkRestored;
    }

    public static PublicKey restorePublicKey(String publickey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
        KeySpec ks = new X509EncodedKeySpec(publickey.getBytes(StandardCharsets.UTF_8));
        PublicKey pkRestored = keyFactory.generatePublic(ks);
        return pkRestored;
    }

    /**
     * Calculates balance of address based on blockchain history (only counts MINED BLOCKS)
     * Two ways to find balance: calculate all transactions to and fro or trusting
     * output values
     */
    public static double calculateWalletBalanceByTraversingChain(Blockchain bc, String adds) {
        double balance = STARTING_BALANCE; // starting balance. static means not touching real wallet.
        // loop through transactions - yes, every transaction of every block of the
        // entire chain (minus the dummy data chains)
        System.out.println("String address" + adds);
        if (bc == null) {
            System.err.println("BLOCKCHAIN IS NULL");
            System.err.println("String address" + adds);
            return -1; // if -1 in caller function, leave balance same. Should this have been non
            // static perhaps?
        }

        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks
            // would for (i=0; i<7; i++) {continue;} work?
            List<TransactionRepr> trListMinedBlocks = b.deserializeTransactionData();
            for (TransactionRepr t : trListMinedBlocks) {
                if (t.getInput().get("address").equals(adds)) { // wallet is sender -- deduct balance
                    // reset balance after each transaction
                    balance = (double) t.getOutput().get(adds);
                } else if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add balance to.
                    balance += (double) t.getOutput().get(adds);
                }
            }
        }
        List<TransactionRepr> trListUnmined = new ArrayList();
        for (TransactionRepr t : trListUnmined) {
            if (t.getInput().get("address").equals(adds)) { // wallet is sender -- deduct balance
                // reset balance after each transaction
                balance = (double) t.getOutput().get(adds);
            } else if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add balance to.
                balance += (double) t.getOutput().get(adds);
            }
        }
        return balance;
    }

    public double getBalanceMined() {
        return balanceMined;
    }

    public void setBalanceMined(double balanceMined) {
        this.balanceMined = balanceMined;
    }

    // TODO wrap above function in this for cleaner code
    // TODO BE ABLE TO TRAVERSE FROM END OF BLOCK BACK UNTIL YOU HIT SOMETHING WTIH USERS ADDRESS. or something
    public static double calculateWalletBalanceByTraversingChainIncludePending(Blockchain bc, String adds, List<TransactionRepr> pendingTransactions) {
        double bal = STARTING_BALANCE;
        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks
            // would for (i=0; i<7; i++) {continue;} work?
            System.out.println("BLOCK DESERIALIZED " + b.toStringConsole());
            List<TransactionRepr> trListMinedBlocks = b.deserializeTransactionData();
            double currentPmt = 0;
            for (TransactionRepr t : trListMinedBlocks) {
                if (t.getInput().get("address").equals(adds)) {  // input is moving per transaction and it should not.
                    System.out.println("INPUT BALANCE : " + t.getInput().get("amount"));
                    currentPmt = (double) t.getInput().get("amount") - (double) t.getOutput().get(adds);
                    System.out.println("Input Balance to deduct " + currentPmt );
                    bal -= currentPmt;
                    break;
                }
                if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add receipts.
                    System.out.println("ADD: " + t.getOutput().get(adds) + "    to " + adds);
                    bal += (double) t.getOutput().get(adds);
                }
            }
        }

        // Process Pending Transactions
        double paying = 0;
        double receiving = 0;
        for (TransactionRepr t : pendingTransactions) {
            if (t.getInput().get("address").equals(adds)) {
                paying = (double) t.getInput().get("amount") - (double) t.getOutput().get(adds);
                System.out.println("T INPUT " + t.getInput().get("amount") + " and output " + t.getOutput().get(adds));
                System.out.println("Paying " + paying + " by " + adds);
                break;
            }
            if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add bal to.
                receiving += (double) t.getOutput().get(adds);
                System.out.println("Receiving " + receiving + " by " + adds);
            }
        }
        return bal - paying + receiving;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Wallet [balance=" + balance + ", publickey=" + publickey + ", address=" + address + "]";
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public static double getSTARTING_BALANCE() {
        return STARTING_BALANCE;
    }

    public PublicKey getPublickey() {
        return publickey;
    }

}
