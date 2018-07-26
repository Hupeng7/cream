package com.icecream.common.util.res;

/**
 * 统一错误返回
 * 自定义异常规范：模块 + 业务类型 + ERROR（log打印）
 * 对应的错误代码：1000是基数，每一个模块对应1000（针对与业务逻辑出错的错误代码，http错误代码不变且已定义）
 * 如service层远程调用失败异常  RemoteCallException可以定义为1999
 * 可以根据业务需要不断向后添加非http异常
 * http --------------------->100~1000
 * common ------------------->1000~2000
 * user --------------------->2000~3000
 * order -------------------->3000~4000
 * comment------------------->4000~5000
 * Good---------------------->5000~6000
 * repertory----------------->6000~7000
 * ...
 */
public enum ResultEnum {
    NOT_AUTH(401,"您所在的用户组没有该权限"),
    NOT_FOUND(404, "不存在的映射路径"),
    REQUEST_TYPE_TO_METHOD_NOT_ALLOW(405, "请求类型错误"),
    ERROR_UNKNOWN(500, "服务器跑路了呢.."),
    DATA_ERROR(1001,"数据转换错误"),
    MYSQL_OPERATION_FAILED(1002,"数据库sql操作失败"),
    INSERT_REPETITION(1003,"重复插入"),
    TOKEN_INFO_ERROR(1004,"token中获取的用户id不存在"),
    QUERY_RESULT_IS_NULL(1005,"查询结果为空"),
    SUCCESS(2000, "success"),
    NAME_REPETITION(2001,"昵称重复"),
    EXIST_ACCOUNT(2002,"账号已存在"),
    PARAMS_ERROR(2003, "参数格式错误"),
    ERROR_PHONE(2004,"手机号与用户id不符合"),
    SMS_CODE_SEND_FAILED(2005,"验证码发送失败"),
    EXIST_BINDING(2006,"该手机号已绑定其他雪糕群账号"),
    CODE_AUTHENTICATION_FAILED(2007,"验证码授权失败"),
    WRONG_CODE(2008,"验证码输入错误");


    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}