package org.inzight.mapper;


import org.inzight.dto.request.PostRequest;

import org.inzight.dto.response.PostResponse;

import org.inzight.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    PostResponse toResponse(Post post);


}
