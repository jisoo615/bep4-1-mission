package com.back.shared.post.event;

import com.back.shared.post.dto.PostDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreatedEvent implements HasEventName {
    private final PostDto post;
}