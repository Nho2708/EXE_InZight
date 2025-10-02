package org.inzight.mapper;

import org.inzight.dto.response.CommentResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.Comment;
import org.inzight.entity.Post;
import org.mapstruct.Mapping;

public interface CommentMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    CommentResponse toResponse(Comment comment);
}
