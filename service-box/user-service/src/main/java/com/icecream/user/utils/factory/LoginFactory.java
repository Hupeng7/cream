package com.icecream.user.utils.factory;

import com.alibaba.fastjson.JSON;
import com.icecream.common.model.requstbody.*;
import com.icecream.user.service.login.account.AccountLoginService;
import com.icecream.user.service.login.auth.QQLoginService;
import com.icecream.user.utils.factory.builder.FactoryBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import static com.icecream.user.constants.Constants.*;

/**
 * @version 2.0
 */
@Component
@SuppressWarnings("all")
public class LoginFactory extends FactoryBuilder implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    private static LoginParamContainer loginParamContainer = null;

    static {
        loginParamContainer = new LoginParamContainer();
    }

    public LoginParamContainer getObject(LoginParamContainer loginParamContainer) {
        Integer type = loginParamContainer.getType();
        switch (type) {
            case TYPE_SMS:
                SmsLoginOrRegisterParams smsLoginOrRegisterParams = JSON.parseObject((String) loginParamContainer.getBody(), SmsLoginOrRegisterParams.class);
                smsLoginOrRegisterParams.setType(TYPE_SMS);
                return build(smsLoginOrRegisterParams, getBeanByClass(AccountLoginService.class));
            case TYPE_ACCOUNT:
                AccountLoginParams accountLoginParams = JSON.parseObject((String) loginParamContainer.getBody(), AccountLoginParams.class);
                accountLoginParams.setType(TYPE_ACCOUNT);
                return build(accountLoginParams, getBeanByClass(AccountLoginService.class));
            case TYPE_AUTH_QQ:
                QQLoginParams qqLoginParams = JSON.parseObject((String) loginParamContainer.getBody(), QQLoginParams.class);
                qqLoginParams.setType(TYPE_AUTH_QQ);
                return build(qqLoginParams, getBeanByClass(QQLoginService.class));
            case TYPE_AUTH_WB:
                WbLoginParams wbLoginParams = JSON.parseObject((String) loginParamContainer.getBody(), WbLoginParams.class);
                wbLoginParams.setType(TYPE_AUTH_WB);
                return build(wbLoginParams, getBeanByClass(QQLoginService.class));
            case TYPE_AUTH_WX:
                WxLoginParams wxLoginParams = JSON.parseObject((String) loginParamContainer.getBody(), WxLoginParams.class);
                wxLoginParams.setType(TYPE_AUTH_WX);
                return build(wxLoginParams, getBeanByClass(QQLoginService.class));
            default:
                throw new RuntimeException("未知的type类型");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (LoginFactory.applicationContext == null) {
            LoginFactory.applicationContext = applicationContext;
        }
    }

    public static <T> T getBeanByClass(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }


    public static <K, V> LoginParamContainer build(K body, V service) {
        return new LoginParamContainer(body, service);
    }


}
