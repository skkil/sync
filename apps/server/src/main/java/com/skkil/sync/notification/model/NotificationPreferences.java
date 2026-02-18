package com.skkil.sync.notification.model;

import com.skkil.sync.notification.constant.ChannelType;
import java.util.List;

// TODO: Implement actual notification preferences logic
public class NotificationPreferences {

  public boolean isNotificationsEnabled() {
    return true;
  }

  public List<ChannelType> getEnabledChannelTypes() {
    return List.of(ChannelType.IN_APP);
  }
}
