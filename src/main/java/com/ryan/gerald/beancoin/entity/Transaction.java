package com.ryan.gerald.beancoin.entity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
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

import com.ryan.gerald.beancoin.dto.TransactionDTO;
import com.ryan.gerald.beancoin.exceptions.InvalidTransactionException;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;
import com.ryan.gerald.beancoin.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Document an exchange of currency from a sender to one or more recipients
 */
@Entity
@Table(name = "transaction")
public class Transaction implements TransactionInterface {
    @Id
    String uuid;
    String recipientAddress;
    String senderAddress;
    double amount;
    @Column(columnDefinition = "varchar(2000) default 'Jon Snow'")
    String outputjson;
    @Column(columnDefinition = "varchar(2000) default 'Jon Snow'")
    String inputjson;
    @Transient  // WHAT'S THE POINT OF HAVING THESE MAPS? DECIDE ON STRING FMT OR OTHER TABLE (OR OTHER DB MODEL)
    HashMap<String, Object> output; // recipients (including sender)
    @Transient
    HashMap<String, Object> input; // meta-inputHash about transaction including sender starting balance

    public Transaction() {}

    public Transaction(String uuid, String recipientAddress, String senderAddress, double amount, String outputjson, String inputjson) {
        this.uuid = uuid;
        this.recipientAddress = recipientAddress;
        this.senderAddress = senderAddress;
        this.amount = amount;
        this.outputjson = outputjson;
        this.inputjson = inputjson;
        rebuildOutputInput();
    }

    /**
     * Factory method for creating Transaction given a full fledged wallet
     * @param senderWallet
     * @param recipientAddress
     * @param toAmount
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws TransactionAmountExceedsBalance
     * @throws SignatureException
     */
    public static Transaction createTransactionWithWallet(Wallet senderWallet, String recipientAddress, double toAmount)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, TransactionAmountExceedsBalance, SignatureException {
        if (senderWallet.getBalance() < toAmount) {
            throw new TransactionAmountExceedsBalance("Transaction Amount Exceeds Balance");
        }
        String uuid = StringUtils.getUUID8();
        HashMap<String, Object> output = Transaction.createOutputMap(senderWallet.getAddress(), recipientAddress, senderWallet.getBalance(), toAmount);
        HashMap<String, Object> input = Transaction.createInputMapWithWallet(senderWallet, output);
        String outputjson = new Gson().toJson(output);
        String inputjson = new Gson().toJson(input);
        System.out.println("New transaction made!");
        return new Transaction(uuid, recipientAddress, senderWallet.getAddress(), toAmount, outputjson, inputjson);
    }

    /**
     * API METHOD for POSTing transactions. Input and output JSON strings must conform to spec (or should I persist these objects in separate tables -
     * transaction input hash transaction output hash
     * TODO Test this works and set up API (BIG DAY THAT WILL BE)
     * @param t object in following form {}
     * @return
     * @throws TransactionAmountExceedsBalance
     */
    public static Transaction postTransactionWithPrivateKey(TransactionDTO t)
            throws TransactionAmountExceedsBalance {
        // BAD PRACTICE TO SEND PRIVATE KEY OVER WIRE, BUT WHAT THE HECK. When angular client is up and going, then will have sign functionality
        String uuid = StringUtils.getUUID8();
        String recipientAddress = t.getToAddress();
        String senderAddress = t.getFromAddress();
        String privateKey = t.getPrivatekey();
        double senderBalance = t.getFromBalance();
        double toAmount = t.getToAmount();
        double amount = t.getToAmount();
        HashMap<String, Object> outputMap = createOutputMap(senderAddress, recipientAddress, senderBalance, toAmount);
        HashMap<String, Object> inputMap = createInputMap(senderAddress,senderBalance,t.getPrivatekey(), t.getPublickey(), t.getFormat());
        String outputjson = new Gson().toJson(outputMap);
        String inputjson = new Gson().toJson(inputMap);
        return new Transaction(uuid, recipientAddress, senderAddress, amount, outputjson, inputjson);
    }

    /**
     * API METHOD for POSTing transactions. Input and output JSON strings must conform to spec (or should I persist these objects in separate tables -
     * transaction input hash transaction output hash.
     * This is more like a user directly announces a new transaction. In reality this would go over the wire through a message broker or streaming service. Work for later.
     */
    public static Transaction postPresignedTransaction(String uuid, String recipientAddress, String senderAddress, double amount, String outputjson, String inputjson) {
        return new Transaction(uuid, recipientAddress, senderAddress, amount, outputjson, inputjson);
    }

    public static boolean verifyInputOutputStrings(String in, String out) {
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        Map<String, Object> inputHash = new Gson().fromJson(in, type);  // TODO TEST
        Map<String, Object> outputHash = new Gson().fromJson(in, type);  // TODO TEST
        for (String key : inputHash.keySet()) {
            System.out.println("key: " + key);
            System.out.println("value: " + inputHash.get(key));
        }
        System.out.println(inputHash.getClass());
        if (!inputHash.containsKey("amount")
                && !inputHash.containsKey("address")
                && !inputHash.containsKey("signatureB64")
                && !inputHash.containsKey("publicKeyFormat")
                && !inputHash.containsKey("publicKeyB64")
                && !inputHash.containsKey("timestamp")
                && inputHash.keySet().size() == 6
                && !((long) outputHash.values().stream().reduce(0, (sub, el) -> (long) sub + (long) el) == (long) inputHash.get("amount"))
        ) {
            return false;
        }
        if (!senderHasBalanceOnChain() || !verifySignature()) {
            return false;
        }
        return true;
    }

    // TODO Implement me- oh wait already is in Wallet but moving to Specialty class
    public static boolean verifySignature() {
        return false;
    }

    // TODO Implement me in BalanceCalculator class
    public static boolean senderHasBalanceOnChain() {
        return false;
    }

    /**
     * Structures output data of wallet - a hashmap of two items, currency going to
     * recipient at address and change going back to sender
     */
    public static HashMap<String, Object> createOutputMap(String senderAddress, String recipientAddress, double fromBalance, double toAmount)
            throws TransactionAmountExceedsBalance {
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put(senderAddress, (fromBalance - toAmount));
        output.put(recipientAddress, toAmount);
        return output;
    }

    /**
     * Structured meta data about transaction, including digitial binding signature
     * of transaction from sender Includes senders public key for verification
     */
    public static HashMap<String, Object> createInputMap(String senderAddress,
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

    /**
     * Update transaction with existing or new recipient. A true instance method.
     * TODO create releated sub methods where wallet not provided (as in over wire). Need only match address and other key fields
     */
    public void updateTransaction(Wallet senderWallet, String recipientAddress, double amount)
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
        this.input = this.createInputMapWithWallet(senderWallet, output);
        this.updateOutputInputJson();
    }

    public static HashMap<String, Object> createInputMapWithWallet(Wallet senderWallet, HashMap<String, Object> output) throws NoSuchAlgorithmException, SignatureException, IOException, NoSuchProviderException, InvalidKeyException {
        String signatureB64 = senderWallet.sign(output); // consider signing outputJSON String not bytearray HashMap
        String publicKeyString = Base64.getEncoder().encodeToString(senderWallet.getPublickey().getEncoded());
        String pkFormat = senderWallet.getPublickey().getFormat();
        return Transaction.createInputMap(senderWallet.getAddress(), senderWallet.getBalance(), signatureB64, publicKeyString, pkFormat);
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

    @Override
    public String toString() {
        return "Transaction [uuid=" + uuid + ", recipientAddress=" + recipientAddress
                + ", amount=" + amount + ", output=" + output + ", input=" + input + "]";
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    /**
     * Uses GSON library to serialize blockchain chain as json string.
     * TODO Extract to other class
     */
    public String toJSONtheTransaction() {
        HashMap<String, Object> serializeThisBundle = new HashMap<String, Object>();
        HashMap<String, Object> inputClone = (HashMap<String, Object>) input.clone();
        serializeThisBundle.put("input", inputClone);
        serializeThisBundle.put("output", output);
        serializeThisBundle.put("UUID", uuid);
        serializeThisBundle.put("amount", amount);
        serializeThisBundle.put("recipientaddress", recipientAddress);
        serializeThisBundle.put("senderaddress", senderAddress);
        return new Gson().toJson(serializeThisBundle);
    }

    public static String transactionStringSingleton(Transaction t) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(t.__repr__());
        sb.append("]");
        return sb.toString().replace("\\\\", "");
    }

    /**
     * Use this jsonifying method to get the final form for mining of transaction.
     */
    public String __repr__() {
        if (this.getInput() == null) {
            this.rebuildOutputInput();
        }
        HashMap<String, Object> serializeThisBundle = new HashMap<String, Object>();
        HashMap<String, Object> inputClone = (HashMap<String, Object>) input.clone();
        inputClone.remove("wallet");
        serializeThisBundle.put("input", inputClone);
        serializeThisBundle.put("output", output);
        serializeThisBundle.put("id", uuid);
        return new Gson().toJson(serializeThisBundle);
    }

    // TODO THESE guys are being used but do they need to be? At least rename or review.
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
}
