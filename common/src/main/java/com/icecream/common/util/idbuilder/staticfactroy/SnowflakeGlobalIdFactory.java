package com.icecream.common.util.idbuilder.staticfactroy;

import com.icecream.common.util.idbuilder.handler.SnowflakeArithmeticHandler;
import org.springframework.stereotype.Component;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/4 0004
 */
@Component
public class SnowflakeGlobalIdFactory implements GlobalIdFactory<SnowflakeArithmeticHandler> {


    @Override
    public SnowflakeArithmeticHandler create() {
        return new SnowflakeArithmeticHandler(0,0);
    }

}
