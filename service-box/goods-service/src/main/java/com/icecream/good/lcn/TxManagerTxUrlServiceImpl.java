package com.icecream.good.lcn;

import com.codingapi.tx.config.service.TxManagerTxUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @author  MR_H
 * LCN分布式事务相关
 * create by mr_h on 2018/6/6
 */
@Slf4j
@Service
public class TxManagerTxUrlServiceImpl implements TxManagerTxUrlService{


    @Value("${tm.manager.url}")
    private String url;

    @Override
    public String getTxUrl() {
       log.info("load tm.manager.url ");
        return url;
    }
}
