package ru.job4j.site.configuration;

public final class NotificationKafkaTopics {
    public static final String INTERVIEW_TOPIC = "checkdev_notification_interview_topic";
    public static final String INTERVIEW_PARTICIPATE = "checkdev_notification_interview_participate";
    public static final String INTERVIEW_CANCEL = "checkdev_notification_interview_cancel";
    public static final String INTERVIEW_PARTICIPANT_DISMISSED = "checkdev_notification_participant_dismissed";
    public static final String WISHER_APPROVED = "checkdev_notification_wisher_approved";
    public static final String FEEDBACK_INTERVIEW = "checkdev_notification_feedback_interview";
    public static final String NEW_INTERVIEW = "checkdev_notification_new_interview";
    public static final String INNER_MESSAGE = "checkdev_notification_inner_message";

    private NotificationKafkaTopics() {
    }
}
