package com.back.shared.post.event;

import com.back.shared.post.dto.PostCommentDto;
import com.back.standard.event.HasEventName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostCommentCreatedEvent implements HasEventName {
    private final PostCommentDto postComment;
}