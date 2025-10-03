package org.inzight.mapper;


import org.inzight.dto.request.PostRequest;

import org.inzight.dto.response.PostResponse;

import org.inzight.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { CommentMapper.class })
public interface PostMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "comments", target = "comments")
    PostResponse toResponse(Post post);


}
