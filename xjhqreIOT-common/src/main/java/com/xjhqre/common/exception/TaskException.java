package com.xjhqre.common.exception;

/**
 * 计划策略异常
 * 
 * @author ruoyi
 */
public class TaskException extends Exception {
    private static final long serialVersionUID = 1L;

    private final Code code;

    public TaskException(String msg, Code code) {
        this(msg, code, null);
    }

    public TaskException(String msg, Code code, Exception nestedEx) {
        super(msg, nestedEx);
        this.code = code;
    }

    public Code getCode() {
        return this.code;
    }

    public enum Code {
        TASK_EXISTS, // 存在任务
        NO_TASK_EXISTS, // 不存在任务
        TASK_ALREADY_STARTED, // 任务正在执行
        UNKNOWN, // 未知
        CONFIG_ERROR, // 配置错误
        TASK_NODE_NOT_AVAILABLE // 任务不支持
    }
}