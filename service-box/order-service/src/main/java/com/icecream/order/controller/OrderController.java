package com.icecream.order.controller;

import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.util.req.RequestHandler;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RefreshScope
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单详情
     * @param sid 小星空等级
     * @param orderNo 订单编号
     * @return {@link ResultVO}
     */
    @GetMapping("/{sid}/{order_no}")
    public ResultVO getOrderByOrderNo(@PathVariable("sid")Integer sid, @PathVariable("order_no") String orderNo){
        return ResultUtil.success(orderService.getOrderByOrderNo(sid,orderNo));
    }

    /**
     * 修改订单地址
     * @param sid 小星空等级
     * @param orderNo 订单编号
     * @return {@link ResultVO}
     */
    @PutMapping("address/{sid}/{order_no}")
    public ResultVO updateOrderAddress(@PathVariable("sid")Integer sid, @PathVariable("order_no")String orderNo
            , HttpServletRequest request){
        return orderService.updateOrderAddress(sid,orderNo, RequestHandler.getParams(request,"address"));
    }


    @PostMapping("create")
    public ResultVO createOrder(@RequestParam("specialTokenIds")String uid,@Validated @RequestBody CreateOrderModel createOrderModel){
        return orderService.create(Integer.parseInt(uid),createOrderModel);
    }




}
