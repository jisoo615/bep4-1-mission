package com.back.shared.market.event;

import com.back.shared.market.dto.OrderDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MarketOrderPaymentRequestedEvent implements HasEventName {
    private OrderDto order;
    private long pgPaymentAmount;
}