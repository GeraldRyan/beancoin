package com.ryan.gerald.beancoin.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class CryptoHash {
	static HashMap<Character, String> HEX_TO_BIN_TABLE;
	static {
		HEX_TO_BIN_TABLE = new HashMap<Character, String>();
		HEX_TO_BIN_TABLE.put('0', "0000");
		HEX_TO_BIN_TABLE.put('1', "0001");
		HEX_TO_BIN_TABLE.put('2', "0010");
		HEX_TO_BIN_TABLE.put('3', "0011");
		HEX_TO_BIN_TABLE.put('4', "0100");
		HEX_TO_BIN_TABLE.put('5', "0101");
		HEX_TO_BIN_TABLE.put('6', "0110");
		HEX_TO_BIN_TABLE.put('7', "0111");
		HEX_TO_BIN_TABLE.put('8', "1000");
		HEX_TO_BIN_TABLE.put('9', "1001");
		HEX_TO_BIN_TABLE.put('a', "1010");
		HEX_TO_BIN_TABLE.put('b', "1011");
		HEX_TO_BIN_TABLE.put('c', "1100");
		HEX_TO_BIN_TABLE.put('d', "1101");
		HEX_TO_BIN_TABLE.put('e', "1110");
		HEX_TO_BIN_TABLE.put('f', "1111");
	}

	public static String getSHA256(String... sarray) throws NoSuchAlgorithmException {
		String s = concat(sarray);
//		System.out.printf("Hashing \"%s\"\n", s);
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
		byte[] b = md.digest(s.getBytes(StandardCharsets.UTF_8));
		BigInteger number = new BigInteger(1, b);
		StringBuilder hexString = new StringBuilder(number.toString(16));
		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}
		String mds = hexString.toString();
//		System.out.printf("hash is:\n%s\n", mds);
		return hexString.toString();

	}

	public static String getSHA256(long timestamp, String last_hash, String data, int difficulty, int nonce)
			throws NoSuchAlgorithmException {
		String s = "";

		s += Long.toString(timestamp);
		s += last_hash;
		s += concat(data);
		s += Integer.toString(difficulty);
		s += Integer.toString(nonce);
//		System.out.printf("Hashing \"%s\"\n", s);
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-256");
		byte[] b = md.digest(s.getBytes(StandardCharsets.UTF_8));
		BigInteger number = new BigInteger(1, b);
		StringBuilder hexString = new StringBuilder(number.toString(16));
//		System.out.println(hexString);
		while (hexString.length() < 64) {
			hexString.insert(0, '0');
		}
		String messageDigestString = hexString.toString();
//		System.out.printf("hash is:\n%s\n", messageDigestString);
		return messageDigestString;
	}

	public static char[] n_len_array(char c, int n) {
		char[] ch = new char[n];
		for (int i = 0; i < n; i++) {
			ch[i] = c;
		}
		return ch;
	}

	public static String n_len_string(String s, int n) {
		String string = new String();
		for (int i = 0; i < n; i++) {
			string += s;
		}
		return string;
	}

	public static String n_len_string(char c, int n) {
		String s = "";
		for (int i = 0; i < n; i++) {
			s += c;
		}
		return s;
	}

	public static String concat(String... args) {
		String s = "";
		for (String $ : args) {
			s += $;
		}
//		System.out.println(s);
		return s;
	}

	public static char[] string_to_charray(String str) {
		char[] ch = new char[str.length()];

		for (int i = 0; i < str.length(); i++) {
			ch[i] = str.charAt(i);
		}
		return ch;
	}

	public static String string_to_hex(String arg) {
		return String.format("%064x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
	}

	public static String hex_to_binary(String hex_string) {
		String binary_string = "";
		for (int i = 0; i < hex_string.length(); i++) {
			binary_string += HEX_TO_BIN_TABLE.get(hex_string.charAt(i));
		}
		return binary_string;
	}

	public static String string_to_binary(String raw_string) {
		String hex_string = string_to_hex(raw_string);
		String bin_string = hex_to_binary(hex_string);
		return bin_string;
	}

	public static char[] string_to_binary_chararray(String non_binary_string) {
		String binary_string = string_to_binary(non_binary_string);
		char[] binary_char_array = string_to_charray(binary_string);
		return binary_char_array;
	}

	public static char[] hex_to_binary_chararray(String hex) {
		String binary_string = string_to_binary(hex);
		char[] binary_char_array = string_to_charray(binary_string);
		return binary_char_array;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
//		c.concat("foo","bar", "bat");
		String md = getSHA256("foo", "bar", "bat");
		;
//		System.out.println(hex_to_binary(string_to_hex("foo")));
//		BigInteger b = new BigInteger("255", 10);
//		System.out.println(hex_to_binary("00000000000000000001c58bdb922377644ea0918c60664b2081867ed0b99945"));
//		System.out.println(string_to_binary("foo"));
		System.out.println(CryptoHash.n_len_array('0', 5));
	}

}
