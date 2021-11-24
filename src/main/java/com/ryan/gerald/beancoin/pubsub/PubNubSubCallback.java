package com.ryan.gerald.beancoin.pubsub;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPool;
import com.ryan.gerald.beancoin.exceptions.BlocksInChainInvalidException;
import com.ryan.gerald.beancoin.exceptions.ChainTooShortException;
import com.ryan.gerald.beancoin.exceptions.GenesisBlockInvalidException;
import com.ryan.gerald.beancoin.utilities.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.pubnub.api.PubNub;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.message_actions.PNMessageAction;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.channels.Channels;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;




/**
 * Callback function that in PubNub official is inlined. This listener
 * implements the handling methods for what to do when a message is received.
 * 
 * Can take zero args but be extended to take more args if you want it to have
 * access to other data.
 */
public class PubNubSubCallback extends com.pubnub.api.callbacks.SubscribeCallback {
	Blockchain blockchain;
	BlockchainService blockchainApp = new BlockchainService();
	HashMap<String, String> CHANNELS;
	final TransactionService tService = new TransactionService();
	TransactionPool pool;

	public PubNubSubCallback(Blockchain blockchain, HashMap<String, String> CHANNELS, TransactionPool transactionPool) {
		this.blockchain = blockchain;
		this.CHANNELS = CHANNELS;
	}

	public PubNubSubCallback() {
		this.blockchain = new Blockchain(StringUtils.RandomStringLenN(5));
	}

	@Override
	public void status(PubNub pubnub, PNStatus status) {
		System.out.println("pubnub listener heard something");
		switch (status.getOperation()) {
		// combine unsubscribe and subscribe handling for ease of use
		case PNSubscribeOperation:
		case PNUnsubscribeOperation:
			// Note: subscribe statuses never have traditional errors,
			// just categories to represent different issues or successes
			// that occur as part of subscribe
			switch (status.getCategory()) {
			case PNConnectedCategory:
				// No error or issue whatsoever.
			case PNReconnectedCategory:
				// Subscribe temporarily failed but reconnected.
				// There is no longer any issue.
			case PNDisconnectedCategory:
				// No error in unsubscribing from everything.
			case PNUnexpectedDisconnectCategory:
				// Usually an issue with the internet connection.
				// This is an error: handle appropriately.
			case PNAccessDeniedCategory:
				// PAM does not allow this client to subscribe to this
				// channel and channel group configuration. This is
				// another explicit error.
			default:
				// You can directly specify more errors by creating
				// explicit cases for other error categories of
				// `PNStatusCategory` such as `PNTimeoutCategory` or
				// `PNMalformedFilterExpressionCategory` or
				// `PNDecryptionErrorCategory`.
			}

		case PNHeartbeatOperation:
			// Heartbeat operations can in fact have errors, so it's important to check
			// first for an error.
			// For more information on how to configure heartbeat notifications through the
			// status
			// PNObjectEventListener callback, refer to
			// /docs/sdks/java/android/api-reference/configuration#configuration_basic_usage
			if (status.isError()) {
				// There was an error with the heartbeat operation, handle here
			} else {
				// heartbeat operation was successful
			}
		default: {
			// Encountered unknown status type
		}
		}
	}

	/**
	 * This method is called when a message is heard by listener
	 */
	@Override
	public void message(PubNub pubnub, PNMessageResult message) {

		System.out.println("-- Incoming Transmission -");
		String messagePublisher = message.getPublisher();
		System.out.println("Message publisher: " + messagePublisher);
		System.out.println("Message Payload: " + message.getMessage());
		System.out.println("Message Subscription: " + message.getSubscription());
		System.out.println("Message Channel: " + message.getChannel());
		System.out.println("Message timetoken: " + message.getTimetoken());
		System.out.println("-- End Transmission -");
		System.out.println();
		if (message.getChannel().equals("BLOCK_CHANNEL")) {
//			String block_string = message.getMessage().toString().replaceAll("^\"|\"$|", "").replace("\\", ""); // no longer needed because .getAsString() fixes .toString() error
			String block_string = message.getMessage().getAsString(); // note: toString() would be wrong in this case
																		// (bad formatting)
			ArrayList<Block> potential_chain = new ArrayList<Block>(blockchain.getChain());
			Block deserialized_block = new Gson().fromJson(block_string, Block.class);
			potential_chain.add(deserialized_block);
			try {
				this.blockchain.replace_chain(potential_chain);
				blockchainApp.replaceChainService("beancoin", potential_chain);
				pool = tService.getAllTransactionsAsTransactionPoolService(); // pool up to date each time message comes
				System.out.println("SUCCESSFULLY REPLACED LOCAL CHAIN WITH BROADCAST CHAIN");
				System.out.println("REFRESHING TRANSACTION POOL; SCANNING..\n ELIMINATING PROCESSED TRANSACTIONS");
				pool.refreshBlockchainTransactionPool(blockchain);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (ChainTooShortException e) {
				System.err.println("DID NOT REPLACE CHAIN. CHAIN TOO SHORT");
			} catch (BlocksInChainInvalidException e) {
				System.err.println("DID NOT REPLACE CHAIN. At least one of the blocks in the chain is not valid");
				System.err.println(
						"(Ignore the above if you mined the block yourself. You're just hearing your echo and you've already appended).");
			} catch (GenesisBlockInvalidException e) {
				System.err.println("DID NOT REPLACE CHAIN. Genesis block invalid exception");
			}
		} else if (message.getChannel().equals("TRANSACTION")) {
			String raw_transaction = message.getMessage().getAsString();
			System.out.println("RECEIVED NEW TRANSACTION FROM THE NETWORK");
			System.out.println(raw_transaction);
			try {
				Transaction transactionRestored = Transaction.fromJSONToTransaction(message.getMessage().getAsString());
				System.err.println("New Transaction collected and unpacked from network broadcast PUBNUBSUBCALLBACK160");
				if (new TransactionService().getTransactionService(transactionRestored.getUuid()) != null) {
					new TransactionService().addTransactionService(transactionRestored);
				}

			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
				System.err.println(
						"Received transaction message over network but failed to rebuild transaction in PubNubSubCallback");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void presence(@NotNull PubNub pubnub, @NotNull PNPresenceEventResult presence) {
		System.out.println("Presence Event: " + presence.getEvent());
		// Can be join, leave, state-change or timeout

		System.out.println("Presence Channel: " + presence.getChannel());
		// The channel to which the message was published

		System.out.println("Presence Occupancy: " + presence.getOccupancy());
		// Number of users subscribed to the channel

		System.out.println("Presence State: " + presence.getState());
		// User state

		System.out.println("Presence UUID: " + presence.getUuid());
		// UUID to which this event is related

		presence.getJoin();
		// List of users that have joined the channel (if event is 'interval')

		presence.getLeave();
		// List of users that have left the channel (if event is 'interval')

		presence.getTimeout();
		// List of users that have timed-out off the channel (if event is 'interval')

		presence.getHereNowRefresh();
		// Indicates to the client that it should call 'hereNow()' to get the
		// complete list of users present in the channel.
	}

	// Signals
	@Override
	public void signal(PubNub pubnub, PNSignalResult pnSignalResult) {
		System.out.println("Signal publisher: " + pnSignalResult.getPublisher());
		System.out.println("Signal payload: " + pnSignalResult.getMessage());
		System.out.println("Signal subscription: " + pnSignalResult.getSubscription());
		System.out.println("Signal channel: " + pnSignalResult.getChannel());
		System.out.println("Signal timetoken: " + pnSignalResult.getTimetoken());
	}

	// Message actions
	@Override
	public void messageAction(PubNub pubnub, PNMessageActionResult pnActionResult) {

		PNMessageAction pnMessageAction = pnActionResult.getMessageAction();
		System.out.println("Message action type: " + pnMessageAction.getType());
		System.out.println("Message action value: " + pnMessageAction.getValue());
		System.out.println("Message action uuid: " + pnMessageAction.getUuid());
		System.out.println("Message action actionTimetoken: " + pnMessageAction.getActionTimetoken());
		System.out.println("Message action messageTimetoken: " + pnMessageAction.getMessageTimetoken());
		System.out.println("Message action subscription: " + pnActionResult.getSubscription());
		System.out.println("Message action channel: " + pnActionResult.getChannel());
		System.out.println("Message action timetoken: " + pnActionResult.getTimetoken());
	}

	// files
	@Override
	public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) {
		System.out.println("File channel: " + pnFileEventResult.getChannel());
		System.out.println("File publisher: " + pnFileEventResult.getPublisher());
		System.out.println("File message: " + pnFileEventResult.getMessage());
		System.out.println("File timetoken: " + pnFileEventResult.getTimetoken());
		System.out.println("File file.id: " + pnFileEventResult.getFile().getId());
		System.out.println("File file.name: " + pnFileEventResult.getFile().getName());
		System.out.println("File file.url: " + pnFileEventResult.getFile().getUrl());
	}

	@Override
	public void uuid(@NotNull PubNub pubnub, @NotNull PNUUIDMetadataResult pnUUIDMetadataResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void channel(@NotNull PubNub pubnub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {
		// TODO Auto-generated method stub

	}
}
