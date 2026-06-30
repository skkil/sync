package com.skkil.sync.media.repository;

import com.skkil.sync.media.model.Media;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {

  List<Media> findAllByIdIn(List<Long> mediaIds);
}
