package com.back.boundedContext.payout.out;

import com.back.boundedContext.payout.domain.PayoutMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutMemberRepository extends JpaRepository<PayoutMember,Integer> {
}
