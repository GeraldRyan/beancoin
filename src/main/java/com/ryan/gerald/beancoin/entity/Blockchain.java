package com.ryan.gerald.beancoin.entity;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.exceptions.BlocksInChainInvalidException;
import com.ryan.gerald.beancoin.exceptions.ChainTooShortException;
import com.ryan.gerald.beancoin.exceptions.GenesisBlockInvalidException;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "blockchain")
public class Blockchain {
	@Id
	String name;
	long date_created;
	long date_last_modified;
	int length_of_chain;
	@OneToMany(targetEntity = Block.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinTable(name="blockchain_blocks")
	List<Block> chain;

	public Blockchain() {}

	public Blockchain(String name) {
		this.name = name;
		this.date_created = new Date().getTime();
		this.chain = new ArrayList<Block>();
		this.chain.add(Block.genesis_block());
		this.length_of_chain = 1;
	}


	public static Blockchain createBlockchainInstance(String name) {return new Blockchain(name);}

	/**
	 * Adds block to blockchain by calling block class's static mine_block method.
	 * This ensures block is valid in itself, and is attached to end of local chain,
	 * ensuring chain is valid.
	 * @param transactionData
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public Block add_block(String transactionData) throws NoSuchAlgorithmException {
		Block new_block = Block.mine_block(this.chain.get(this.chain.size() - 1), transactionData);
		this.chain.add(new_block);
		this.length_of_chain++;
		this.date_last_modified = new Date().getTime();
		return new_block;
	}

	/**
	 * Replace the local chain with the incoming chain if the following apply: - the
	 * incoming chain is longer than the local one - the incoming chain is formatted
	 * properly
	 * 
	 * @param
	 * @throws NoSuchAlgorithmException
	 * @throws ChainTooShortException
	 * @throws BlocksInChainInvalidException
	 * @throws GenesisBlockInvalidException
	 */
	public void replace_chain(Blockchain other_blockchain) throws NoSuchAlgorithmException, ChainTooShortException,
			GenesisBlockInvalidException, BlocksInChainInvalidException {
		if (other_blockchain.chain.size() <= this.chain.size()) {
			throw new ChainTooShortException("Chain too short to replace");
		}
		if (!Blockchain.is_valid_chain(other_blockchain)) {
			System.out.println("Cannot replace chain. The incoming chain is invalid");
			return;
		}

		try {
			Blockchain.is_valid_chain(other_blockchain);
			this.chain = other_blockchain.chain;
			this.length_of_chain = other_blockchain.length_of_chain;
			this.date_last_modified = new Date().getTime();
			System.out.println("Chain replaced with valid longer chain");
		} catch (GenesisBlockInvalidException e) {
			System.out.println(e);
		}
	}

	/**
	 * 
	 * Orders chain of Blocks by timestamp, restoring original order if it had
	 * gotten out of order somehow.
	 * 
	 * 
	 * If ArrayList<Block> is not in order, it's a broken blockchain breaking
	 * certain methods like replace chain, which causes a failed validation.
	 */
	public static void restoreChainOrderByTimestamp(Blockchain blockchain) {
		blockchain.getChain().sort((b1, b2) -> (int) (b1.getTimestamp() - b2.getTimestamp()));

	}

	/**
	 * Replace the local chain with the incoming chain if the following apply: - the
	 * incoming chain is longer than the local one - the incoming chain is formatted
	 * properly
	 * 
	 * @param
	 * @throws NoSuchAlgorithmException
	 * @throws ChainTooShortException
	 * @throws BlocksInChainInvalidException
	 * @throws GenesisBlockInvalidException
	 */
	public void replace_chain(List<Block> other_chain) throws NoSuchAlgorithmException, ChainTooShortException,
			GenesisBlockInvalidException, BlocksInChainInvalidException {
		System.out.println(other_chain.size() + " " + this.chain.size());
		if (other_chain.size() <= this.chain.size()) {
			throw new ChainTooShortException("Chain too short to replace");
		}
		if (!Blockchain.is_valid_chain(other_chain)) {
			System.out.println("Cannot replace chain. The incoming chain is invalid");
			return;
		}

		try {
			Blockchain.is_valid_chain(other_chain);
			this.chain = other_chain;
			this.length_of_chain = other_chain.size();
			this.date_last_modified = new Date().getTime();
			System.out.println("Chain replaced with valid longer chain");
		} catch (GenesisBlockInvalidException e) {
			System.out.println(e);
		} catch (BlocksInChainInvalidException e) {
			System.out.println(e);
		}
	}

	/**
	 * Checker method as to whether the chain will be replace, in case it is
	 * required for lifecycle actions. In practice, this is used for flushing the
	 * database's join table for JPA in the @OneToMany entity, which itself is due
	 * to ignorance about how to do it properly. New data doesn't strictly displace
	 * old entries and duplicate key errors crash the process. So this is a method
	 * that enables a temporary hack.
	 * 
	 * @param other_chain
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GenesisBlockInvalidException
	 * @throws BlocksInChainInvalidException
	 */
	public boolean willReplace(List<Block> other_chain)
			throws NoSuchAlgorithmException, GenesisBlockInvalidException, BlocksInChainInvalidException {
		System.out.println(other_chain.size() + " " + this.chain.size());
		if (other_chain.size() <= this.chain.size()) {
			return false;
		}
		if (!Blockchain.is_valid_chain(other_chain)) {
			return false;
		}
		return true;
	}

	public void setChain(List<Block> chain) {
		this.chain = chain;
	}

	/**
	 * Validate the incoming chain. Enforce the following rules: -
	 * 1. the chain must start with the genesis block
	 * 2. blocks must be formatted correctly
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GenesisBlockInvalidException
	 * @throws BlocksInChainInvalidException
	 */
	public static boolean is_valid_chain(Blockchain blockchain)
			throws NoSuchAlgorithmException, GenesisBlockInvalidException, BlocksInChainInvalidException {
		if (!blockchain.chain.get(0).equals(Block.genesis_block())) {
			System.out.println("THE GENESIS BLOCK MUST BE VALID");
			throw new GenesisBlockInvalidException("GENESIS BLOCK IS INVALID");
		}
		for (int i = 1; i < blockchain.chain.size(); i++) {
			Block current_block = blockchain.chain.get(i);
			Block last_block = blockchain.chain.get(i - 1);
			if (!Block.is_valid_block(last_block, current_block)) {
				System.out.println("AT lEAST ONE OF THE BLOCKS IN THE CHAIN IS NOT VALID!");
				throw new BlocksInChainInvalidException("At least one of the blocks in the chain is not valid");
			}
		}
		return true;
	}

	/**
	 * Validate the incoming chain. Enforce the following rules: - the chain must
	 * start with the genesis block - blocks must be formatted correctly
	 * 
	 * @param
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GenesisBlockInvalidException
	 * @throws BlocksInChainInvalidException
	 */
	public static boolean is_valid_chain(List<Block> other_chain)
			throws NoSuchAlgorithmException, GenesisBlockInvalidException, BlocksInChainInvalidException {
		if (!other_chain.get(0).equals(Block.genesis_block())) {
			System.out.println("The genesis block must be valid");
			throw new GenesisBlockInvalidException("Genesis Block is invalid");
		}
		for (int i = 1; i < other_chain.size(); i++) {
			Block current_block = other_chain.get(i);
			Block last_block = other_chain.get(i - 1);
			if (!Block.is_valid_block(last_block, current_block)) {
				throw new BlocksInChainInvalidException("At least one of the blocks in the chain is not valid");
			}
		}
		return true;
	}

	/**
	 * returns headerless console customized string output for use with an existing
	 * header
	 * 
	 * @return
	 */
	public String toStringConsole() {
		return String.format("%5s %15s %15s %15s", name, date_created, date_last_modified,
				length_of_chain, "length", "content");
	}

	public String toStringMeta() {
		return String.format(
				"Blockchain Metadata: Name: %s date_created: %s date_modified: %s length of chain: %s",
				name, date_created, date_last_modified, length_of_chain);
	}

	public String toStringBroadcastChain() {
		String string_to_return = "";
		for (Block b : chain) {
			string_to_return += b.toStringWebAPI();
		}
		return string_to_return;
	}

	/**
	 * Uses GSON library to serialize blockchain chain as json string.
	 */
	public String toJSONtheChain() {
		return new Gson().toJson(chain);
	}

	public ArrayList fromJSONtheChain(String json) {
		return new Gson().fromJson(json, ArrayList.class);
	}

	/**
	 * Helper method for getting last block (peeking)
	 */
	public Block getLastBlock() {
		return this.getChain().get(getLength_of_chain() - 1);
	}

	public Block getNthBlock(int n) {
		int last_index = this.getLength_of_chain() - 1;
		if (n > last_index) {
			return this.getChain().get(last_index);
		} else if (n < 0) {
			if (n * -1 > last_index) {
				return this.getChain().get(0);
			} else {
				return this.getChain().get(getLength_of_chain() + n);
			}
		} else {
			return this.getChain().get(n);
		}
	}

	public String getName() {
		return name;
	}

	public long getDate_created() {
		return date_created;
	}

	public long getDate_last_modified() {
		return date_last_modified;
	}

	public int getLength_of_chain() {
		return length_of_chain;
	}

	public List<Block> getChain() {
		return chain;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {

	}

}
