package com.example.service;

import com.example.model.OfferBidMap;
import com.example.model.Order;
import com.example.model.OrderInfo;
import com.example.model.OrderType;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;

import java.util.*;

import static com.example.utils.Preconditions.checkState;


public class OrderBook {
    private final Map<Integer, OrderInfo> idOrdersMap = new HashMap<>();
    private final OfferBidMap offerBidMap = new OfferBidMap();



    public void addOrder(Order order) {
        if (order.getOrderType() == OrderType.MARKET) {
            processOrder(order);
        } else {
            int id = order.getId();
            OrderInfo orderInfo = buildOrderInfo(order);
            checkState(!idOrdersMap.containsKey(id), "OrderInfo already exists id: " + id);

            idOrdersMap.put(id, orderInfo);
            updateOrderQuantity(orderInfo, order.getQuantity());
        }
    }

    private void updateOrderQuantity(OrderInfo order, int quantity) {
        offerBidMap.getOrderMap(order.isBid()).put(order, quantity);
    }


    public void updateOrder(Integer id, Integer quantity) {
        OrderInfo existingOrder = idOrdersMap.get(id);
        if (existingOrder != null) {
            updateOrderQuantity(existingOrder, quantity);
        }
    }

    public void deleteOrder(Integer id) {
        OrderInfo existingOrder = idOrdersMap.remove(id);
        if (existingOrder != null) {
            offerBidMap.getOrderMap(existingOrder.isBid()).remove(existingOrder);
        }
    }

    public List<Order> processOrder(Order order) {
        NavigableMap<OrderInfo, Integer> orderMap = offerBidMap.getOrderMap(!order.isBid());
        Map.Entry<OrderInfo, Integer> orderInfoQuantityEntry = orderMap.firstEntry();
        OrderInfo orderInfo = orderInfoQuantityEntry.getKey();
        Integer quantity = orderInfoQuantityEntry.getValue();
        long marketPrice = orderInfo.getPrice();

        if (order.getOrderType() == OrderType.LIMIT && marketPrice > order.getPrice() ^ order.isBid()) {
            return Collections.emptyList();
        }

        if (order.getQuantity() < quantity) {
            updateOrder(orderInfo.getId(), order.getQuantity() - quantity);
            return Collections.singletonList(buildOrder(orderInfoQuantityEntry));
        } else if (order.getQuantity() == quantity) {
            deleteOrder(orderInfo.getId());
            return Collections.singletonList(buildOrder(orderInfoQuantityEntry));
        } else {
            deleteOrder(orderInfo.getId());
            order.setQuantity(quantity - order.getQuantity());

            List<Order> orders = new ArrayList<>(2);
            orders.add(buildOrder(orderInfoQuantityEntry));
            orders.addAll(processOrder(order));
            return orders;
        }
    }

    public Order getBestBid() {
        return getBestOrder(true);
    }

    public Order getBestOffer() {
        return getBestOrder(false);
    }

    private Order getBestOrder(boolean isBid) {
        Map.Entry<OrderInfo, Integer> orderInfoQuantityEntry = offerBidMap.getOrderMap(isBid).firstEntry();
        if (orderInfoQuantityEntry == null) return null;
        return buildOrder(orderInfoQuantityEntry);
    }

    private static OrderInfo buildOrderInfo(Order order) {
        OrderInfo orderInfo = new OrderInfo();
        convertOrderToOrderInfo(order, orderInfo);
        return orderInfo;
    }

    private static Order buildOrder(Map.Entry<OrderInfo, Integer> orderInfoQuantityEntry) {
        Order order = new Order();
        convertOrderInfoToOder(orderInfoQuantityEntry.getKey(), order);
        order.setQuantity(orderInfoQuantityEntry.getValue());
        return order;
    }

    @SneakyThrows
    private static void convertOrderToOrderInfo(Order order, OrderInfo orderInfo) {
        BeanUtils.copyProperties(orderInfo, order);
    }

    @SneakyThrows
    private static void convertOrderInfoToOder(OrderInfo orderInfo, Order order) {
        BeanUtils.copyProperties(order, orderInfo);
    }
}
