package com.icecream.user.service.charge;

import com.icecream.common.util.res.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description: 微信充值
 * create by Mr_h on 2018/7/27 0027
 */
@Slf4j
@Service
public class WxPayChargeServiceImpl implements ChargeService {

    @Override
    public ResultVO charge(BigDecimal price) {
        log.info("收到金额数据------->{},准备请求微信...",price);
        return null;
    }
}
