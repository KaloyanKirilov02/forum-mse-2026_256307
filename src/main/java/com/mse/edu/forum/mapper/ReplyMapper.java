package com.mse.edu.forum.mapper;

import com.mse.edu.forum.api.generated.model.CreateReplyRequest;
import com.mse.edu.forum.api.generated.model.ReplyResponse;
import com.mse.edu.forum.api.generated.model.UpdateReplyRequest;
import com.mse.edu.forum.domain.ReplyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ReplyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "content", source = "request.content", qualifiedByName = "trimmed")
    ReplyEntity toEntity(CreateReplyRequest request, Long postId);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffset")
    @Mapping(target = "modifiedAt", source = "modifiedAt", qualifiedByName = "instantToOffset")
    ReplyResponse toResponse(ReplyEntity entity);

    default void applyUpdate(UpdateReplyRequest request, ReplyEntity entity) {
        entity.setContent(trimmed(request.getContent()));
    }

    @Named("trimmed")
    default String trimmed(String value) {
        return value == null ? null : value.trim();
    }

    @Named("instantToOffset")
    default OffsetDateTime instantToOffset(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }
}