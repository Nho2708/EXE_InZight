package org.inzight.mapper;

import org.inzight.dto.response.CommentResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.Comment;
import org.inzight.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", imports = { LocalDateTime.class, ZoneId.class })

public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(expression = "java(comment.getCreatedAt() != null ? LocalDateTime.ofInstant(comment.getCreatedAt(), ZoneId.systemDefault()) : null)",
            target = "createdAt")
    @Mapping(expression = "java(comment.getUpdatedAt() != null ? LocalDateTime.ofInstant(comment.getUpdatedAt(), ZoneId.systemDefault()) : null)",
            target = "updatedAt")
    CommentResponse toResponse(Comment comment);
}
