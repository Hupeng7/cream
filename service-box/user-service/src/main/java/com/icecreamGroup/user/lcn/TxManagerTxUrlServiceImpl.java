package com.icecreamGroup.user.lcn;

import com.codingapi.tx.config.service.TxManagerTxUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @author mr_h
 * create by MR_H on 2017/11/18
 * LCN分布式事务相关
 */
@Slf4j
@Service
public class TxManagerTxUrlServiceImpl implements TxManagerTxUrlService{


    @Value("${tm.manager.url}")
    private String url;

    @Override
    public String getTxUrl() {
       log.info("load tm.manager.url");
        return url;
    }
}
