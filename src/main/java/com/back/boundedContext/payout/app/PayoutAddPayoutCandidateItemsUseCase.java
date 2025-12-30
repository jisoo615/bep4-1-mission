package com.back.boundedContext.payout.app;

import com.back.boundedContext.payout.domain.PayoutCandidateItem;
import com.back.boundedContext.payout.domain.PayoutEventType;
import com.back.boundedContext.payout.domain.PayoutMember;
import com.back.boundedContext.payout.out.PayoutCandidateItemRepository;
import com.back.shared.market.dto.OrderDto;
import com.back.shared.market.dto.OrderItemDto;
import com.back.shared.market.out.MarketApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutAddPayoutCandidateItemsUseCase {
    private final MarketApiClient marketApiClient;
    private final PayoutSupport payoutSupport;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;


    public void addPayoutCandidateItems(OrderDto order) {
        marketApiClient.getOrderItems(order.getId())
                        .forEach(orderItem -> makePayoutCandidateItems(order, orderItem));

    }
    /**
     * payout -> payoutCandyDateitems -> payoutItems
     * 구매 확정 전에 구매를 취소할 경우가 있어서
     * 후보에 넣어두고 15일 후 구매확정 됐을 때 정산으로 보내는 것
     * 결제 완료 -> candidateItems에 추가 -> 15일 후 -> 정산
     */

    /**
     * candidate item을 2개 만드는 이유
     * 판매자에게 결제금액-수수료, 홀딩(시스템)에게 수수료 이렇게 두 개를 만들어야 해서
     */
    private void makePayoutCandidateItems(
            OrderDto order,
            OrderItemDto orderItem
    ) {
        PayoutMember holding = payoutSupport.findHolingMember().get();
        PayoutMember buyer = payoutSupport.findMemberById(orderItem.getBuyerId()).get();
        PayoutMember seller = payoutSupport.findMemberById(orderItem.getSellerId()).get();

        makePayoutCandidateItem(
                PayoutEventType.정산__상품판매_수수료,
                orderItem.getModelTypeCode(),
                orderItem.getId(),
                order.getPaymentDate(),
                buyer,
                holding, // 시스템에게 수수료
                orderItem.getPayoutFee()
        );

        makePayoutCandidateItem(
                PayoutEventType.정산__상품판매_대금,
                orderItem.getModelTypeCode(),
                orderItem.getId(),
                order.getPaymentDate(),
                buyer,
                seller, // 판매자에게 대금
                orderItem.getSalePriceWithoutFee()
        );
    }

    private void makePayoutCandidateItem(
            PayoutEventType eventType,
            String relTypeCode,
            int relId,
            LocalDateTime paymentDate,
            PayoutMember payer,
            PayoutMember payee,
            long amount
    ) {
        PayoutCandidateItem payoutCandidateItem = new PayoutCandidateItem(
                eventType,
                relTypeCode,
                relId,
                paymentDate,
                payer,
                payee,
                amount
        );
        payoutCandidateItemRepository.save(payoutCandidateItem);
    }
}