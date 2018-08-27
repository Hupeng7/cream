package com.icecream.order.service;

import com.icecream.common.model.pojo.Wallet;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.mapper.WalletMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
@Slf4j
public class WalletService {

    @Autowired
    private WalletMapper walletMapper;

    //获取我的钱包余额
    public Wallet getMyWalletBalance(Integer uid) {
        return Optional.ofNullable(get(uid)).orElse(createDefaultWallet(uid));
    }

    //根据uid获取钱包对象
    public Wallet get(Integer uid) {
        Wallet wallet = new Wallet();
        wallet.setUid(uid);
        Wallet result = walletMapper.selectOne(wallet);
        return result;
    }

    //创建一个默认的钱包对象(新用户第一次充值)
    private Wallet createDefaultWallet(Integer uid) {
        return build(new BigDecimal(0), uid);
    }

    //根据充值星星数额插入对象
    public int insert(Integer uid, BigDecimal stars) {
        return walletMapper.insertSelective(build(stars, uid));
    }

    //构建数据库对象
    private Wallet build(BigDecimal stars, Integer uid) {
        Wallet wallet = new Wallet();
        wallet.setType(1);
        wallet.setStatus(1);
        wallet.setBalance(stars);
        wallet.setType(1);
        wallet.setUid(uid);
        wallet.setSid(1);
        wallet.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        return wallet;
    }


    //根据查询出的钱包对象进行更新(加钱)
    public int update(BigDecimal stars, Wallet wallet) {
        BigDecimal finalStars = getFinalStars(stars, wallet);
        wallet.setBalance(finalStars);
        wallet.setMtime(DateUtil.getNowSecondIntTime());
        return walletMapper.updateByPrimaryKeySelective(wallet);
    }


    //获取最终的星星余额(如果数额为负数，则抛出异常)
    private BigDecimal getFinalStars(BigDecimal stars, Wallet wallet) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal add = balance.add(stars);
        if (add.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("金额数据为负数");
        }
        return add;
    }


    //判断对于钱包对象是插入还是更新
    public int insertOrUpateHandler(Integer uid, BigDecimal stars) {
        Wallet wallet = get(uid);
        return wallet == null ? insert(uid, stars) : update(stars, wallet);
    }


    //先减钱
    @Transactional(rollbackFor = Exception.class)
    public Wallet reduceWalletBalanceOrRollBack(BigDecimal stars,Integer uid,Integer sid) {
        try {
            walletMapper.reduceWalletBalance(stars,uid,sid);
            Wallet wallet = get(uid);
            return Optional.ofNullable(wallet).filter(w -> wallet.getBalance().compareTo(BigDecimal.ZERO)>=0).orElseThrow(Exception::new);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
}
