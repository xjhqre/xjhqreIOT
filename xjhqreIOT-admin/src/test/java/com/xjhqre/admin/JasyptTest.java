package com.xjhqre.admin;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.Test;

public class JasyptTest {

    @Test
    public void testEncrypt() throws Exception {

        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();

        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES"); // 加密的算法，这个算法是默认的

        config.setPassword("xjhqre"); // 加密的密钥，必须为ASCll码

        standardPBEStringEncryptor.setConfig(config);

        String plainText = "qovgrteqnsskbdbe";

        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);

        System.out.println(encryptedText);

    }

    @Test

    public void testDe() throws Exception {

        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();

        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");

        config.setPassword("xjhqre");

        standardPBEStringEncryptor.setConfig(config);

        String encryptedText = "RSSBtJfceg1SStaGPVmRDQ==";

        String plainText = standardPBEStringEncryptor.decrypt(encryptedText);

        System.out.println(plainText);

    }

}
