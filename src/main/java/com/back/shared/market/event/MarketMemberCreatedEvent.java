package com.back.shared.market.event;

import com.back.shared.market.dto.MarketMemberDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MarketMemberCreatedEvent implements HasEventName {
    private final MarketMemberDto member;
}