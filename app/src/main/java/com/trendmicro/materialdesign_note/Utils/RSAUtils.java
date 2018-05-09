package com.trendmicro.materialdesign_note.Utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;


public class RSAUtils {
    private String String_publicKey, String_privateKey;
    private Cipher cipher;
    private PublicKey Key1;
    private PrivateKey Key2;

    /**
     * 得到公钥
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        //      keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Base64Utils.decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        //       keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Base64Utils.decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * key的格式转换(key->string)
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        //        String s = (new BASE64Encoder()).encode(keyBytes);
        String s = Base64Utils.encode(keyBytes);
        return s;
    }

    /**
     * 生成公私钥
     */

    public static String CreateKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        //密钥位数
        keyPairGen.initialize(1024);
        //密钥对
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String publicKeyString = getKeyString(publicKey);

        String privateKeyString = getKeyString(privateKey);

        String s = publicKeyString + "#" + privateKeyString;

        return s;
    }

    /**
     * 得到公私钥
     */
    public void GetKey() throws Exception {

        String s = RSAUtils.CreateKey();
        String[] key = s.split("#");
        String_publicKey = key[0];
        String_privateKey = key[1];
        cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        //通过密钥字符串得到密钥
        Key1 = RSAUtils.getPublicKey(String_publicKey);
        Key2 = RSAUtils.getPrivateKey(String_privateKey);
    }

    /**
     * 公钥加密
     */
    public static String encryptData(String data, PublicKey key) throws Exception {
        byte[] plainText = data.getBytes();
        //加密
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] enBytes = cipher.doFinal(plainText);
        String afterencrypt = Base64Utils.encode(enBytes);
        return afterencrypt;
    }

    /**
     * 私钥解密
     */
    public static String decryptData(String encryptContent, PrivateKey key) throws Exception {
        //解密
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] midBytes = Base64Utils.decode(encryptContent);
        byte[] deBytes = cipher.doFinal(midBytes);
        String s = new String(deBytes);
        return s;
    }
}