package zerobase.weatherproject.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.weatherproject.domain.DateWeather;
import zerobase.weatherproject.domain.Diary;
import zerobase.weatherproject.repository.DateWeatherRepository;
import zerobase.weatherproject.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;

    @InjectMocks
    private DiaryService diaryService;

    private static final LocalDate JANUARY = LocalDate.of(
            2024, 1, 2);
    private static final LocalDate SEPTEMBER = LocalDate.of(
            2024, 9, 3);
    private static final LocalDate NOVEMBER = LocalDate.of(
            2024, 11, 3);

    @Test
    void createDiaryTest() {
        // given
        DateWeather dateWeather = new DateWeather(
                JANUARY, "Clear", "01d", 298.55);

        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);

        // when
        when(dateWeatherRepository.findAllByDate(JANUARY)).thenReturn(
                Collections.singletonList(dateWeather)
        );

        diaryService.createDiary(JANUARY, "오늘의 일기");

        // then
        verify(diaryRepository, times(1)).save(any(Diary.class));
    }

    @Test
    void readDiaryTest() {
        // given
        DateWeather dateWeather = new DateWeather(
                JANUARY, "Clear", "01d", 298.55);

        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText("일기 조회 테스트");

        // when
        when(diaryRepository.findAllByDate(JANUARY))
                .thenReturn(Collections.singletonList(diary));

        List<Diary> diaries = diaryService.readDiary(JANUARY);

        // then
        assertThat(diaries)
                .hasSize(1)
                .first()
                .satisfies(getDiary -> {
                    assertThat(getDiary.getId()).isEqualTo(0);
                    assertThat(getDiary.getDate()).isEqualTo(JANUARY);
                    assertThat(getDiary.getWeather()).isEqualTo("Clear");
                    assertThat(getDiary.getIcon()).isEqualTo("01d");
                    assertThat(getDiary.getTemperature()).isEqualTo(298.55);
                    assertThat(getDiary.getText()).isEqualTo("일기 조회 테스트");
                });
    }

    @Test
    void readDiariesTest() {
        // given
        DateWeather dateWeather1 = new DateWeather(
                JANUARY, "Clear", "01d", 298.55);

        Diary diary1 = new Diary();
        diary1.setDateWeather(dateWeather1);
        diary1.setText("일기 조회 테스트1");

        DateWeather dateWeather2 = new DateWeather(
                SEPTEMBER, "Rain", "10d", 268.55);

        Diary diary2 = new Diary();
        diary2.setDateWeather(dateWeather2);
        diary2.setText("일기 조회 테스트2");

        DateWeather dateWeather3 = new DateWeather(
                NOVEMBER, "Clear", "01d", 288.55);

        Diary diary3 = new Diary();
        diary3.setDateWeather(dateWeather3);
        diary3.setText("일기 조회 테스트3");

        // when
        when(diaryRepository.findAllByDateBetween(JANUARY, NOVEMBER))
                .thenReturn(Arrays.asList(diary1, diary2, diary3));

        List<Diary> diaries = diaryService.readDiaries(JANUARY, NOVEMBER);

        // then
        assertThat(diaries)
                .hasSize(3)
                .first()
                .satisfies(diary -> {
                    assertThat(diary.getId()).isEqualTo(0);
                    assertThat(diary.getDate()).isEqualTo(JANUARY);
                    assertThat(diary.getWeather()).isEqualTo("Clear");
                    assertThat(diary.getIcon()).isEqualTo("01d");
                    assertThat(diary.getTemperature()).isEqualTo(298.55);
                    assertThat(diary.getText()).isEqualTo("일기 조회 테스트1");
                });

        assertThat(diaries)
                .hasSize(3)
                .element(1)
                .satisfies(diary -> {
                    assertThat(diary.getId()).isEqualTo(0);
                    assertThat(diary.getDate()).isEqualTo(SEPTEMBER);
                    assertThat(diary.getWeather()).isEqualTo("Rain");
                    assertThat(diary.getIcon()).isEqualTo("10d");
                    assertThat(diary.getTemperature()).isEqualTo(268.55);
                    assertThat(diary.getText()).isEqualTo("일기 조회 테스트2");
                });

        assertThat(diaries)
                .hasSize(3)
                .last()
                .satisfies(diary -> {
                    assertThat(diary.getId()).isEqualTo(0);
                    assertThat(diary.getDate()).isEqualTo(NOVEMBER);
                    assertThat(diary.getWeather()).isEqualTo("Clear");
                    assertThat(diary.getIcon()).isEqualTo("01d");
                    assertThat(diary.getTemperature()).isEqualTo(288.55);
                    assertThat(diary.getText()).isEqualTo("일기 조회 테스트3");
                });
    }

    @Test
    void updateDiaryTest() {
        // given
        DateWeather dateWeather = new DateWeather(
                JANUARY, "Clear", "01d", 298.55);

        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText("일기 조회 테스트");

        // when
        when(diaryRepository.getFirstByDate(JANUARY))
                .thenReturn(diary);

        String newText = "새로운 일기";
        diaryService.updateDiary(JANUARY, newText);

        // then
        assertThat(diary.getText()).isEqualTo(newText);
        verify(diaryRepository, times(1)).save(diary);
    }

    @Test
    void deleteDiaryTest() {
        // given
        // when
        diaryService.deleteDiary(JANUARY);
        // then
        verify(diaryRepository, times(1))
                .deleteAllByDate(JANUARY);
    }
}
