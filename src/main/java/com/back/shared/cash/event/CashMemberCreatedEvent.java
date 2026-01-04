package com.back.shared.cash.event;

import com.back.shared.cash.dto.CashMemberDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CashMemberCreatedEvent implements HasEventName {
    private final CashMemberDto member;
}