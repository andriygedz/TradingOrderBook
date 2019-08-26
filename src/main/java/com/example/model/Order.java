package com.example.model;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Order {
    private long timestamp;
    private boolean isBid;
    private int id;
    private long price;
    private int quantity;
    private char[] venue;
    private OrderType orderType;

}
