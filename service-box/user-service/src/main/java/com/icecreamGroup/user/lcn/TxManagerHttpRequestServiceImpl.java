package com.icecreamGroup.user.lcn;

import com.codingapi.tx.netty.service.TxManagerHttpRequestService;
import com.lorne.core.framework.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @author mr_h
 * create by MR_H on 2017/11/18
 * LCN分布式事务相关
 */
@Slf4j
@Service
public class TxManagerHttpRequestServiceImpl implements TxManagerHttpRequestService{

    @Override
    public String httpGet(String url) {
        log.info("httpGet-start");
        String res = HttpUtils.get(url);
        log.info("httpGet-end");
        return res;
    }

    @Override
    public String httpPost(String url, String params) {
        log.info("httpPost-start");
        String res = HttpUtils.post(url,params);
        log.info("httpPost-end");
        return res;
    }
}
