package com.ryan.gerald.beancoin.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ryan.gerald.beancoin.utils.CryptoHash;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity
@Repository
public class Block {

    private static Gson gson = new Gson();

    @Id String hash;
    private String lastHash;
    private long timestamp;
    private Integer height;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String tx; // deserializes to List<Transaction> tx ----- only for you SQL
    int difficulty;
    int nonce;


    static final long MILLISECONDS = 1;
    static final long SECONDS = 1000 * MILLISECONDS;
    static final long MINE_RATE = 2 * SECONDS;

    public static final HashMap<String, Object> GENESIS_DATA = new HashMap<String, Object>();

    static {
        String mintLoader = "[{\"uuid\":\"mintloader\",\"amount\":0.0,\"output\":\"{\\\"777mint777\\\":5000000.0}\",\"outputMap\":{\"777mint777\":5000000.0}}]";
        GENESIS_DATA.put("hash", "genesis_hash");
        GENESIS_DATA.put("last_hash", "genesis_last_hash");
        GENESIS_DATA.put("timestamp", (long) 1);
        GENESIS_DATA.put("height", 0);
        GENESIS_DATA.put("tx", mintLoader);
        GENESIS_DATA.put("difficulty", 7);
        GENESIS_DATA.put("nonce", 1);
    }

    public Block() {}

    /**
     * A block is a unit of storage for a blockchain that supports a cryptocurrency.
     */
    private Block(String hash, String lastHash, long timestamp, int height, int difficulty, int nonce, String tx) {
        super();
        this.hash = hash;
        this.lastHash = lastHash;
        this.timestamp = timestamp;
        this.height = height;
        this.tx = tx;
        this.difficulty = difficulty;
        this.nonce = nonce;
    }

    /**
     * Generates Genesis block with hard coded transaction data (tx) that will be identical for all
     * instances of blockchain
     */
    public static Block genesis_block() {
        return new Block((String) GENESIS_DATA.get("hash"), (String) GENESIS_DATA.get("last_hash"), (Long) GENESIS_DATA.get("timestamp"), (Integer) GENESIS_DATA.get("height"), (Integer) GENESIS_DATA.get("difficulty"), (Integer) GENESIS_DATA.get("nonce"), (String) GENESIS_DATA.get("tx"));
    }

    /**
     * Mine a block based on given last block and tx until a block hash is found
     * that meets the leading 0's Proof of Work requirement.
     */
    public static Block mine_block(Block last_block, String txArrJson) throws NoSuchAlgorithmException {
        String last_hash = last_block.getHash();
        Integer height = last_block.getHeight() + 1;
        long timestamp = new Date().getTime();
        int difficulty = Block.adjust_difficulty(last_block, timestamp);
        int nonce = 0;
        String hash = CryptoHash.getSHA256(timestamp, last_block.getHash(), txArrJson, difficulty, nonce);
        String proof_of_work = CryptoHash.n_len_string('0', difficulty);
        String binary_hash = CryptoHash.hex_to_binary(hash);
        String binary_hash_work_end = binary_hash.substring(0, difficulty);
        System.out.println("DIFFICULTY: " + difficulty);
        System.out.println("MINING BLOCK..");
        while (!proof_of_work.equalsIgnoreCase(binary_hash_work_end)) {
            nonce += 1;
            timestamp = new Date().getTime();
            difficulty = Block.adjust_difficulty(last_block, timestamp);
            hash = CryptoHash.getSHA256(timestamp, last_block.getHash(), txArrJson, difficulty, nonce);
            proof_of_work = CryptoHash.n_len_string('0', difficulty);
            binary_hash = CryptoHash.hex_to_binary(hash);
            binary_hash_work_end = binary_hash.substring(0, difficulty);
        }
        System.out.println("Solved at Difficulty: " + difficulty);
        System.out.println("Proof of work requirement " + proof_of_work);
        System.out.println("binary_Hash_work_end " + binary_hash_work_end);
        System.out.println("binary hash " + binary_hash);
        System.out.println("BLOCK MINED!");
        return new Block(hash, last_hash, timestamp, height, difficulty, nonce, txArrJson);
    }

    /**
     * Calculate the adjusted difficulty according to the MINE_RATE. Increase the
     * difficulty for quickly mined blocks. Decrease the difficulty for slowly mined
     * blocks.
     */
    public static int adjust_difficulty(Block last_block, long new_timestamp) {
        long time_diff = new_timestamp - last_block.getTimestamp();
        if (time_diff < MINE_RATE) {
            return last_block.getDifficulty() + 1;
        } else if (last_block.getDifficulty() - 1 > 0) {
            return last_block.getDifficulty() - 1;
        } else {
            return 1; // floor
        }
    }

    /**
     * Validate block by enforcing following rules: - Block must have the proper
     * last_hash reference - Block must meet the proof of work requirements -
     * difficulty must only adjust by one - block hash must be a valid combination
     * of block fields
     */
    public static boolean is_valid_block(Block last_block, Block block) throws NoSuchAlgorithmException {
        String binary_hash = CryptoHash.hex_to_binary(block.getHash());
        char[] pow_array = CryptoHash.n_len_array('0', block.getDifficulty());
        char[] binary_char_array = CryptoHash.string_to_charray(binary_hash);
        if (!block.getLastHash().equalsIgnoreCase(last_block.getHash())) {
            System.out.println("The last hash must be correct");
            return false;
//			throw new InvalidLastHashException("LAST HASH MUCH BE CORRECT");
        }
        if (!Arrays.equals(pow_array, Arrays.copyOfRange(binary_char_array, 0, block.getDifficulty()))) {
            System.out.println("Proof of work requirement not met");
            return false;
            // throw exception - proof of work requirement not met
        }
        if (Math.abs(last_block.difficulty - block.difficulty) > 1) {
            System.out.println("Block difficulty must adjust by one");
            return false;
            // throw exception: The block difficulty must only adjust by 1
        }
        String reconstructed_hash = CryptoHash.getSHA256(block.getTimestamp(), block.getLastHash(), block.getTx(),
                block.getDifficulty(), block.getNonce());
        if (!block.getHash().equalsIgnoreCase(reconstructed_hash)) {
            System.out.println("The block hash must be correct");
            System.out.println(block.getHash());
            System.out.println(reconstructed_hash);
            return false;
            // throw exception: the block hash must be correct
        }
        return true;
    }

    /**
     * Prints a header-conforming console adapted output string in the form
     *
     * @return
     */
    public String toStringConsole() {
        return "\n-----------BLOCK--------\ntimestamp: " + this.timestamp + "\nlastHash: " + this.lastHash + "\nhash: "
                + this.hash + "\ndifficulty: " + this.getDifficulty() + "\nData: " + this.tx + "\nNonce: "
                + this.nonce + "\n-----------------------\n";
    }

    public String toStringWebAPI() {
        return String.format("timestamp: %s, lastHash:%s, hash:%s, tx:[%s], difficulty:%s", timestamp, lastHash, hash,
                tx, difficulty, nonce);
    }

    public String toStringFormatted() {
        String txstring = "";
        return String.format("%5s %10s %15s %15s %15s", timestamp, lastHash, hash, tx, difficulty, nonce);
    }

    /**
     * This utility serializer function helps deal with the complexities of Gson and
     * escape characters for different types of object and list serialization.
     * Understand its role by how it is used in this app.
     */
    public String serialize(List<Transaction> txList) {
        HashMap<String, Object> serializableMap = new HashMap<String, Object>();
        serializableMap.put("timestamp", this.timestamp);
        serializableMap.put("hash", this.hash);
        serializableMap.put("lasthash", this.lastHash);
        serializableMap.put("difficulty", this.difficulty);
        serializableMap.put("nonce", this.nonce);
        serializableMap.put("tx", txList);
        return gson.toJson(serializableMap);
    }

    /**
     * convert the block to a serialized JSON representation
     */
    public String serialize() {
        return gson.toJson(this);
    }

    public List<Transaction> deserializeTx() {
        java.lang.reflect.Type type = new TypeToken<List<Transaction>>() {
        }.getType();
        List<Transaction> txList = gson.fromJson(this.getTx(), type);
        System.out.println(txList.size());
        txList.forEach(t -> System.out.println(t.toString()));
        return txList;
    }

    /**
     * Deserialize a valid JSON string with GSON and convert it back into valid
     * block
     */
    public static Block fromJsonToBlock(String jsonel) {
        Block block_restored = gson.fromJson(jsonel, Block.class);
        return block_restored;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getLastHash() {
        return lastHash;
    }

    public String getTx() {return tx;}

    public int getNonce() {
        return nonce;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setLastHash(String lastHash) {
        this.lastHash = lastHash;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tx == null) ? 0 : tx.hashCode());
        result = prime * result + difficulty;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((lastHash == null) ? 0 : lastHash.hashCode());
        result = prime * result + nonce;
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
        Block other = (Block) obj;
        if (tx == null) {
            if (other.tx != null)
                return false;
        } else if (!tx.equals(other.tx))
            return false;
        if (difficulty != other.difficulty)
            return false;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;
        if (lastHash == null) {
            if (other.lastHash != null)
                return false;
        } else if (!lastHash.equals(other.lastHash))
            return false;
        if (nonce != other.nonce)
            return false;
        if (timestamp != other.timestamp)
            return false;
        return true;
    }
}
