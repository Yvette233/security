package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "SYS_LOGIN_INFO")
public class SysLoginInfo extends BasicModel {

    public final static Integer TYPE_LOGIN = 1;
    public final static Integer TYPE_ACCESS_PATH = 2;
    public final static Integer STATE_SUCCESS = 1;
    public final static Integer STATE_FAILURE = 0;
    private static final long serialVersionUID = 1L;
    // 人员编号
    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID")
    private String id;

    @Column(name = "STATE")
    private Integer state;

    @Column(name = "TYPE")
    private Integer type;

    //登录用户号
    @Column(name = "USERID")
    private String userId;
    //登陆用户名
    @Column(name = "USER_NAME")
    private String userName;
    //登陆角色
    @Column(name = "ROLEID")
    private String roleId;

    //    返回客户端发出请求时的完整URL。
    @Column(name = "REQUEST_URL")
    private String requestURL;
    //    返回请求行中的参数部分。
    @Column(name = "REQUEST_URI")
    private String requestURI;
    //    方法返回请求行中的参数部分（参数名+值）
    @Column(name = "QUERY_STRING")
    private String queryString;
    //    返回发出请求的客户机的完整主机名。
    @Column(name = "REMOTE_HOST")
    private String remoteHost;
    //    返回发出请求的客户机的IP地址。
    @Column(name = "REMOTE_ADDRESS")
    private String remoteAddr;
    //    返回请求URL中的额外路径信息。额外路径信息是请求URL中的位于Servlet的路径之后和查询参数之前的内容，它以"/"开头。
    @Column(name = "PATH_INFO")
    private String pathInfo;
    //    返回客户机所使用的网络端口号。
    @Column(name = "REMOTE_PORT")
    private String remotePort;
    //    返回WEB服务器的IP地址。
    @Column(name = "LOCAL_ADDRESS")
    private String localAddr;
    //    返回WEB服务器的主机名。
    @Column(name = "LOCAL_NAME")
    private String localName;

    /********************************************************************************/
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}