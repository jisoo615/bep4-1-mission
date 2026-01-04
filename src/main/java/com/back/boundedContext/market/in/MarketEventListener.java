package com.back.boundedContext.market.in;

import com.back.boundedContext.market.app.MarketFacade;
import com.back.boundedContext.market.domain.MarketMember;
import com.back.shared.cash.event.CashOrderPaymentFailedEvent;
import com.back.shared.cash.event.CashOrderPaymentSucceededEvent;
import com.back.shared.member.event.MemberJoinedEvent;
import com.back.shared.member.event.MemberModifiedEvent;
import com.back.shared.market.event.MarketMemberCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@RequiredArgsConstructor
@Component
public class MarketEventListener {
    private final MarketFacade marketFacade;

    @KafkaListener(topics = "MemberJoinedEvent", groupId = "MarketEventListener__handle__1")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketMember handle(MemberJoinedEvent event) {
        return marketFacade.syncMember(event.getMember());
    }

    @KafkaListener(topics = "MemberModifiedEvent", groupId = "MarketEventListener__handle__2")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketMember handle(MemberModifiedEvent event) {
        return marketFacade.syncMember(event.getMember());
    }

    @KafkaListener(topics = "MarketMemberCreatedEvent", groupId = "MarketEventListener__handle__3")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(MarketMemberCreatedEvent event){
        marketFacade.createCart(event.getMember());
    }

    @KafkaListener(topics = "CashOrderPaymentSucceededEvent", groupId = "MarketEventListener__handle__4")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(CashOrderPaymentSucceededEvent event) {
        int orderId = event.getOrder().getId();
        marketFacade.completeOrderPayment(orderId);
    }

    @KafkaListener(topics = "CashOrderPaymentFailedEvent", groupId = "MarketEventListener__handle__5")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(CashOrderPaymentFailedEvent event) {
        int orderId = event.getOrder().getId();
        marketFacade.cancelOrderRequestPayment(orderId);
    }
}
