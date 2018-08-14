package com.icecream.order.service;

import com.codingapi.tx.annotation.ITxTransaction;
import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.requstbody.AddressInfo;
import com.icecream.common.util.idbuilder.staticfactroy.SnowflakeGlobalIdFactory;
import com.icecream.common.util.res.ResultEnum;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.mapper.OrderMapper;
import com.icecream.order.utils.OrderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.nio.file.OpenOption;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@Service
@SuppressWarnings("all")
public class OrderService implements ITxTransaction {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SnowflakeGlobalIdFactory snowflakeGlobalIdFactory;

    //获取订单详情
    public Order getOrderByOrderNo(Integer sid, String orderNo) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        return Optional.ofNullable(orderMapper.selectOne(order)).orElse(null);
    }

    //更新订单地址
    public ResultVO updateOrderAddress(Integer sid, String orderNo, String address) {
        Order order = getOrderByOrderNo(sid, orderNo);
        if ("".equals(address) | null == address) {
            return ResultUtil.error("address参数不能为空",ResultEnum.PARAMS_ERROR);
        }
        order.setAddress(address);
        AddressInfo addressInfo = getAddressInfo(address);
        order.setCity(addressInfo.getCity());
        order.setPhone(addressInfo.getPhone());
        order.setProvince(addressInfo.getProvince());
        order.setDistrict(addressInfo.getDistrict());
        order.setAddressee(addressInfo.getAddressSee());
        return ResultUtil.success(orderMapper.updateByPrimaryKeySelective(order));
    }

    @Transactional
    public int insert() {
        return orderMapper.insertSelective(OrderBuilder.buildOrder());
    }

    @Transactional
    public int insert(Order order) {
        return orderMapper.insertSelective(order);
    }

    public int updateOrderForCharge(Order order) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uid", order.getUid());
        criteria.andEqualTo("isPay", 0);
        criteria.andEqualTo("status", 1);
        criteria.andEqualTo("orderStatus", 0);
        criteria.andEqualTo("paymentType", 2);
        criteria.andEqualTo("isDigital", 1);
        return orderMapper.updateByExampleSelective(order, example);
    }

    private AddressInfo getAddressInfo(String address) {
        AddressInfo addressInfo = new AddressInfo();
        String[] outer = address.replace("|", "&").split("&");
        if (outer.length == 2) {
            String[] inner1 = outer[0].split(",");
            addressInfo.setAddressSee(inner1[0]);
            addressInfo.setPhone(inner1[1]);
            addressInfo.setProvince(inner1[2]);
            addressInfo.setCity(addressInfo.getProvince());
            addressInfo.setDistrict(outer[1]);
        } else {
            String[] inner1 = outer[0].split(",");
            String[] inner2 = outer[1].split(",");
            addressInfo.setAddressSee(inner1[0]);
            addressInfo.setPhone(inner1[1]);
            addressInfo.setProvince(inner1[2]);
            addressInfo.setCity(inner2[0]);
            addressInfo.setDistrict(outer[2]);
        }
        return addressInfo;
}
}

