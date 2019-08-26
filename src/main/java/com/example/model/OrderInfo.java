package com.example.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class OrderInfo {
    private long timestamp;
    private boolean isBid;
    private int id;
    private long price;
    private char[] venue;
    private OrderType orderType;

}
