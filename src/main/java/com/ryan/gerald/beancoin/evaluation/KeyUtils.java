package com.ryan.gerald.beancoin.evaluation;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtils {
    String PROVIDER = "SunEC";
    String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    String KEYPAIR_GEN_ALGORITHM = "EC";
    String PARAMETER_SPEC = "secp256k1";

    public KeyUtils() {}

    public PublicKey getPublicKeyObj(String keyString)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        return getPublicKeyObj(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public PublicKey getPublicKeyObj(byte[] keyBytes)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(KEYPAIR_GEN_ALGORITHM, PROVIDER);
        KeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(keySpec);
    }



}
