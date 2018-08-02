package com.icecream.order.service;

import com.icecream.common.model.pojo.AlipayNotifyRecord;
import com.icecream.common.model.pojo.AlipayNotifyRecordErrorLog;
import com.icecream.common.model.pojo.Wallet;
import com.icecream.common.util.time.DateUtil;
import com.icecream.order.mapper.AlipayNotifyRecordErrorLogMapper;
import com.icecream.order.mapper.AlipayNotifyRecordMapper;
import com.icecream.order.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/2 0002
 */
@Service
@SuppressWarnings("all")
public class ChargeRecordService {

    @Autowired
    private AlipayNotifyRecordMapper alipayNotifyRecordMapper;

    @Autowired
    private AlipayNotifyRecordErrorLogMapper alipayNotifyRecordErrorLogMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Transactional(rollbackFor = Exception.class)
    public String insert(AlipayNotifyRecord alipayNotifyRecord) {
        int count = alipayNotifyRecordMapper.insert(alipayNotifyRecord);
        Wallet slecetArgs = new Wallet();
        slecetArgs.setUid(alipayNotifyRecord.getUid());
        Wallet wallet = walletMapper.selectOne(slecetArgs);
        //如果改该用户从未充值过，数据库操作为insert
        if (wallet == null) {
            BigDecimal money = alipayNotifyRecord.getTotal_amount();
            int row = walletMapper.insertSelective(buildWallet(-1, alipayNotifyRecord.getUid(), money));
            return row > 0 ? "ok" : "";
        }
        //用户充值过，则在原来钱包的基础上加钱,数据库操作为update
        BigDecimal money = wallet.getBalance().add(alipayNotifyRecord.getTotal_amount());
        int row = walletMapper.updateByPrimaryKeySelective(buildWallet(
                wallet.getId(), alipayNotifyRecord.getUid(), money));
        return row > 0 ? "ok" : "";
    }

    public String insert(AlipayNotifyRecordErrorLog alipayNotifyRecordErrorLog) {
        int count = alipayNotifyRecordErrorLogMapper.insert(alipayNotifyRecordErrorLog);
        return count > 0 ? "ok" : "";
    }

    public Wallet buildWallet(Integer id, Integer uid, BigDecimal money) {
        Wallet inserArgs = new Wallet();
        if (id != -1) {
            inserArgs.setId(id);
        }
        inserArgs.setUid(uid);
        inserArgs.setBalance(money);
        inserArgs.setCtime(Integer.parseInt(DateUtil.getNowSecond()));
        inserArgs.setSid(1);
        inserArgs.setStatus(1);
        return inserArgs;
    }
}
