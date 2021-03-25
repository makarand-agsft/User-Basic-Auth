package com.formz.service.impl;

import com.formz.dto.NotificationDTO;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FireBaseServiceImpl {

    @Autowired
    FirebaseMessaging firebaseMessaging;

    public String sendMessage(NotificationDTO notificationDTO) throws FirebaseMessagingException {


        Notification notification = Notification
                .builder()
                .setTitle(notificationDTO.getTitle())
                .setBody(notificationDTO.getBody())
                .build();

        Message message = Message
                .builder()
                .setTopic(notificationDTO.getTopic())
                .setNotification(notification)
                .putAllData(notificationDTO.getData())
                .build();

        return firebaseMessaging.send(message);
    }

    public TopicManagementResponse subscribeToTopic(List<String> tokens, String topic) throws FirebaseMessagingException {
        TopicManagementResponse topicManagementResponse = firebaseMessaging.subscribeToTopic(tokens, topic);
        return topicManagementResponse;
    }

    public TopicManagementResponse unSubscribeToTopic(List<String> tokens, String topic) throws FirebaseMessagingException {
        TopicManagementResponse topicManagementResponse = firebaseMessaging.unsubscribeFromTopic(tokens, topic);
        return topicManagementResponse;

    }
}
