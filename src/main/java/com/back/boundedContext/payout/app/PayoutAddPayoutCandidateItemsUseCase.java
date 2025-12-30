package com.back.boundedContext.payout.app;

import com.back.shared.market.dto.OrderDto;
import com.back.shared.market.dto.OrderItemDto;
import com.back.shared.market.out.MarketApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final MarketApiClient marketApiClient;

    public void addPayoutCandidateItems(OrderDto order) {
        List<OrderItemDto> items = marketApiClient.getOrderItems(order.getId());

        items.forEach(item -> {
            log.debug("orderItem.id : {}", item.getId());
        });
    }
    /**
     * payout -> payoutCandyDateitems -> payoutItems
     * 구매 확정 전에 구매를 취소할 경우가 있어서
     * 후보에 넣어두고 15일 후 구매확정 됐을 때 정산으로 보내는 것
     * 결제 완료 -> candidateItems에 추가 -> 15일 후 -> 정산
     */
}