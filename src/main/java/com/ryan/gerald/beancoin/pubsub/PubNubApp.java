package com.ryan.gerald.beancoin.pubsub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//import org.jetbrains.annotations.NotNull;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPool;
import com.google.gson.JsonElement;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.message_actions.PNMessageAction;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

// TODO ---- TOO MANY DARN VARIETIES OF THE SAME APP. NEED TO PARE DOWN TO ONE AND MAKE THE USER CONFORM TO THE SPEC. 
// FIGURE OUT HOW THIS ALL WORKS BETTER TOO AND MAKE SURE MY CODE IS TIGHT AND THAT IT CAN WORK ON DEPLOY

/**
 * 
 * Listener callback function to work with pubnub API
 * 
 * This class handles the publish/subscribe layer of the application, providing
 * communication between the nodes of the blockchain network.
 */
public class PubNubApp {
	private static String publish_key = "pub-c-74f31a3f-e3da-4cbe-81a6-02e2ba8744bd";
	private static String subscribe_key = "sub-c-1e6d4f2c-9012-11eb-968e-467c259650fa";
	private PubNub pn;
	String TEST_CHANNEL;
	String BLOCK_CHANNEL;
	public HashMap<String, String> CHANNELS;
	public Blockchain blockchain;
	TransactionPool transactionPool;

	/*
	 * Not being used, may want to implement later
	 */
	public PubNubApp(PNConfiguration pnConfiguration, Blockchain blockchain, TransactionPool transactionPool)
			throws InterruptedException {
		this.pn = new PubNub(pnConfiguration);
		this.transactionPool = transactionPool;
		this.TEST_CHANNEL = "TEST_CHANNEL";
		this.BLOCK_CHANNEL = "BLOCK_CHANNEL";
		this.CHANNELS = new HashMap<String, String>();
		this.CHANNELS.put("BLOCK", "BLOCK_CHANNEL");
		this.CHANNELS.put("TEST", "TEST_CHANNEL");
		this.CHANNELS.put("GENERAL", "general");
		this.CHANNELS.put("TRANSACTION", "TRANSACTION");
		this.pn = new PubNub(pnConfiguration);
		this.pn.addListener(new PubNubSubCallback(blockchain, CHANNELS, transactionPool));
		Thread.sleep(1000);
//		this.pn.subscribe().channels(Collections.singletonList("general")).execute();
//		this.pn.subscribe().channels(channels).execute();
		this.pn.subscribe().channels(new ArrayList<String>(CHANNELS.values())).execute();
	}

	public HashMap<String, String> getCHANNELS() {
		return CHANNELS;
	}

	public void setCHANNELS(HashMap<String, String> cHANNELS) {
		CHANNELS = cHANNELS;
	}

	/**
	 * Default constructor. Subscribes to general, TEST_CHANNEL and BLOCK_CHANNEL
	 * channels automatically.
	 */
	public PubNubApp(Blockchain blockchain, TransactionPool transactionPool) throws InterruptedException {
		this.blockchain = blockchain;
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey(subscribe_key);
		pnConfiguration.setPublishKey(publish_key);
		pnConfiguration.setUuid("BeanMaster");
		this.transactionPool = transactionPool;
		TEST_CHANNEL = "TEST_CHANNEL";
		BLOCK_CHANNEL = "BLOCK_CHANNEL";
		ArrayList<String> channels = new ArrayList();
		channels.add(TEST_CHANNEL);
		channels.add(BLOCK_CHANNEL);
		channels.add("general");
		CHANNELS = new HashMap<String, String>();
		CHANNELS.put("BLOCK", "BLOCK_CHANNEL");
		CHANNELS.put("TEST", "TEST_CHANNEL");
		CHANNELS.put("GENERAL", "general");
		CHANNELS.put("TRANSACTION", "TRANSACTION");
		this.pn = new PubNub(pnConfiguration);
		this.pn.addListener(new PubNubSubCallback(blockchain, CHANNELS, transactionPool));
		Thread.sleep(1000);
//		this.pn.subscribe().channels(Collections.singletonList("general")).execute();
//		this.pn.subscribe().channels(channels).execute();
		this.pn.subscribe().channels(new ArrayList<String>(CHANNELS.values())).execute();
	}

	public PubNubApp() throws InterruptedException {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey(subscribe_key);
		pnConfiguration.setPublishKey(publish_key);
		pnConfiguration.setUuid("BEANMASTER"); // unique UUID
		this.transactionPool = transactionPool;
		TEST_CHANNEL = "TEST_CHANNEL";
		BLOCK_CHANNEL = "BLOCK_CHANNEL";
		ArrayList<String> channels = new ArrayList();
		channels.add(TEST_CHANNEL); // duplicate functionality
		channels.add(BLOCK_CHANNEL);
		channels.add("general");
		CHANNELS = new HashMap<String, String>();
		CHANNELS.put("BLOCK", "BLOCK_CHANNEL");
		CHANNELS.put("TEST", "TEST_CHANNEL");
		CHANNELS.put("GENERAL", "general");
		CHANNELS.put("TRANSACTION", "TRANSACTION");
		this.pn = new PubNub(pnConfiguration);
		this.pn.addListener(new PubNubSubCallback());
		Thread.sleep(1000);
//		this.pn.subscribe().channels(Collections.singletonList("general")).execute();
//		this.pn.subscribe().channels(channels).execute();
		this.pn.subscribe().channels(new ArrayList<String>(CHANNELS.values())).execute();
	}

	/**
	 * Publish to a channel a given message
	 * 
	 * @param channel
	 * @param message
	 * @throws PubNubException
	 */
	public void publish(String channel, String[] message) throws PubNubException {
		this.pn.publish().channel(channel).message(message).sync();
		// delete this version when safe. We only need String or at least Object
	}

	public void publish(String channel, Object message) throws PubNubException {
		this.pn.publish().channel(channel).message(message).sync();
	}

	/**
	 * Subscribe to pubnub channel, e.g. BLOCK_CHANNEL or "TRANSACTION"
	 * 
	 * @param channel
	 */
	public void subscribe(String channel) {
		this.pn.subscribe().channels(Collections.singletonList(channel)).execute();
	}

	public void subscribe(List<String> channels) {
		this.pn.subscribe().channels(channels).execute();
	}

	/**
	 * Unsubscribe to a channel
	 * 
	 * @param channel
	 */
	public void unsubscribe(String channel) {
		this.pn.unsubscribe().channels(Collections.singletonList(channel)).execute();
	}

	public void unsubscribeAll(List<String> channels) {
		this.pn.unsubscribe().channels(channels).execute();
	}

	public static PubNub createConfiguredPubNubInstance() {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey(subscribe_key);
		pnConfiguration.setPublishKey(publish_key);
		pnConfiguration.setUuid("BeanMaster"); // unique UUID
		return new PubNub(pnConfiguration);

	}

	public static void publishMessageToChannel(Object message, String channel) throws PubNubException {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey(subscribe_key);
		pnConfiguration.setPublishKey(publish_key);
		pnConfiguration.setUuid("BEANMASTER"); // unique UUID

		PubNub pubnub = new PubNub(pnConfiguration);

		String TEST_CHANNEL = "TEST_CHANNEL";
		String BLOCK_CHANNEL = "BLOCK_CHANNEL";
		pubnub.addListener(new PubNubSubCallback());
		pubnub.subscribe().channels(Collections.singletonList(channel));
		pubnub.publish().channel(channel).message(message).sync();
	}

	/**
	 * Broadcasts a block object to all nodes
	 */
	public void broadcastBlock(Block block) throws PubNubException {
		this.publish(this.CHANNELS.get("BLOCK"), block.toJSONtheBlock());
	}

	/**
	 * Broadcasts transaction to the network
	 * 
	 * @throws PubNubException
	 */
	public void broadcastTransaction(Transaction transaction) throws PubNubException {
		this.publish(this.CHANNELS.get("TRANSACTION"), transaction.toJSONtheTransaction());
	}

	public static void main(String[] args) throws PubNubException, InterruptedException {
		System.out.println("Running pubnub main");
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey(subscribe_key);
		pnConfiguration.setPublishKey(publish_key);
		pnConfiguration.setUuid("BEANMASTER"); // unique UUID

		PubNub pubnub = new PubNub(pnConfiguration);

		final String TEST_CHANNEL = "TEST_CHANNEL LynyrdSkynder";

		pubnub.addListener(new PubNubSubCallback());

		pubnub.subscribe().channels(Collections.singletonList(TEST_CHANNEL)).execute();
		Thread.sleep(1000);

		pubnub.publish().channel(TEST_CHANNEL).message(new String("Test message led zeppelin")).sync();
		System.out.println("End run");

	}
	
}




