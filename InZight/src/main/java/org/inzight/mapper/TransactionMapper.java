package org.inzight.mapper;

import org.inzight.dto.response.TransactionResponse;
import org.inzight.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "wallet.name", target = "walletName")
    @Mapping(source = "transactionDate", target = "transactionDate", qualifiedByName = "instantToLocalDateTime")
    TransactionResponse toResponse(Transaction transaction);

    // 🕒 Chuyển đổi Instant → LocalDateTime
    @Named("instantToLocalDateTime")
    default LocalDateTime map(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
