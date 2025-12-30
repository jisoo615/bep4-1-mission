package com.back.boundedContext.market.domain;

import com.back.global.jpa.entity.BaseIdAndTime;
import com.back.shared.market.dto.OrderDto;
import com.back.shared.market.event.MarketOrderPaymentCompletedEvent;
import com.back.shared.market.event.MarketOrderPaymentRequestedEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER")
@NoArgsConstructor
@Getter
public class Order extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    private MarketMember buyer;
    private LocalDateTime cancelDate;
    private LocalDateTime requestPaymentDate;
    private LocalDateTime paymentDate;
    private long price;
    private long salePrice;

    @OneToMany(mappedBy = "order", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order(Cart cart) {
        this.buyer = cart.getBuyer();

        cart.getItems().forEach(item -> {
            addItem(item.getProduct());
        });
    }

    public OrderDto toDto() {
        return new OrderDto(
                getId(),
                getCreateDate(),
                getModifyDate(),
                buyer.getId(),
                buyer.getNickname(),
                price,
                salePrice,
                requestPaymentDate,
                paymentDate
        );
    }

    public void addItem(Product product) {
        OrderItem orderItem = new OrderItem(
                this,
                product,
                product.getName(),
                product.getPrice(),
                product.getSalePrice()
        );

        items.add(orderItem);

        price += product.getPrice();
        salePrice += product.getSalePrice();
    }

    /**
     * 결제가 완료되면 이벤트 발행
     */
    public void completePayment() {
        paymentDate = LocalDateTime.now();

        publishEvent(
                new MarketOrderPaymentCompletedEvent(
                        toDto()
                )
        );
    }

    public boolean isPaid() {
        return paymentDate != null;
    }

    public boolean isCanceled(){
        return cancelDate!=null;
    }

    /**
     * 이미 결제된 것인지 취소된것인지 진행중인 것인지 확인하는 로직
     */
    public boolean isPaymentInProgress(){
        return requestPaymentDate != null && paymentDate==null && cancelDate==null;
    }


    public void requestPayment(long pgPaymentAmount) {
        requestPaymentDate = LocalDateTime.now();

        publishEvent(
                new MarketOrderPaymentRequestedEvent(
                        toDto(),
                        pgPaymentAmount
                )
        );
    }

    public void cancelRequestPayment() {
        requestPaymentDate = null;
    }
}