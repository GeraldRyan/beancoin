package com.ryan.gerald.beancoin.entity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import com.ryan.gerald.beancoin.exception.TransactionAmountExceedsBalance;
import com.ryan.gerald.beancoin.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Document an exchange of currency from a sender to one or more recipients
 */
@Entity
@Table(name="unmined_transactions")
public class Transaction extends AbstractTransaction implements TransactionInterface {
    @Id String uuid;
    String recipientAddress;
    String senderAddress;
    double amount;
    @Column(columnDefinition = "varchar(2000) default 'Jon Snow'") String output;
    @Column(columnDefinition = "varchar(2000) default 'Jon Snow'") String input;
    @Transient HashMap<String, Object> outputMap;
    @Transient HashMap<String, Object> inputMap;

    public Transaction() {}

    private Transaction(String toAddress, double toAmount, String fromAddress, String outputJson, String inputJson) {
        this.uuid = StringUtils.getUUID8();
        this.recipientAddress = toAddress;
        this.amount = toAmount;
        this.senderAddress = fromAddress;
        this.output = outputJson;
        this.input = inputJson;
        reinflateInputOutputMaps();
    }

    public static Transaction createTransaction(String toAddress, double toAmount, String fromAddress, double fromBalance, HashMap<String, Object> outputMap, String signatureB64, String publicKeyB64, String format) {
        if (toAmount > fromBalance) {
            System.err.println("Amount exceeds balance");
            return null;
        }
        HashMap<String, Object> inputMap = Transaction.createInputMap(fromAddress, fromBalance, signatureB64, publicKeyB64, format);
        String outputJson = new Gson().toJson(outputMap);
        String inputJson = new Gson().toJson(inputMap);
        System.out.println("New transaction made!");
        return new Transaction(toAddress, toAmount, fromAddress, outputJson, inputJson);
    }

    /**
     * API METHOD for POSTing transactions. Input and output JSON strings must conform to spec (or should I persist these objects in separate tables -
     * transaction input hash transaction output hash
     * TODO Test this works and set up API (BIG DAY THAT WILL BE)
     * @param t object in following form {}
     * @return
     * @throws TransactionAmountExceedsBalance
     */
//    public static Transaction postTransactionWithPrivateKey(TransactionDTO t)
//            throws TransactionAmountExceedsBalance {
//        // BAD PRACTICE TO SEND PRIVATE KEY OVER WIRE, BUT WHAT THE HECK. When angular client is up and going, then will have sign functionality
//        String recipientAddress = t.getToAddress();
//        String senderAddress = t.getFromAddress();
//        String privateKey = t.getPrivatekey();
//        double senderBalance = t.getFromBalance();
//        double toAmount = t.getToAmount();
//        double amount = t.getToAmount();
//        HashMap<String, Object> outputMap = this.createOutputMap(senderAddress, recipientAddress, senderBalance, toAmount);
//        HashMap<String, Object> inputMap = this.createInputMap(senderAddress,senderBalance,t.getPrivatekey(), t.getPublickey(), t.getFormat());
//        String outputjson = new Gson().toJson(outputMap);
//        String inputjson = new Gson().toJson(inputMap);
//        return new Transaction(recipientAddress, senderAddress, amount, outputjson, inputjson);
//    }

    /**
     * API METHOD for POSTing transactions. Input and output JSON strings must conform to spec (or should I persist these objects in separate tables -
     * transaction input hash transaction output hash.
     * This is more like a user directly announces a new transaction. In reality this would go over the wire through a message broker or streaming service. Work for later.
     */
//    public static Transaction postPresignedTransaction(String uuid, String recipientAddress, String senderAddress, double amount, String outputjson, String inputjson) {
//        return new Transaction(recipientAddress, senderAddress, amount, outputjson, inputjson);
//    }
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
     * Do we even need these in state?
     */
    public void reinflateInputOutputMaps() {
        if (this.outputMap == null) {
            this.inputMap = this.deserializeInputJson(this.getInput());
            this.outputMap = this.deserializeOutputJson(this.getOutput());
        }
    }

    /**
     * For persistence, the output and input maps must be jsonified. As such, when
     * the maps themselves change in memory, they must be themselves updated. Here
     * is a quick and easy method to do so.
     */
    public void serializeInputOutputMaps() {
        if (this.outputMap != null) {
            this.output = new Gson().toJson(this.outputMap);
            this.input = new Gson().toJson(this.inputMap);
        }
    }

    @Override
    public String toString() {
        return "Transaction [uuid=" + uuid + ", recipientAddress=" + recipientAddress
                + ", amount=" + amount + ", output=" + outputMap + ", input=" + inputMap + "]";
    }

    /**
     * Use this jsonifying method to get the final form for mining of transaction.
     */
    public String serialize() {
        if (this.getInputMap() == null) {this.reinflateInputOutputMaps();}
        HashMap<String, Object> serializableMap = new HashMap<String, Object>();
        serializableMap.put("input", this.inputMap);
        serializableMap.put("output", this.outputMap);
        serializableMap.put("id", this.uuid);
        return new Gson().toJson(serializableMap);
    }

    public Transaction deserialize(String s) {
        return new Gson().fromJson(s, Transaction.class);
    }

    public static String transactionStringSingleton(Transaction t) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(t.serialize());
        sb.append("]");
        return sb.toString().replace("\\\\", "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((inputMap == null) ? 0 : inputMap.hashCode());
        result = prime * result + ((outputMap == null) ? 0 : outputMap.hashCode());
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
        if (inputMap == null) {
            if (other.inputMap != null)
                return false;
        } else if (!inputMap.equals(other.inputMap))
            return false;
        if (outputMap == null) {
            if (other.outputMap != null)
                return false;
        } else if (!outputMap.equals(other.outputMap))
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

    public void setUuid(String uuid) {this.uuid = uuid;}

    public void setRecipientAddress(String recipientAddress) {this.recipientAddress = recipientAddress;}

    public void setAmount(double amount) {this.amount = amount;}

    public void setOutputMap(HashMap<String, Object> outputMap) {this.outputMap = outputMap;}

    public void setInputMap(HashMap<String, Object> inputMap) {this.inputMap = inputMap;}

    public String getUuid() {return uuid;}

    public String getRecipientAddress() {return recipientAddress;}

    public double getAmount() {return amount;}

    public HashMap<String, Object> getOutputMap() {return outputMap;}

    public HashMap<String, Object> getInputMap() {return inputMap;}

    public String getSenderAddress() {return senderAddress;}

    public void setSenderAddress(String senderAddress) {this.senderAddress = senderAddress;}

    public String getOutput() {return output;}

    public void setOutput(String output) {this.output = output;}

    public String getInput() {return input;}

    public void setInput(String input) {this.input = input;}
}