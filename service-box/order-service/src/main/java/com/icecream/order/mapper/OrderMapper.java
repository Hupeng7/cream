package com.icecream.order.mapper;


import com.icecream.common.model.pojo.Order;
import com.icecream.common.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

@org.apache.ibatis.annotations.Mapper
public interface OrderMapper extends Mapper<Order>, MySqlMapper<Order> {

    @Select("SELECT order_no orderNo,change_price changePrice,change_type changeType,change_time changeTime,user_behavior userBehavior,ctime from order_info " +
            "where order_no =#{orderNo} and sid=#{sid} and uid = #{uid}")
    @ResultType(User.class)
    Order getOrderDetail(@Param("sid")Integer sid,@Param("orderNo")String orderNo,@Param("uid")Integer uid);
}
