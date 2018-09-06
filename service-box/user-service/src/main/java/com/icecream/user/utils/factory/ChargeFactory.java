package com.icecream.user.utils.factory;


import com.icecream.common.model.model.ChargeParamContainer;
import com.icecream.user.exception.UnsupportedPaymentTypeException;
import com.icecream.user.service.charge.AilPayChargeServiceImpl;
import com.icecream.user.service.charge.WxPayChargeServiceImpl;
import com.icecream.user.utils.factory.builder.FactoryBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static com.icecream.user.constants.Constants.TYPE_ALI_PAY;
import static com.icecream.user.constants.Constants.TYPE_WX_PAY;

/**
 * @author Mr_h
 * @version 1.0
 * description:充值业务工厂(目前支持的方式为支付宝和微信)
 * create by Mr_h on 2018/7/27 0027
 */
@Component
@SuppressWarnings("all")
public class ChargeFactory extends FactoryBuilder implements ApplicationContextAware{

    private static ApplicationContext context = null;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> Object getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static ChargeParamContainer getServiceHandler(ChargeParamContainer chargeParamContainer) throws UnsupportedPaymentTypeException {
        switch (chargeParamContainer.getType()) {
            case TYPE_ALI_PAY:
                chargeParamContainer.setService(getBean(AilPayChargeServiceImpl.class));
                return chargeParamContainer;
            case TYPE_WX_PAY:
                chargeParamContainer.setService(getBean(WxPayChargeServiceImpl.class));
                return chargeParamContainer;
            default:
                throw new UnsupportedPaymentTypeException("不支持的支付方式");
        }
    }
}
