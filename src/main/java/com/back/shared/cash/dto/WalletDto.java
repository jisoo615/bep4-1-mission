package com.back.shared.cash.dto;

import com.back.boundedContext.cash.domain.Wallet;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * final 변수가 있을 때 직렬화 가능하게 하는 방법
 */
@AllArgsConstructor(
        onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
)
@Getter
public class WalletDto {
    private final int id;
    private final LocalDateTime createDate;
    private final LocalDateTime modifyDate;
    private final int holderId;
    private final String holderName;
    private final long balance;

    public WalletDto(Wallet wallet) {
        this(
                wallet.getId(),
                wallet.getCreateDate(),
                wallet.getModifyDate(),
                wallet.getHolder().getId(),
                wallet.getHolder().getUsername(),
                wallet.getBalance()
        );
    }
}