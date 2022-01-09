package com.ryan.gerald.beancoin.config;

/**
 * Generic Blockchain Config
 *
 */
public class Config {

	double STARTING_BALANCE = 1000;
	public static boolean BROADCAST_TRANSACTIONS = false; // not using
	public static boolean BROADCAST_MINED_BLOCKS = false; // not using
	public static boolean BROADCASTING = false;
	public static boolean LISTENING = false;

	public static long MILLISECONDS = 1;
	public static long SECONDS = 1000 * MILLISECONDS;

	public static boolean DB_DEV = true;

	// 2 SECONDS MINE RATE FOR DEBUG AFTER WHICH IT SHOULD ADJUST DIFFICULTY
	public static long MINE_RATE = 2 * SECONDS;
	// 10 MINUTES OR SOMETHING WOULD BE GOOD FOR PRODUCTION

	public static String REMOTE_NODE_URL = "http://localhost:8080/blocks/blockchain";

	// PUB NUB KEYS
	private static String PUBLISH_KEY = "pub-c-74f31a3f-e3da-4cbe-81a6-02e2ba8744bd";
	private static String SUBSCRIBE_KEY = "sub-c-1e6d4f2c-9012-11eb-968e-467c259650fa";

}
