package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.*;
import ru.job4j.site.util.RestAuthCall;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.job4j.site.configuration.NotificationKafkaTopics.FEEDBACK_INTERVIEW;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INNER_MESSAGE;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_CANCEL;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_PARTICIPANT_DISMISSED;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_PARTICIPATE;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_TOPIC;
import static ru.job4j.site.configuration.NotificationKafkaTopics.NEW_INTERVIEW;
import static ru.job4j.site.configuration.NotificationKafkaTopics.WISHER_APPROVED;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationService {

    private final EurekaUriProvider uriProvider;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String SERVICE_ID = "notification";

    public void addSubscribeCategory(String token, int userId, int categoryId) {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        try {
            var url = String
                    .format("%s/subscribeCategory/add", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(token, mapper.writeValueAsString(subscribeCategory));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public void deleteSubscribeCategory(String token, int userId, int categoryId) {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        try {
            var url = String
                    .format("%s/subscribeCategory/delete", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(token, mapper.writeValueAsString(subscribeCategory));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public Optional<UserDTO> findCategoriesByUserId(int id) {
        var mapper = new ObjectMapper();
        try {
            var text = new RestAuthCall(String
                    .format("%s/subscribeCategory/%d", uriProvider.getUri(SERVICE_ID), id))
                    .get();
            List<Integer> list = mapper.readValue(text, new TypeReference<>() {
            });
            return Optional.of(new UserDTO(id, list));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void addSubscribeTopic(String token, int userId, int topicId) {
        SubscribeTopicDTO subscribeTopicDTO = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        try {
            var url = String.format("%s/subscribeTopic/add", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(subscribeTopicDTO));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public void deleteSubscribeTopic(String token, int userId, int topicId) {
        SubscribeTopicDTO subscribeTopic = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        try {
            var url = String
                    .format("%s/subscribeTopic/delete", uriProvider.getUri(SERVICE_ID));
            new RestAuthCall(url).post(
                    token, mapper.writeValueAsString(subscribeTopic));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    public Optional<UserTopicDTO> findTopicByUserId(int id) {
        var mapper = new ObjectMapper();
        try {
            var text = new RestAuthCall(String
                    .format("%s/subscribeTopic/%d", uriProvider.getUri(SERVICE_ID), id))
                    .get();
            List<Integer> list = mapper.readValue(text, new TypeReference<>() {
            });
            return Optional.of(new UserTopicDTO(id, list));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<InnerMessageDTO> findBotMessageByUserId(String token, int id) {
        var url = String
                .format("%s/messages/actual/%d", uriProvider.getUri(SERVICE_ID), id);
        var mapper = new ObjectMapper();
        try {
            var text = new RestAuthCall(url).get(token);
            return mapper.readValue(text, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void notifyAboutInterviewCreation(String token,
                                             CategoryWithTopicDTO categoryAndTopicIds) {
        sendNotificationEvent(NEW_INTERVIEW, categoryAndTopicIds);
    }

    public void sendFeedBackMessage(String token, InnerMessageDTO innerMessage) {
        sendNotificationEvent(INNER_MESSAGE, innerMessage);
    }

    public void sendFeedbackNotification(String token,
                                         FeedbackNotificationDTO feedbackNotification) {
        sendNotificationEvent(FEEDBACK_INTERVIEW, feedbackNotification);
    }

    /**
     * Метод отправляет запрос в сервис Notification.
     * Запрос для отправки подписчикам темы о том, что появилось новое интервью.
     *
     * @param token              String
     * @param interviewNotifyDTO InterviewNotifyDTO
     * @throws JsonProcessingException Exception
     */
    public void sendSubscribeTopic(String token, InterviewNotifyDTO interviewNotifyDTO) {
        sendNotificationEvent(INTERVIEW_TOPIC, interviewNotifyDTO);
    }

    /**
     * Метод оправляет запрос в сервис Notification.
     * Запрос для отправки автору собеседования о том что добавился участник.
     *
     * @param token           String
     * @param wisherNotifyDTO WisherNotifyDTO
     */
    public void sendParticipateAuthor(String token, WisherNotifyDTO wisherNotifyDTO) {
        sendNotificationEvent(INTERVIEW_PARTICIPATE, wisherNotifyDTO);
    }

    /**
     * Метод оправляет запрос в сервис Notification.
     * Запрос для отправки сообщения участнику собеседования о том что автор собеседования
     * удалил собеседование.
     *
     * @param token              String
     * @param cancelInterviewDTO CancelInterviewNotificationDTO
     */
    public void sendParticipateCancelInterview(String token,
                                               CancelInterviewNotificationDTO cancelInterviewDTO) {
        sendNotificationEvent(INTERVIEW_CANCEL, cancelInterviewDTO);
    }

    /**
     * Метод оправляет запрос в сервис Notification.
     * Запрос для отправки сообщения участнику собеседования о том что автор собеседования
     * одобрил другого участника.
     *
     * @param token                  String
     * @param wisherDismissedDTOList List<WisherDismissedDTO>
     */
    public void sendParticipantIsDismissed(String token,
                                           List<WisherDismissedDTO> wisherDismissedDTOList) {
        wisherDismissedDTOList.forEach(wisherDismissedDTO ->
                sendNotificationEvent(INTERVIEW_PARTICIPANT_DISMISSED, wisherDismissedDTO));
    }

    public void approvedWisher(String token, WisherApprovedDTO wisherApprovedDTO) {
        try {
            sendNotificationEvent(WISHER_APPROVED, wisherApprovedDTO);
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
        }
    }

    private void sendNotificationEvent(String topic, Object event) {
        try {
            kafkaTemplate.send(topic, event);
        } catch (Exception e) {
            log.error("Kafka notification send error, topic: {}, error: {}", topic, e.getMessage());
        }
    }
}
