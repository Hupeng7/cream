package com.icecream.user.utils.factory.builder;

import com.icecream.user.utils.factory.ChargeFactory;
import com.icecream.user.utils.factory.LoginFactory;
import org.springframework.stereotype.Component;

import static com.icecream.user.constants.Constants.CHARGE;
import static com.icecream.user.constants.Constants.LOGIN;

/**
 * @version 2.0
 */
@Component
public abstract class FactoryBuilder {

    public static FactoryBuilder build(Integer factoryType){
        switch (factoryType){
            case LOGIN:
                return new LoginFactory();
            case CHARGE:
                return new ChargeFactory();
            default:
                throw new RuntimeException("工厂类型错误");
        }
    }
}
