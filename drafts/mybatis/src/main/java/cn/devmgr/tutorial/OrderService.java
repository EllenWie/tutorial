package cn.devmgr.tutorial;

import java.util.Calendar;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.devmgr.tutorial.model.Order;
import cn.devmgr.tutorial.model.OrderItem;
import cn.devmgr.tutorial.model.Product;

@Service
public class OrderService {
    private Log log = LogFactory.getLog(OrderService.class);

    @Autowired
    private OrderDao orderDao;
    
    @Transactional
    public Order create(Order order){
        order.setOrderDate(Calendar.getInstance().getTime());
        //从查询积分和供货价，并计入订单
        for(OrderItem item: order.getOrderItems()){
            Product product = orderDao.selectProductById(item.getProductId());
            if(product == null){
                throw new RuntimeException("未找到产品" + item.getProductId() + "，不能下单。");
            }else{
                if(log.isTraceEnabled()){
                    log.trace("设置" + item.getProductId() + "价格未：" + product.getPrice() + ".");
                }
                item.setPrice(product.getPrice());
            }
        }
        //保存订单
        int result = orderDao.insertOrder(order);
        if(log.isTraceEnabled()){
            log.trace("插入的新订单记录" + order.getId() + "; sql return " + result);
        }
        return order;
    }
    
    public String getAString(@Valid @NotNull String s, @Valid Order order) {
        return "NEW_" + s + "; order_" + order.getOrderDate();
    }
    
    public Order getOneOrder(int id){
        return orderDao.getOrderById(id);
    }
}
