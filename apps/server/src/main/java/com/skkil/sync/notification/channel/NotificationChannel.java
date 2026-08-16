package com.skkil.sync.notification.channel;

import com.skkil.sync.notification.constant.ChannelType;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.user.model.User;

public interface NotificationChannel {

  ChannelType type();

  void send(User recipient, NotificationSummary notification);
}
