package com.back.boundedContext.payout.app;

import com.back.shared.market.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    public void addPayoutCandidateItems(OrderDto order) {
        log.debug("addPayoutCandidateItems.order: {}", order.getId());
    }
    /**
     * payout -> payoutCandyDateitems -> payoutItems
     * 구매 확정 전에 구매를 취소할 경우가 있어서
     * 후보에 넣어두고 15일 후 구매확정 됐을 때 정산으로 보내는 것
     * 결제 완료 -> candidateItems에 추가 -> 15일 후 -> 정산
     */
}