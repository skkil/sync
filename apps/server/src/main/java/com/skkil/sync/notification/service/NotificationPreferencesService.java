package com.skkil.sync.notification.service;

import com.skkil.sync.notification.model.NotificationPreferences;
import org.springframework.stereotype.Service;

@Service
public class NotificationPreferencesService {

  public NotificationPreferences getNotificationPreferences() {
    return new NotificationPreferences();
  }
}
