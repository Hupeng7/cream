package com.icecream.order.controller;

import com.icecream.common.model.pojo.Wallet;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/6 0006
 */
@RestController
@RequestMapping("wallet")
public class MyWalletController {

    @Autowired
    private WalletService walletService;

    @RequestMapping("getBalance")
    public Wallet getMyWallet(Integer uid){
        return walletService.getMyWalletBalance(uid);
    }
}
