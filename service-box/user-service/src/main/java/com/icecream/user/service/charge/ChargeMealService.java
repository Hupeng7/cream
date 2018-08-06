package com.icecream.user.service.charge;

import com.icecream.common.util.res.ResultVO;
import com.icecream.user.feignclients.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/3 0003
 */
@Service
public class ChargeMealService {


    @Autowired
    private OrderFeignClient orderFeignClient;

    public ResultVO get(){
        return orderFeignClient.getMeal();
    }

}
