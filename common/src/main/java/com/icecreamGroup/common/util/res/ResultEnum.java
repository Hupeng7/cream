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
    SUCCESS(200, "请求成功呢！好棒"),
    NOT_FOUND(404, "您的访问地址可能在火星上哦"),
    AUTH_ERROR(401, "拒绝木星人，出门左转身份登记"),
    ERROR_UNKOWN(500, "服务器正在与水星黑客展开殊死搏斗，请过几分钟再试"),
    PARAMS_ERROR(205, "星际航班上不能带核弹哦，请检查参数~"),
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