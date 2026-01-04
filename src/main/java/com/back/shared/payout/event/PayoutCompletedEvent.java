package com.back.shared.payout.event;

import com.back.shared.payout.dto.PayoutDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PayoutCompletedEvent implements HasEventName {
    private final PayoutDto payout;
}