package com.back.global.jpa.entity;

import com.back.global.global.GlobalConfig;
import com.back.standard.modelType.CanGetModelTypeCode;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
// 모든 엔티티들의 조상
public abstract class BaseEntity implements CanGetModelTypeCode {
    public abstract int getId();
    public abstract LocalDateTime getCreateDate();
    public abstract LocalDateTime getModifyDate();

    protected void publishEvent(Object event) {
        GlobalConfig.getEventPublisher().publish(event);
    }

    @Override
    public String getModelTypeCode(){
        return this.getClass().getSimpleName();
    }
}