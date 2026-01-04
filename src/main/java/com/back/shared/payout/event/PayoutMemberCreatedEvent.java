package com.back.shared.payout.event;

import com.back.shared.payout.dto.PayoutMemberDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PayoutMemberCreatedEvent implements HasEventName {
    private final PayoutMemberDto member;
}
