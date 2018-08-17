package com.icecream.order.controller;

import com.icecream.common.model.requstbody.CreateOrderModel;
import com.icecream.common.util.req.RequestHandler;
import com.icecream.common.util.res.ResultUtil;
import com.icecream.common.util.res.ResultVO;
import com.icecream.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Slf4j
@RefreshScope
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单详情
     *
     * @param sid     小星空等级
     * @param orderNo 订单编号
     * @return {@link ResultVO}
     */
    @GetMapping("/{sid}/{order_no}")
    public ResultVO getOrderByOrderNo(@PathVariable("sid") Integer sid, @PathVariable("order_no") String orderNo) {
        return ResultUtil.success(orderService.getOrderByOrderNo(sid, orderNo));
    }

    /**
     * 修改订单地址
     *
     * @param sid     小星空等级
     * @param orderNo 订单编号
     * @return {@link ResultVO}
     */
    @PutMapping("address/{sid}/{order_no}")
    public ResultVO updateOrderAddress(@PathVariable("sid") Integer sid, @PathVariable("order_no") String orderNo
            , HttpServletRequest request) {
        return orderService.updateOrderAddress(sid, orderNo, RequestHandler.getParams(request, "address"));
    }


    /**
     * 创建订单
     * @param specialTokenId 用户id
     * @param createOrderModel {@link CreateOrderModel}
     * @return {@link ResultVO}
     * 该创建订单流程为普通的商城订单
     * 影响的表：
     * wallet(用户钱包)，order_info(订单表),point_inout(账务流水表)，goods_spec(多规格商品表)
     * goods(商品表)，goods_limit(用户购买商品记录表)，user_exp(用户经验表)
     * 影响的库：
     * 用户库，订单库，商品库
     */
    @PostMapping("create")
    public ResultVO createOrder(@Param("specialTokenId") String specialTokenId, @Validated @RequestBody CreateOrderModel createOrderModel) {
        return orderService.create(Integer.parseInt(specialTokenId), createOrderModel);
    }


    /**
     * 查询某个订单的详情
     * @param sid 小星空等级
     * @param orderNo 订单编号
     * @param specialTokenId 用户id
     * @return {@link ResultVO}
     */
    @GetMapping("detail/{sid}/{order_no}")
    public ResultVO getOrderDetail(@PathVariable("sid") Integer sid,
                                   @PathVariable("order_no") String orderNo,
                                   @Param("specialTokenId") String specialTokenId) {
        return orderService.getOrderDetail(sid, orderNo, Integer.parseInt(specialTokenId));
    }


    @GetMapping("detailList/{count}/{lastTime}/{sort}")
    public ResultVO getOrderListAndSort(@PathVariable("count")Integer count,
                                        @PathVariable("lastTime")Integer lastTime,
                                        @PathVariable("sort")Integer sort,
                                        @Param("specialTokenId") String specialTokenId){
        return orderService.getOrderListSort(count,lastTime,sort,Integer.parseInt(specialTokenId));
    }


}
