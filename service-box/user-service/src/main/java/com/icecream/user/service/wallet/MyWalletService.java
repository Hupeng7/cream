package com.icecream.user.service.wallet;

import com.icecream.common.model.pojo.Wallet;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.feignclients.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/6 0006
 */
@Service
public class MyWalletService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    public ResultVO getWallet(Integer uid){
        Wallet wallet = orderFeignClient.getWallet(uid);
        return ResultUtil.success(wallet);

    }
}
