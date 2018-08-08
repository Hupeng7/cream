package com.icecream.user.controller.wallet;

import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.wallet.MyWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr_h
 * @version 1.0
 * description:用户钱包调度器
 * create by Mr_h on 2018/7/26 0026
 */
@RestController
@RequestMapping("wallet")
public class UserWalletController {


    @Autowired
    private MyWalletService myWalletService;

    @GetMapping("getBalance")
    public ResultVO getMyWallet(@Param("specialTokenId") Integer specialTokenId){
        return myWalletService.getWallet(specialTokenId);
    }
}
