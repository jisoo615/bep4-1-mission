package com.back.shared.member.event;

import com.back.shared.member.dto.MemberDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberModifiedEvent implements HasEventName {
    private final MemberDto member;
}