package com.ryan.gerald.beancoin.entity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.exceptions.InvalidTransactionException;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;
import com.ryan.gerald.beancoin.utilities.StringUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

/**
 * Document an exchange of currency from a sender to one or more recipients
 *
 * @author User
 */
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    String uuid;
    @Transient
    Wallet senderWallet;
    String recipientAddress;
    String senderAddress;
    double amount;
    /**
     * Data structure about who the recipients are in the transaction and how much
     * currency they are receiving and how much goes back to sender (change). Kind
     * of like a simple receipt/ledger.
     */
    @Transient
    HashMap<String, Object> output; // like basic receipt
    /**
     * Meta info about transaction including timestamp, address, publickey of sender
     * and signature, used to determine validity. Depends on output above
     */
    @Transient
    HashMap<String, Object> input; // like wire transfer document
    @Column(columnDefinition = "varchar(2000) default 'Jon Snow'")
    String outputjson;
    @Column(columnDefinition = "varchar(2000) default 'Jon Snow'")
    String inputjson;

    public Transaction(Wallet senderWallet, String recipientAddress, double amount)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, TransactionAmountExceedsBalance {
        super();
        if (senderWallet.getBalance() < amount) {
            throw new TransactionAmountExceedsBalance("Transactionm Amount Exceeds Balance");
        }
        this.uuid = StringUtils.getUUID8();
        this.output = Transaction.createOutputMap(senderWallet, recipientAddress, amount);
        try {
            this.input = Transaction.createInputMap(senderWallet, this.output);
        } catch (SignatureException e) {e.printStackTrace();}
        this.senderWallet = senderWallet;
        this.recipientAddress = recipientAddress;
        this.senderAddress = senderWallet.getAddress();
        this.outputjson = new Gson().toJson(output);
        this.inputjson = new Gson().toJson(input);
        this.amount = amount;
        System.out.println("New valid transaction created");
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getOutputjson() {
        return outputjson;
    }

    public void setOutputjson(String outputjson) {
        this.outputjson = outputjson;
    }

    public String getInputjson() {
        return inputjson;
    }

    public void setInputjson(String inputjson) {
        this.inputjson = inputjson;
    }

    public Transaction(String recipientAddress, double amount, String uuid, HashMap<String, Object> output,
                       HashMap<String, Object> input)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        super();
        this.uuid = uuid;
        this.output = output;
        this.input = input;
        this.senderWallet = null;
        this.recipientAddress = recipientAddress;
        this.amount = amount;
    }

    public static Transaction createPublicTransactionFromJSON() {
        return null;
    }

    public Transaction() {
    }

    /**
     * Structures output data of wallet - a hashmap of two items, currency going to
     * recipient at address and change going back to sender
     */
    public static HashMap<String, Object> createOutputMap(Wallet senderWallet, String recipientAddress, double amount)
            throws TransactionAmountExceedsBalance {
        if (amount > senderWallet.getBalance()) {
            System.out.println("Amount exceeds balance");
            throw new TransactionAmountExceedsBalance("The transaction amount exceeds the current balance");
        }
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put(recipientAddress, amount);
        output.put(senderWallet.getAddress(), (senderWallet.getBalance() - amount));
        return output;
    }

    /**
     * Structured meta data about transaction, including digitial binding signature
     * of transaction from sender Includes senders public key for verification
     */
    public static HashMap<String, Object> createInputMap(Wallet senderWallet, HashMap<String, Object> output)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException,
            IOException {
        String publicKeyString = Base64.getEncoder().encodeToString(senderWallet.getPublickey().getEncoded());
        byte[] bytesignature = senderWallet.sign(output);
        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("timestamp", new Date().getTime());
        input.put("amount", senderWallet.getBalance());
        input.put("address", senderWallet.getAddress());
        input.put("publicKeyB64", publicKeyString);
        input.put("publicKeyFormat", senderWallet.getPublickey().getFormat());
        input.put("signatureB64", Base64.getEncoder().encodeToString(bytesignature));
        return input;
    }

    /**
     * Update transaction with existing or new recipient
     *
     * @param senderWallet
     * @param recipientAddress
     * @param amount
     * @throws TransactionAmountExceedsBalance
     * @throws IOException
     * @throws SignatureException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void update(Wallet senderWallet, String recipientAddress, double amount)
            throws TransactionAmountExceedsBalance, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException, IOException {
        if (amount > (double) this.output.get(senderWallet.getAddress())) {
            throw new TransactionAmountExceedsBalance(
                    "Transaction amount exceeds existing balance after prior transactions");
        }

        if (this.output.containsKey(recipientAddress)) {
            this.output.put(recipientAddress, (double) this.output.get(recipientAddress) + amount);
        } else {
            this.output.put(recipientAddress, amount);
            this.recipientAddress = "multiple";
        }
        this.output.put(senderWallet.getAddress(), (double) this.output.get(senderWallet.getAddress()) - amount);
        this.amount += amount;
        this.input = this.createInputMap(senderWallet, output);
        this.updateOutputInputJson();
    }

    /**
     * Validate a transaction. For invalid transactions, raises
     * InvalidTransactionException
     *
     * @param transaction
     * @return
     * @throws InvalidTransactionException
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws CertificateException
     * @throws InvalidKeySpecException
     * @throws InvalidAlgorithmParameterException
     */
    public static boolean is_valid_transaction(Transaction transaction) throws InvalidTransactionException,
            InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, IOException,
            CertificateException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        PublicKey wrongPK = Wallet.createWallet("jeremy").getPublickey();
//		PublicKey restoredPK = Wallet.restorePK((byte[]) twrongPK.getInput().get("publicKeyByte")); // THROWS EXPECTED @SIGNATURE NOT VALID!!!
        String signatureString = (String) transaction.getInput().get("signatureString");
        String publicKeyString = (String) transaction.getInput().get("publicKeyB64");
        byte[] signatureByte = Base64.getDecoder().decode(signatureString);
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        PublicKey reconstructedPK = Wallet.restorePublicKey(publicKeyByte);
//		PublicKey restoredPK = Wallet.restorePK((String) transaction.getInput().get("publicKeyB64"));
//		PublicKey originalPK = (PublicKey) transaction.input.get("publicKey");
        double sumOfTransactions = transaction.output.values().stream().mapToDouble(t -> (double) t).sum();
        System.out.println("Sum of values " + sumOfTransactions);
        if (sumOfTransactions != (double) transaction.input.get("amount")) {
            throw new InvalidTransactionException("Value mismatch of propsed transactions");
        }
//		if (!Wallet.verifySignature((byte[]) transaction.input.get("signature"), transaction.output,
//				originalPK)) {
//			System.err.println("Signature not valid!");
//			throw new InvalidTransactionException("Invalid Signature");
//		}
        if (!Wallet.verifySignature(signatureByte, transaction.getOutput(), reconstructedPK)) {
            System.err.println("Signature not valid!");
            throw new InvalidTransactionException("Invalid Signature");
        }
        return true;
    }

    /**
     * HashMap<> Input, Output are not persisted to DB but JSON strings inputJson
     * and outputJson are. This method rebuilds them when called- for instance upon
     * retrieval from DB. It is self inflating, requiring no args, only a GSON
     * dependency.
     */
    public void rebuildOutputInput() {
        if (this.output == null) {
            this.input = new Gson().fromJson(this.getInputjson(), HashMap.class);
            this.output = new Gson().fromJson(this.getOutputjson(), HashMap.class);
        }
    }

    /**
     * For persistence, the output and input maps must be jsonified. As such, when
     * the maps themselves change in memory, they must be themselves updated. Here
     * is a quick and easy method to do so.
     */
    public void updateOutputInputJson() {
        if (this.output != null) {
            this.outputjson = new Gson().toJson(this.output);
            this.inputjson = new Gson().toJson(this.input);
        }
    }

    /**
     * jsonify only input output for blockchain itself. Calls a helper class that
     * strips off extraneous data (easiest cleanest way to implement and maintain
     * existing code
     *
     * @return
     */

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
        if (!Wallet.verifySignature(signatureByte, transaction.getOutput(), reconstructedPK)) {
            System.err.println("Signature not valid!");
            throw new InvalidTransactionException("Invalid Signature");
        }
        return true;
    }

    @Override
    public String toString() {
        return "Transaction [uuid=" + uuid + ", senderWallet=" + senderWallet + ", recipientAddress=" + recipientAddress
                + ", amount=" + amount + ", output=" + output + ", input=" + input + "]";
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setSenderWallet(Wallet senderWallet) {
        this.senderWallet = senderWallet;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setOutput(HashMap<String, Object> output) {
        this.output = output;
    }

    public void setInput(HashMap<String, Object> input) {
        this.input = input;
    }

    public String getUuid() {
        return uuid;
    }

    public Wallet getSenderWallet() {
        return senderWallet;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public double getAmount() {
        return amount;
    }

    public HashMap<String, Object> getOutput() {
        return output;
    }

    public HashMap<String, Object> getInput() {
        return input;
    }

    // DID NOT WORK AND FOUND OTHER METHOD (didn't try super hard but found other
    // method)
//	public static PublicKey restorePublicKeyObj(byte[] pk) throws CertificateException {
//		CertificateFactory f = CertificateFactory.getInstance("X.509");
//		X509Certificate certificate = (X509Certificate) f.generateCertificate(new ByteArrayInputStream(pk));
//		PublicKey publicKey = certificate.getPublicKey();
//
//		return publicKey;
//	}
    // DID NOT WORK AND FOUND OTHER METHOD (didn't try super hard but found other
    // method)
//	public static PublicKey restorePublicKeyObj(String pk) throws CertificateException {
//		CertificateFactory f = CertificateFactory.getInstance("X.509");
//		X509Certificate certificate = (X509Certificate) f
//				.generateCertificate(new ByteArrayInputStream(pk.getBytes(StandardCharsets.UTF_8)));
//		PublicKey publicKey = certificate.getPublicKey();
//		return publicKey;
//	}

    /**
     * Uses GSON library to serialize blockchain chain as json string.
     */
    public String toJSONtheTransaction() {

        HashMap<String, Object> serializeThisBundle = new HashMap<String, Object>();
        HashMap<String, Object> inputClone = (HashMap<String, Object>) input.clone();
        HashMap<String, Object> outputClone = (HashMap<String, Object>) output.clone();
        inputClone.remove("wallet");
        serializeThisBundle.put("input", inputClone);
        serializeThisBundle.put("output", output);
        serializeThisBundle.put("UUID", uuid);
        serializeThisBundle.put("amount", amount);
        serializeThisBundle.put("recipientaddress", recipientAddress);
        serializeThisBundle.put("senderaddress", senderAddress);
        return new Gson().toJson(serializeThisBundle);
    }

    /**
     * Use this jsonifying method to get the final form for mining of transaction.
     *
     * @return
     */
    public String __repr__() {
        if (this.getInput() == null) {
            this.rebuildOutputInput();
        }
        HashMap<String, Object> serializeThisBundle = new HashMap<String, Object>();
        HashMap<String, Object> inputClone = (HashMap<String, Object>) input.clone();
        HashMap<String, Object> outputClone = (HashMap<String, Object>) output.clone();
        inputClone.remove("wallet");
        serializeThisBundle.put("input", inputClone);
        serializeThisBundle.put("output", output);
        serializeThisBundle.put("id", uuid);
        return new Gson().toJson(serializeThisBundle);
    }

    /**
     * Restore a Transaction instance (sans Wallet object) from JSON serialized Data
     * Data you get over the wire/REST API
     *
     * @param transactionJSON
     * @return
     * @throws IOExceptionlinked        tree map to hashmap
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    static public Transaction fromJSONToTransaction(String transactionJSON)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        Map<String, Object> info = new Gson().fromJson(transactionJSON, type);
        System.err.println("Deserializing");
        for (String key : info.keySet()) {
            System.out.println("key: " + key);
            System.out.println("value: " + info.get(key));
        }
        System.out.println(info.getClass());
        System.out.println(info.get("input").getClass());

        LinkedTreeMap inputLTM = (LinkedTreeMap) info.get("input");
        LinkedTreeMap outputLTM = (LinkedTreeMap) info.get("output");
        HashMap<String, Object> input = new HashMap<String, Object>();
        HashMap<String, Object> output = new HashMap<String, Object>();
        // NEEDED BECAUSE GOOGLE GSON IS WIERD- RETURNS LINKEDHASHTREE

        for (Object key : inputLTM.keySet()) {
            input.put((String) key, inputLTM.get(key));
        }
        for (Object key : outputLTM.keySet()) {
            output.put((String) key, outputLTM.get(key));
        }

        String recipientAddress = (String) info.get("address");
        double amount = (double) info.get("amount");
        String uuid = (String) info.get("UUID");
        StringUtils.mapKeyValue(input, "442");
        System.out.println(output);
        Transaction t = new Transaction(recipientAddress, amount, uuid, output, input);
        return t;
    }

    public Transaction fromJSONTheTransaction(String json) {
        return new Gson().fromJson(json, Transaction.class);
    }

    public static Transaction fromJSONTheTransactionStatic(String json) {
        return new Gson().fromJson(json, Transaction.class);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((input == null) ? 0 : input.hashCode());
        result = prime * result + ((output == null) ? 0 : output.hashCode());
        result = prime * result + ((recipientAddress == null) ? 0 : recipientAddress.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Transaction other = (Transaction) obj;
        if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
            return false;
        if (input == null) {
            if (other.input != null)
                return false;
        } else if (!input.equals(other.input))
            return false;
        if (output == null) {
            if (other.output != null)
                return false;
        } else if (!output.equals(other.output))
            return false;
        if (recipientAddress == null) {
            if (other.recipientAddress != null)
                return false;
        } else if (!recipientAddress.equals(other.recipientAddress))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    public static void main(String[] args)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            InvalidKeyException, IOException, SignatureException, TransactionAmountExceedsBalance,
            InvalidTransactionException, CertificateException, InvalidKeySpecException {
        Wallet senderWallet = Wallet.createWallet("bigsender");

        Transaction t1 = new Transaction(senderWallet, "recipientWalletAddress1920", 15);

        System.out.println("Is it valid?");
        System.out.println(Transaction.is_valid_transaction(t1));
        System.err.println("PRINTING t1.TOJSON");
        String jsonified = t1.toJSONtheTransaction();
        System.err.println("Deserializing and restoring ");
        Transaction t1r = Transaction.fromJSONToTransaction(jsonified);
        System.err.println("DO THEY EQUSL");
//		System.out.println(t1r.getSenderWallet().getPublickey().getEncoded());
        System.out.println(t1);
        System.out.println(t1r);
        System.out.println("Is it STILL valid?");
        StringUtils.mapKeyValue(t1r.getInput(), "528");
        System.out.println(Transaction.is_valid_transaction(t1r));
        System.out.println(t1.getAmount());
        System.out.println(t1.recipientAddress);
        System.out.println(t1.getUuid());
        new TransactionService().addTransactionService(t1);
    }

}
