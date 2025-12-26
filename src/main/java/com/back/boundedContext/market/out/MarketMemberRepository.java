package com.back.boundedContext.market.out;

import com.back.boundedContext.market.domain.MarketMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketMemberRepository extends JpaRepository<MarketMember, Integer> {
}
