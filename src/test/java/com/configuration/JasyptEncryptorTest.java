package com.configuration;

import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public final class JasyptEncryptorTest {
    public static final String salt = "fangpengcheng@mail.tsinghua.edu.cn";

    public static BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();

    static {
        basicTextEncryptor.setPassword(salt);
    }

    public JasyptEncryptorTest() {
    }

    /**
     * 明文加密
     *
     * @return
     */
    @Test
    public void encode() {
        String plaintext = "les";
        System.out.println("明文字符串：" + plaintext);
        String ciphertext = basicTextEncryptor.encrypt(plaintext);
        System.out.println("加密后字符串：" + ciphertext);
    }

    /**
     * 解密
     *
     * @return
     */
    @Test
    public void decode() {
        String ciphertext = "3o/msh0k71m0MJWXJV8NGQ==";
        System.out.println("加密字符串：" + ciphertext);
        ciphertext = "ENC(" + ciphertext + ")";
        if (PropertyValueEncryptionUtils.isEncryptedValue(ciphertext)) {
            String plaintext = PropertyValueEncryptionUtils.decrypt(ciphertext, basicTextEncryptor);
            System.out.println("解密后的字符串：" + plaintext);
        }
    }

//    public static void main(String[] args){
//
//    }

    @Test
    public void generateKey() {
        // 加密
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("1ab1cd2ef4hd"); // 盐值
        String username = encryptor.encrypt("les");
        String password = encryptor.encrypt("6666");
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println(encryptor.decrypt(username));
        System.out.println(encryptor.decrypt(password));

//        jasypt.encryptor.password=1ab1cd2ef4hd
//        jasypt.encryptor.algorithm=PBEWithMD5AndDES
//##  #PBEWITHHMACSHA512ANDAES_256 它是 sha512 加 AES 高级加密，需要 Java JDK 1.9 及以上支持
//        jasypt.encryptor.iv-generator-classname=org.jasypt.iv.NoIvGenerator
    }

}
