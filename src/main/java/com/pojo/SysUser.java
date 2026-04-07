package com.pojo;

import com.common.BasicModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "SYS_USER")
@JsonIgnoreProperties(ignoreUnknown = true)//@JsonIgnoreProperties({ "isEnabled" })
public class SysUser extends BasicModel implements UserDetails {


    public final static Integer USERSTATE_OPEN_INNER = 101;//开放--内部
    public final static Integer USERSTATE_OPEN_OUTER = 102;//开放--外部
    public final static Integer USERSTATE_NOT_OPEN = 103;//不开放
    public final static Integer USERSTATE_DELETE = 201;//已注销
    private static final long serialVersionUID = 1L;
    // 人员编号
    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    //im使用
    @Transient
    private String userSig;

    @Transient
    private long sdkAppId;

    // 人员姓名，英文
    @Column(name = "TOKEN")
    private String token;

    // 人员姓名，中文
    @Column(name = "USER_NAME_CN", length = 100, nullable = false)
    private String userNameCH;

    // 人员姓名，英文
    @Column(name = "USER_NAME", length = 100, nullable = false)
    private String userName;

    @Column(name = "TYPE", length = 20)
    private String type;

    @Column(name = "STATE")
    private Integer state;

    @Column(name = "CONFIRMTYPE", precision = 10)
    private Long confirmType;

    // 胸卡号
    @Column(name = "CARDID", length = 50)
    private String cardId;

    @Column(name = "SEX", precision = 22)
    private Long sex;

    @Column(name = "IMAGE_URL", length = 50)
    private String imageUrl;

    @Column(name = "MARRIED", precision = 22)
    private Long married;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "CELLPHONE", length = 20)
    private String cellphone;

    @Column(name = "DEGREE", length = 20)
    private String degree;

    @Column(name = "BIRTH", length = 7)
    private Date birth;

    @Column(name = "LOGIN_ROLEID")
    private String loginRoleId;

    /****************UserDetails中使用的********************/
    //activiti中调用taskRuntime.task(taskId)，需要先通过通过Principal::getName获取登录用户是否属于分派任务得候选人或候选组？
    //fpc todo 可以改为taskService...
    @Transient
    private String name;

    @Column(name = "PASSWORD", length = 50)
    private String password;

    @Transient
    private Set<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();
    /****************关联关系********************/
    @Transient
    private Set<SysRole> sysRoleSet = new HashSet<SysRole>();

    @Transient
    private Set<SysGroup> sysGroupSet = new HashSet<SysGroup>();

    @Transient
    private Set<SysDept> sysDeptSet = new HashSet<SysDept>();

    @Transient
    private Set<SysPosition> sysPositionSet = new HashSet<SysPosition>();

    //    // 部门
//    @Column(name = "DEPT_ID", length = 50)
//    private String deptId;
//    //many-to-one
////	@org.hibernate.annotations.ForeignKey(name="null")
//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
//    @Fetch(FetchMode.SELECT)
//    @JoinColumn(name = "DEPT_ID", insertable = false, updatable = false)
//    private SysDept sysDept = new SysDept();

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

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public long getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(long sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public Long getSex() {
        return sex;
    }

    public void setSex(Long sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Long getConfirmType() {
        return confirmType;
    }

    public void setConfirmType(Long confirmType) {
        this.confirmType = confirmType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getMarried() {
        return married;
    }

    public void setMarried(Long married) {
        this.married = married;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<SysRole> getSysRoleSet() {
        return sysRoleSet;
    }

    public void setSysRoleSet(Set<SysRole> sysRoleSet) {
        this.sysRoleSet = sysRoleSet;
    }

    public Set<SysGroup> getSysGroupSet() {
        return sysGroupSet;
    }

    public void setSysGroupSet(Set<SysGroup> sysGroupSet) {
        this.sysGroupSet = sysGroupSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return getId();
    }

    public void setName(String name) {
        setId(name);
    }

    public String getUserNameCH() {
        return userNameCH;
    }

    public void setUserNameCH(String userNameCH) {
        this.userNameCH = userNameCH;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<SysDept> getSysDeptSet() {
        return sysDeptSet;
    }

    public void setSysDeptSet(Set<SysDept> sysDeptSet) {
        this.sysDeptSet = sysDeptSet;
    }

    public Set<SysPosition> getSysPositionSet() {
        return sysPositionSet;
    }

    public void setSysPositionSet(Set<SysPosition> sysPositionSet) {
        this.sysPositionSet = sysPositionSet;
    }

    public String getLoginRoleId() {
        return loginRoleId;
    }

    public void setLoginRoleId(String loginRoleId) {
        this.loginRoleId = loginRoleId;
    }

    /******************************springsecurity用到的字段**********************************/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //userState为可用，否则全为不可用
        return this.state == 1 ? true : false;
    }

}