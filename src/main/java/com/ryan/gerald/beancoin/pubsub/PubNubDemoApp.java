package com.ryan.gerald.beancoin.pubsub;

import java.util.Collections;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import javax.validation.constraints.NotNull;

/**
 * Pubnub demo code as copied from their website.
 * https://www.pubnub.com/docs/sdks/java
 * 
 * Run it as main.
 */
public class PubNubDemoApp {
	public static void main(String[] args) throws PubNubException {
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSubscribeKey("sub-c-1e6d4f2c-9012-11eb-968e-467c259650fa");
		pnConfiguration.setPublishKey("pub-c-74f31a3f-e3da-4cbe-81a6-02e2ba8744bd");
		pnConfiguration.setUuid("myUniqueUUID");

		PubNub pubnub = new PubNub(pnConfiguration);

		final String channelName = "myChannel";

		// create message payload using Gson
		final JsonObject messageJsonObject = new JsonObject();
		messageJsonObject.addProperty("msg", "Hello World");

		System.out.println("Message to send: " + messageJsonObject.toString());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pubnub.addListener(new SubscribeCallback() {

			@Override
			public void status(PubNub pubnub, PNStatus status) {
				if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
					// This event happens when radio / connectivity is lost
				} else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
					// Connect event. You can do stuff like publish, and know you'll get it.
					// Or just use the connected event to confirm you are subscribed for
					// UI / internal notifications, etc
					if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
						pubnub.publish().channel(channelName).message(messageJsonObject)
								.async((result, publishStatus) -> {
									if (!publishStatus.isError()) {
										// Message successfully published to specified channel.
									}
									// Request processing failed.
									else {
										// Handle message publish error.
										// Check 'category' property to find out
										// issues because of which the request failed.
										// Request can be resent using: [status retry];
									}
								});
					}
				} else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
					// Happens as part of our regular operation. This event happens when
					// radio / connectivity is lost, then regained.
				} else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
					// Handle messsage decryption error. Probably client configured to
					// encrypt messages and on live data feed it received plain text.
				}
			}

			@Override
			public void message(PubNub pubnub, PNMessageResult message) {
				// Handle new message stored in message.message
				if (message.getChannel() != null) {
					// Message has been received on channel group stored in
					// message.getChannel()
				} else {
					// Message has been received on channel stored in
					// message.getSubscription()
				}

				JsonElement receivedMessageObject = message.getMessage();
				System.out.println("Received message: " + receivedMessageObject.toString());
				// extract desired parts of the payload, using Gson
				String msg = message.getMessage().getAsJsonObject().get("msg").getAsString();
				System.out.println("The content of the message is: " + msg);

				/*
				 * Log the following items with your favorite logger - message.getMessage() -
				 * message.getSubscription() - message.getTimetoken()
				 */
			}

			@Override
			public void signal(PubNub pubnub, PNSignalResult pnSignalResult) {

			}

			@Override
			public void messageAction(PubNub pubnub, PNMessageActionResult pnMessageActionResult) {

			}

			@Override
			public void presence(PubNub pubnub, PNPresenceEventResult presence) {

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

			@Override
			public void file(@NotNull PubNub pubnub, @NotNull PNFileEventResult pnFileEventResult) {
				// TODO Auto-generated method stub

			}
		});

		pubnub.subscribe().channels(Collections.singletonList(channelName)).execute();
		pubnub.publish().channel(channelName).message(messageJsonObject).sync();
		System.out.println("Ended");
	}
}
