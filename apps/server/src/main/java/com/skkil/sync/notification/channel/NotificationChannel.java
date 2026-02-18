package com.skkil.sync.notification.channel;

import com.skkil.sync.notification.constant.ChannelType;

public interface NotificationChannel {

  ChannelType type();

  void send(Long recipientId, String message);
}
