package com.skkil.sync.user.repository.pagination;

import static com.skkil.sync.jooq.tables.UserFollowRelationships.USER_FOLLOW_RELATIONSHIPS;

import com.skkil.sync.common.util.pagination.keyset.KeysetCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.keyset.KeysetField;
import com.skkil.sync.user.dto.data.UserConnectionCursor;
import com.skkil.sync.user.dto.data.UserConnectionDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserConnectionCursorPaginationProvider
    extends KeysetCursorPaginationProvider<UserConnectionDto, UserConnectionCursor> {

  @Override
  public Class<UserConnectionCursor> getCursorClass() {
    return UserConnectionCursor.class;
  }

  @Override
  protected List<KeysetField<UserConnectionCursor, ?>> getKeysetFields() {
    return List.of(
        KeysetField.asc(USER_FOLLOW_RELATIONSHIPS.ID, UserConnectionCursor::relationshipId));
  }

  @Override
  public UserConnectionCursor convert(UserConnectionDto entity) {
    return new UserConnectionCursor(entity.relationshipId());
  }
}
