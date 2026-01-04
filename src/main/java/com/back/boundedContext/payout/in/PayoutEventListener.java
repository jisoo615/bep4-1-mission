package com.back.boundedContext.payout.in;

import com.back.boundedContext.payout.app.PayoutFacade;
import com.back.shared.market.event.MarketOrderPaymentCompletedEvent;
import com.back.shared.member.event.MemberJoinedEvent;
import com.back.shared.member.event.MemberModifiedEvent;
import com.back.shared.payout.event.PayoutCompletedEvent;
import com.back.shared.payout.event.PayoutMemberCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class PayoutEventListener {
    private final PayoutFacade payoutFacade;

    @KafkaListener(topics = "MemberJoinedEvent", groupId = "PayoutEventListener__handle__1")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(MemberJoinedEvent event) {
        payoutFacade.syncMember(event.getMember());
    }

    @KafkaListener(topics = "MemberModifiedEvent", groupId = "PayoutEventListener__handle__2")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(MemberModifiedEvent event) {
        payoutFacade.syncMember(event.getMember());
    }

    @KafkaListener(topics = "PayoutMemberCreatedEvent", groupId = "PayoutEventListener__handle__3")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(PayoutMemberCreatedEvent event) {
        payoutFacade.createPayout(event.getMember().getId());
    }

    @KafkaListener(topics = "MarketOrderPaymentCompletedEvent", groupId = "PayoutEventListener__handle__4")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(MarketOrderPaymentCompletedEvent event) {
        payoutFacade.addPayoutCandidateItems(event.getOrder());
    }

    @KafkaListener(topics = "PayoutCompletedEvent", groupId = "PayoutEventListener__handle__5")
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handle(PayoutCompletedEvent event) {
        payoutFacade.createPayout(event.getPayout().getPayeeId());
    }
}