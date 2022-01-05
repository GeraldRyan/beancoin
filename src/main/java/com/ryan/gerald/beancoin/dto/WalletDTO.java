package com.ryan.gerald.beancoin.dto;

public class WalletDTO {
    String address;
    String publicKey;

    final String privateKey = null; // not to send private key over wire.

    // balance to be calc'd on blockchain, not asserted over wire

    // optional ==> no need for username if they're sending over wire to be valid wallet, but worth checking?
    String ownerId;
}
