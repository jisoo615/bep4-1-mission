package com.back.kafka;

import com.back.standard.event.HasEventName;

public record MyEvent(String msg) implements HasEventName {
}