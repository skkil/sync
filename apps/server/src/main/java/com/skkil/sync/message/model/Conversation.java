package com.skkil.sync.message.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Table(name = "conversations")
@Getter
public class Conversation extends BaseEntity {

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.PERSIST)
  private List<Participant> participants = new ArrayList<>();
}
