package com.util;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 返回响应数据
 */
@MappedSuperclass
public class Result implements Serializable {
    /**
     * 状态码
     */
    @Transient
    private Integer code;

    /**
     * 消息提示
     */
    @Transient
    private String message;

    /**
     * 返回的数据
     */
    @Transient
    private Object data;

    //列表Page
    @Transient
    private Page page;

    @ApiModelProperty(value = "返回类型", dataType = "String")
    private String warnLogs;
    /***************************************************************************/


    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.warnLogs = WarnLogAppender.getWarnMessages();
    }

    public Result(int code, String message, Object data, Page page) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.page = page;
        this.warnLogs = WarnLogAppender.getWarnMessages();
    }

    public static Result ok() {
        return ok(null);
    }

    public static Result ok(Object data) {
        return ok("操作成功", data);
    }

    public static Result ok(String message, Object data, Page page) {
        return new Result(20000, message, data, page);
    }

    public static Result ok(String message, Object data) {
        return new Result(20000, message, data);
    }

    public static Result error(String message) {
        return build(500, message);
    }

    //只是在前台不显示message信息
    public static Result errorWithoutMessageBox(String message) {
        return build(1000, message);
    }

    public static Result build(Integer code, String message) {
        return new Result(code, message, null);
    }

    /***************************************************************************/

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getWarnLogs() {
        return warnLogs;
    }

    public void setWarnLogs(String warnLogs) {
        this.warnLogs = warnLogs;
    }
}