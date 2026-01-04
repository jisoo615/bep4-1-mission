package com.back.shared.cash.event;

import com.back.shared.market.dto.OrderDto;
import com.back.standard.ResultType.ResultType;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CashOrderPaymentFailedEvent implements ResultType, HasEventName {
    private final String resultCode;
    private final String msg;
    private final OrderDto order;
    private final long pgPaymentAmount;
    private final long shortfallAmount;
}