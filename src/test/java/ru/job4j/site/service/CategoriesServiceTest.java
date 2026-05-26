package ru.job4j.site.service;

import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.TopicLiteDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CategoriesServiceTest {

    private final TopicsService topicsService = mock(TopicsService.class);
    private final InterviewsService interviewsService = mock(InterviewsService.class);
    private final EurekaUriProvider uriProvider = mock(EurekaUriProvider.class);
    private final CategoriesService categoriesService =
            spy(new CategoriesService(topicsService, interviewsService, uriProvider));

    @Test
    void whenGetMostPopularThenCountNewInterviewsByCategory() throws Exception {
        var javaBase = new CategoryDTO(1, "Java Base");
        var javaCore = new CategoryDTO(2, "Java Core");
        var spring = new CategoryDTO(3, "Spring");
        doReturn(List.of(javaBase, javaCore, spring)).when(categoriesService).getPopularFromDesc();
        when(topicsService.getAllTopicLiteDTO()).thenReturn(List.of(
                new TopicLiteDTO(11, "Syntax", "", 1, "Java Base", 1),
                new TopicLiteDTO(12, "OOP", "", 1, "Java Base", 2),
                new TopicLiteDTO(21, "Collections", "", 2, "Java Core", 1),
                new TopicLiteDTO(31, "MVC", "", 3, "Spring", 1)
        ));
        when(interviewsService.getNewInterviews()).thenReturn(List.of(
                interview(1001, 11),
                interview(1002, 12),
                interview(1003, 21)
        ));

        var actual = categoriesService.getMostPopular();

        assertThat(actual.get(0).getCountInterview()).isEqualTo(2L);
        assertThat(actual.get(1).getCountInterview()).isEqualTo(1L);
        assertThat(actual.get(2).getCountInterview()).isEqualTo(0L);
    }

    @Test
    void whenGetAllWithTopicsThenCategoryWithoutInterviewGetsZero() throws Exception {
        var javaBase = new CategoryDTO(1, "Java Base");
        var spring = new CategoryDTO(3, "Spring");
        doReturn(List.of(javaBase, spring)).when(categoriesService).getAll();
        when(topicsService.getAllTopicLiteDTO()).thenReturn(List.of(
                new TopicLiteDTO(11, "Syntax", "", 1, "Java Base", 1),
                new TopicLiteDTO(31, "MVC", "", 3, "Spring", 1)
        ));
        when(interviewsService.getNewInterviews()).thenReturn(List.of(interview(1001, 11)));

        var actual = categoriesService.getAllWithTopics();

        assertThat(actual.get(0).getCountInterview()).isEqualTo(1L);
        assertThat(actual.get(1).getCountInterview()).isEqualTo(0L);
    }

    @Test
    void whenInterviewHasUnknownTopicThenIgnoreIt() throws Exception {
        var javaBase = new CategoryDTO(1, "Java Base");
        doReturn(List.of(javaBase)).when(categoriesService).getPopularFromDesc();
        when(topicsService.getAllTopicLiteDTO()).thenReturn(List.of(
                new TopicLiteDTO(11, "Syntax", "", 1, "Java Base", 1)
        ));
        when(interviewsService.getNewInterviews()).thenReturn(List.of(
                interview(1001, 11),
                interview(1002, 999)
        ));

        var actual = categoriesService.getMostPopular();

        assertThat(actual.get(0).getCountInterview()).isEqualTo(1L);
    }

    private InterviewDTO interview(int id, int topicId) {
        var interview = new InterviewDTO();
        interview.setId(id);
        interview.setTopicId(topicId);
        return interview;
    }
}
