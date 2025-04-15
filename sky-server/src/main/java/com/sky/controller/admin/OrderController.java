package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @GetMapping("/statistics")
    public Result<List<OrderStatisticsVO>> statistics() {
        List<OrderStatisticsVO> orderStatisticsVOList = orderService.getOrderStatistics();
        return Result.success(orderStatisticsVOList);
    }


    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.Page(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param cancelDTO
     * @return
     */
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO cancelDTO) {
        orderService.update(cancelDTO);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 拒单
     * @param rejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO rejectionDTO) {
        orderService.rejection(rejectionDTO);
        return Result.success();
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }
}
