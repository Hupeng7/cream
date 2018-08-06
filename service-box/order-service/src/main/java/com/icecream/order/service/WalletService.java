package com.icecream.order.service;

import com.icecream.common.model.pojo.Wallet;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Mr_h
 * @version 1.0
 * description: 用户钱包
 * create by Mr_h on 2018/8/6 0006
 */
@Service
@SuppressWarnings("all")
public class WalletService {

    @Autowired
    private WalletMapper walletMapper;

    public Wallet getMyWalletBalance(Integer uid) {
        Wallet wallet = new Wallet();
        wallet.setUid(uid);
        Wallet result = walletMapper.selectOne(wallet);
        if (result != null) {
            return result;
        } else {
            wallet.setBalance(new BigDecimal(0));
            return wallet;
        }
    }
}
