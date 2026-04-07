package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity()
@Table(name = "SYS_MAIL")
public class SysMail extends BasicModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    @Column(name = "USER_ID", length = 50)
    private String userId;

    @Column(name = "MAIL_NAME", length = 50)
    private String mailName;

    @Column(name = "MAIL_PASSWORD", length = 50)
    private String mailPassword;

    @Column(name = "MAIL_PORT", length = 50)
    private Integer mailPort;

    @Column(name = "MAIL_HOST", length = 50)
    private String mailHost;

    @Column(name = "MAIL_USESSL", length = 50)
    private Boolean mailUseSSL;

    /**********************************************************************************/

    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return this.getId();
    }

    @Override
    public void setUuid(String uuid) {
        // TODO Auto-generated method stub
        this.setId(uuid);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailName(String mailName) {
        this.mailName = mailName;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public Integer getMailPort() {
        return mailPort;
    }

    public void setMailPort(Integer mailPort) {
        this.mailPort = mailPort;
    }

    public Boolean getMailUseSSL() {
        return mailUseSSL;
    }

    public void setMailUseSSL(Boolean mailUseSSL) {
        this.mailUseSSL = mailUseSSL;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }
}
