package com.icecream.order.mapper;

import com.icecream.common.model.pojo.Wallet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.math.BigDecimal;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/2 0002
 */
@org.apache.ibatis.annotations.Mapper
public interface WalletMapper extends Mapper<Wallet>, MySqlMapper<Wallet> {

    @Update("update wallet set balance =  balance -#{star} where uid =#{uid} and sid =#{sid}")
    @ResultType(Integer.class)
    Integer reduceWalletBalance(@Param("star")BigDecimal stars,@Param("uid")Integer uid,@Param("sid")Integer sid);
}
