package org.inzight.mapper;

import org.inzight.dto.response.FriendResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.Friend;
import org.inzight.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")

public interface FriendMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "friend.id", target = "friendId")
    @Mapping(source = "friend.username", target = "friendName")

    FriendResponse toResponse(Friend friend);
    default LocalDateTime map(Instant instant) {
        return instant == null ? null :
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
