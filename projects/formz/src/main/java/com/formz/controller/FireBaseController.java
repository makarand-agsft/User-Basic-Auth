package com.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.TopicManagementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BaseController {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @RequestMapping(value = "/subscribeToTopic", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createTopic(@RequestParam("tokens") List<String> tokens, @RequestParam("topicName") String topicName) throws Exception {

        TopicManagementResponse topicManagementResponse = firebaseMessaging.subscribeToTopic(tokens, topicName);

        return ResponseEntity.ok().body(topicManagementResponse);
    }

    @RequestMapping(value = "/sendNotification", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> sendNotificatioonToTopic(@RequestBody NotificationDTO notificationDTO) throws Exception {

        Notification notification = Notification
                .builder()
                .setTitle(notificationDTO.getNotification().getTitle())
                .setBody(notificationDTO.getNotification().getBody())
                .build();

        Message message = Message
                .builder()
                .setTopic(notificationDTO.getTopic())
                .setNotification(notification)
                .putAllData(notificationDTO.getData())
                .build();

        String responseMessage = firebaseMessaging.send(message);

        return ResponseEntity.ok().body(responseMessage);
    }

    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> unsubscribe(@RequestParam("tokens") List<String> tokens, @RequestParam("topicName") String topicName) throws Exception {

        TopicManagementResponse topicManagementResponse = firebaseMessaging.unsubscribeFromTopic(tokens, topicName);

        return ResponseEntity.ok().body(topicManagementResponse);
    }

}
