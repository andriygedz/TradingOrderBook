package com.example.service;

import com.example.model.Order;
import com.example.model.OrderType;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class OrderBookTest {
    private OrderBook subject;
    private Order offer3;
    private Order offer4;
    private Order bid1;
    private Order bid2;

    @Before
    public void setup(){
        subject = new OrderBook();

        bid1 = Order.builder()
                .id(1)
                .isBid(true)
                .orderType(OrderType.LIMIT)
                .timestamp(new Date().getTime())
                .venue("T".toCharArray())
                .price(10)
                .quantity(2)
                .build();
        bid2 = Order.builder()
                .id(2)
                .isBid(true)
                .orderType(OrderType.LIMIT)
                .timestamp(new Date().getTime())
                .venue("T".toCharArray())
                .price(9)
                .quantity(2)
                .build();
        offer3 = Order.builder()
                .id(3)
                .isBid(false)
                .orderType(OrderType.LIMIT)
                .timestamp(new Date().getTime())
                .venue("T".toCharArray())
                .price(12)
                .quantity(2)
                .build();
        offer4 = Order.builder()
                .id(4)
                .isBid(false)
                .orderType(OrderType.LIMIT)
                .timestamp(new Date().getTime())
                .venue("T".toCharArray())
                .price(11)
                .quantity(2)
                .build();


    }

    @Test
    public void getBestBid_givenEmptyBook_returnNull() {
        assertThat(subject.getBestBid()).isNull();
    }

    @Test
    public void getBestOffer_givenEmptyBook_returnNull() {
        assertThat(subject.getBestOffer()).isNull();
    }

    @Test
    public void addOrder_givenAddOneOffer_bestOfferReturnsIt() {
        subject.addOrder(offer3);

        assertThat(subject.getBestOffer()).isEqualTo(offer3);

    }

    @Test
    public void addOrder_givenAddTwoOffers_bestOfferReturnsLowestPrise() {
        subject.addOrder(offer3);
        subject.addOrder(offer4);

        assertThat(subject.getBestOffer()).isEqualTo(offer4);
    }

    @Test
    public void addOrder_givenAddOneBid_bestBidReturnsIt() {
        subject.addOrder(bid1);

        assertThat(subject.getBestBid()).isEqualTo(bid1);

    }

    @Test
    public void addOrder_givenAddTwoBids_bestBidReturnsHighestPrise() {
        subject.addOrder(bid1);
        subject.addOrder(bid2);

        assertThat(subject.getBestBid()).isEqualTo(bid1);
    }

    @Test
    public void updateOrder_givenUpdateOffer_bestOfferReturnsUpdatedValue() {
        subject.addOrder(offer3);
        subject.addOrder(offer4);
        offer4.setQuantity(3);
        subject.updateOrder(offer4.getId(), 3);

        assertThat(subject.getBestOffer()).isEqualTo(offer4);
    }

    @Test
    public void updateOrder_givenUpdateBid_bestOfferReturnsUpdatedValue() {
        subject.addOrder(bid1);
        subject.addOrder(bid2);
        bid2.setQuantity(1);
        subject.updateOrder(bid2.getId(), 1);

        assertThat(subject.getBestBid()).isEqualTo(bid1);
    }

    @Test
    public void deleteOrder_givenDeleteOffer_bestOfferIsNull() {
        subject.addOrder(bid1);
        subject.deleteOrder(1);

        assertThat(subject.getBestBid()).isNull();
    }

    @Test
    public void deleteOrder_givenDeleteOffer_bestOfferBecomesNextBid() {
        subject.addOrder(bid1);
        subject.addOrder(bid2);
        subject.deleteOrder(1);

        assertThat(subject.getBestBid()).isEqualTo(bid2);
    }

    @Test
    public void processOrder_givenMarketOfferAndMultipleBids_returnsBidsList() {
        subject.addOrder(bid1);
        subject.addOrder(bid2);

        List<Order> orders = subject.processOrder(Order.builder()
                .orderType(OrderType.MARKET)
                .quantity(4)
                .isBid(false)
                .build());

        assertThat(orders).containsExactly(bid1, bid2);
    }

    @Test
    public void processOrder_givenLimitOfferAndHighPriceAndMultipleBids_returnsBidsList() {
        subject.addOrder(bid1);
        subject.addOrder(bid2);

        List<Order> orders = subject.processOrder(Order.builder()
                .orderType(OrderType.LIMIT)
                .quantity(4)
                .price(20)
                .isBid(false)
                .build());

        assertThat(orders).containsExactly(bid1, bid2);
    }

    @Test
    public void processOrder_givenLimitOfferAndLowPriceAndMultipleBids_returnsEmptyBidsList() {
        subject.addOrder(bid1);
        subject.addOrder(bid2);

        List<Order> orders = subject.processOrder(Order.builder()
                .orderType(OrderType.LIMIT)
                .quantity(4)
                .price(2)
                .isBid(false)
                .build());

        assertThat(orders).isEmpty();
    }

    @Test
    public void processOrder_givenLimitBidAndHighPriceAndMultipleOffers_returnsEmptyOffersList() {
        subject.addOrder(offer3);
        subject.addOrder(offer4);

        List<Order> orders = subject.processOrder(Order.builder()
                .orderType(OrderType.LIMIT)
                .quantity(4)
                .price(20)
                .isBid(true)
                .build());

        assertThat(orders).isEmpty();
    }

    @Test
    public void processOrder_givenLimitBidAndLowPriceAndMultipleOffers_returnsOffersList() {
        subject.addOrder(offer3);
        subject.addOrder(offer4);

        List<Order> orders = subject.processOrder(Order.builder()
                .orderType(OrderType.LIMIT)
                .quantity(4)
                .price(2)
                .isBid(true)
                .build());

        assertThat(orders).containsExactly(offer4, offer3);

    }
}