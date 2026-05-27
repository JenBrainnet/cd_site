package ru.job4j.site.service;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import ru.job4j.site.dto.CancelInterviewNotificationDTO;
import ru.job4j.site.dto.CategoryWithTopicDTO;
import ru.job4j.site.dto.FeedbackNotificationDTO;
import ru.job4j.site.dto.InnerMessageDTO;
import ru.job4j.site.dto.InterviewNotifyDTO;
import ru.job4j.site.dto.WisherApprovedDTO;
import ru.job4j.site.dto.WisherDismissedDTO;
import ru.job4j.site.dto.WisherNotifyDTO;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static ru.job4j.site.configuration.NotificationKafkaTopics.FEEDBACK_INTERVIEW;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INNER_MESSAGE;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_CANCEL;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_PARTICIPANT_DISMISSED;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_PARTICIPATE;
import static ru.job4j.site.configuration.NotificationKafkaTopics.INTERVIEW_TOPIC;
import static ru.job4j.site.configuration.NotificationKafkaTopics.NEW_INTERVIEW;
import static ru.job4j.site.configuration.NotificationKafkaTopics.WISHER_APPROVED;

class NotificationServiceKafkaTest {

    private final EurekaUriProvider uriProvider = mock(EurekaUriProvider.class);
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final NotificationService notificationService = new NotificationService(uriProvider, kafkaTemplate);

    @Test
    void whenNotifyAboutInterviewCreationThenSendKafkaEvent() {
        var dto = new CategoryWithTopicDTO(1, "category", 2, "topic", 3, 4);

        notificationService.notifyAboutInterviewCreation("token", dto);

        verify(kafkaTemplate).send(NEW_INTERVIEW, dto);
    }

    @Test
    void whenSendFeedbackMessageThenSendKafkaEvent() {
        var dto = new InnerMessageDTO();

        notificationService.sendFeedBackMessage("token", dto);

        verify(kafkaTemplate).send(INNER_MESSAGE, dto);
    }

    @Test
    void whenSendFeedbackNotificationThenSendKafkaEvent() {
        var dto = new FeedbackNotificationDTO();

        notificationService.sendFeedbackNotification("token", dto);

        verify(kafkaTemplate).send(FEEDBACK_INTERVIEW, dto);
    }

    @Test
    void whenSendSubscribeTopicThenSendKafkaEvent() {
        var dto = new InterviewNotifyDTO();

        notificationService.sendSubscribeTopic("token", dto);

        verify(kafkaTemplate).send(INTERVIEW_TOPIC, dto);
    }

    @Test
    void whenSendParticipateAuthorThenSendKafkaEvent() {
        var dto = new WisherNotifyDTO();

        notificationService.sendParticipateAuthor("token", dto);

        verify(kafkaTemplate).send(INTERVIEW_PARTICIPATE, dto);
    }

    @Test
    void whenSendParticipateCancelInterviewThenSendKafkaEvent() {
        var dto = new CancelInterviewNotificationDTO();

        notificationService.sendParticipateCancelInterview("token", dto);

        verify(kafkaTemplate).send(INTERVIEW_CANCEL, dto);
    }

    @Test
    void whenSendParticipantIsDismissedThenSendKafkaEventForEachWisher() {
        var first = new WisherDismissedDTO();
        first.setUserId(1);
        var second = new WisherDismissedDTO();
        second.setUserId(2);

        notificationService.sendParticipantIsDismissed("token", List.of(first, second));

        verify(kafkaTemplate).send(INTERVIEW_PARTICIPANT_DISMISSED, first);
        verify(kafkaTemplate).send(INTERVIEW_PARTICIPANT_DISMISSED, second);
    }

    @Test
    void whenApprovedWisherThenSendKafkaEvent() {
        var dto = new WisherApprovedDTO();

        notificationService.approvedWisher("token", dto);

        verify(kafkaTemplate).send(WISHER_APPROVED, dto);
    }
}
