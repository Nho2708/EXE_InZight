package org.inzight.mapper;

import org.inzight.dto.request.WalletRequest;
import org.inzight.dto.response.WalletResponse;
import org.inzight.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    Wallet toEntity(WalletRequest request);

    WalletResponse toResponse(Wallet wallet);
}
