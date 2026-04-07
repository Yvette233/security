package com.configuration;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.util.RequestUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

public class DataSourceEncrypt extends TransactionAwareDataSourceProxy {
    private static char[] appName =
            {
                    'B', 'I', 'N', 'G', 'O', 'D', 'I', 'N', 'G', 'O'
            };

//    fpc todo
//     @Autowired
    DataSourceEncrypt(HikariDataSource dataSource) {
        super.setTargetDataSource(decryptPassword(dataSource));
    }

    /*************************************************************************************/
    private DataSource decryptPassword(ComboPooledDataSource dataSource) {
        dataSource.setPassword(decode(dataSource.getPassword()));
        dataSource.setUser(decode(dataSource.getUser()));
        return dataSource;
    }

    private DataSource decryptPassword(HikariDataSource dataSource) {
        dataSource.setPassword(decode(dataSource.getPassword()));
        dataSource.setUsername(decode(dataSource.getUsername()));
        return dataSource;
    }

    private String decode(String codedValue) {
        BasicTextEncryptor decoder = new BasicTextEncryptor();
        decoder.setPasswordCharArray(appName);

        // 盐值
        decoder.setPassword((String) RequestUtils.getValueOfProperty("jasypt.encryptor.password"));
        if (PropertyValueEncryptionUtils.isEncryptedValue(codedValue)) {
            codedValue = PropertyValueEncryptionUtils.decrypt(codedValue, decoder);
            System.out.println("解密后的字符串：" + codedValue);
        }

//            return decoder.decrypt(encodedPassword);
        return codedValue;
    }

    private String encode(String password) {
        BasicTextEncryptor encoder = new BasicTextEncryptor();
        encoder.setPasswordCharArray(appName);
        return encoder.encrypt(password);
    }
}
