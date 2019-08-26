package com.example.model;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;

public class OfferBidMap {
    private final NavigableMap<OrderInfo, Integer> offers;
    private final NavigableMap<OrderInfo, Integer> bids;

    public OfferBidMap() {
        //lower offer price has higher priority
        Comparator<OrderInfo> asc = (o1, o2) -> {
            if (o1.getPrice() != o2.getPrice()) {
                //lower offer price has higher priority
                return Long.compare(o1.getPrice(), o2.getPrice());
            } else {
                return defaultCompare(o1, o2);
            }
        };
        offers = new TreeMap<>(asc);
        //higher bid price has higher priority
        Comparator<OrderInfo> des = (o1, o2) -> {
            if (o1.getPrice() != o2.getPrice()) {
                //higher bid price has higher priority
                return Long.compare(o2.getPrice(), o1.getPrice());
            } else {
                return defaultCompare(o1, o2);
            }
        };
        bids = new TreeMap<>(des);
    }

    private static int defaultCompare(OrderInfo o1, OrderInfo o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }

    public NavigableMap<OrderInfo, Integer> getOrderMap(boolean isBid) {
        if (isBid) {
            return bids;
        } else {
            return offers;
        }
    }
}
