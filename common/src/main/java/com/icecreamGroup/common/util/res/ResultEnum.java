package com.icecreamGroup.common.util.res;

/**
 * 统一错误返回
 * 自定义异常规范：模块 + 业务类型 + ERROR（log打印）
 * 对应的错误代码：10000是基数，每一个模块对应1000（针对与业务逻辑出错的错误代码，http错误代码不变且已定义）
 * 如基础服务类异常 ConfigServerConnectGitException 可以定义为10001
 * 如service层远程调用失败异常  RemoteCallException可以定义为11001
 * 可以根据业务需要不断向后添加非http异常
 * server-basic/common ------>10000
 * user --------------------->11000
 * order -------------------->12000
 * comment------------------->13000
 * good---------------------->14000
 * repertory----------------->15000
 *
 */
public enum ResultEnum {
    SUCCESS(2000, "success"),
    NAME_REPETITION(2003,"昵称重复"),
    TOKEN_INFO_ERROR(3003,"token中获取的用户id不存在"),
    NOT_FOUND(404, "您的访问地址可能在火星上哦"),
    AUTH_ERROR(401, "拒绝木星人，出门左转身份登记"),
    REQUEST_TYPE_TO_METHOD_NOT_ALLOW(405, "请求类型错误"),
    ERROR_UNKNOWN(500, "服务器正在与水星黑客展开殊死搏斗，请过几分钟再试"),
    PARAMS_ERROR(4000, "用户参数格式错误"),
    MYSQL_OPERATION_FAILED(4100,"数据库sql操作失败"),
    USER_USERNAME_OR_PASSWORD_ERROR(11010,"请输入有效用户名和密码"),
    ILLGAL_WEIBO_PARAMS(11011,"微博登陆时,accessToken/uid为空"),
    ILLGAL_QQ_PARAMS(11012,"QQ登陆时，openId/accessToken/appId为空"),
    ILLGAL_WECHAT_PARAMS(11013,"微信登陆时,code为空"),
    SMS_CODE_SEND_FAILED(11014,"验证码发送失败"),
    INSERT_REPETITION(11015,"重复插入"),
    DATA_ERROR(1001,"数据转换错误");

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