package zerobase.weatherproject.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.weatherproject.Controller.DiaryController;
import zerobase.weatherproject.domain.Diary;
import zerobase.weatherproject.service.DiaryService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
@ExtendWith(MockitoExtension.class)
public class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiaryService diaryService;

    private static final LocalDate JANUARY = LocalDate.of(
            2024, 1, 2);
    private static final LocalDate SEPTEMBER = LocalDate.of(
            2024, 9, 3);
    private static final LocalDate NOVEMBER = LocalDate.of(
            2024, 11, 3);


    @Test
    void successCreateDiary() throws Exception {
        // given

        String text = "Today was a great day!";

        diaryService.createDiary(NOVEMBER, text);
        doNothing().when(diaryService).createDiary(NOVEMBER, text);

        // when
        // then
        mockMvc.perform(post("/create/diary").
                        param("date", NOVEMBER.toString())
                        .content(text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void successReadDiary() throws Exception {
        // given
        Diary diary1 = new Diary();
        diary1.setId(1);
        diary1.setWeather("Rain");
        diary1.setIcon("10n");
        diary1.setTemperature(290.11);
        diary1.setText("첫 번째 다이어리");
        diary1.setDate(NOVEMBER);

        Diary diary2 = new Diary();
        diary2.setId(2);
        diary2.setWeather("Rain");
        diary2.setIcon("10n");
        diary2.setTemperature(290.11);
        diary2.setText("두 번째 다이어리");
        diary2.setDate(NOVEMBER);

        List<Diary> diaryList = Arrays.asList(diary1, diary2);

        // when
        when(diaryService.readDiary(NOVEMBER)).thenReturn(diaryList);

        // then
        mockMvc.perform(get("/read/diary")
                        .param("date", NOVEMBER.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(NOVEMBER.toString()))
                .andExpect(jsonPath("$[0].text").value("첫 번째 다이어리"))
                .andExpect(jsonPath("$[1].date").value(NOVEMBER.toString()))
                .andExpect(jsonPath("$[1].text").value("두 번째 다이어리"));
    }

    @Test
    void successReadDiaries() throws Exception {
        // given
        Diary diary1 = new Diary();
        diary1.setId(1);
        diary1.setWeather("Rain");
        diary1.setIcon("10n");
        diary1.setTemperature(290.11);
        diary1.setText("첫 번째 다이어리");
        diary1.setDate(JANUARY);

        Diary diary2 = new Diary();
        diary2.setId(2);
        diary2.setWeather("Rain");
        diary2.setIcon("01d");
        diary2.setTemperature(297.81);
        diary2.setText("두 번째 다이어리");
        diary2.setDate(SEPTEMBER);

        Diary diary3 = new Diary();
        diary3.setId(3);
        diary3.setWeather("Rain");
        diary3.setIcon("01d");
        diary3.setTemperature(270.08);
        diary3.setText("세 번째 다이어리");
        diary3.setDate(NOVEMBER);

        List<Diary> diaryList = Arrays.asList(diary1, diary2, diary3);

        // when
        when(diaryService.readDiaries(JANUARY, NOVEMBER)).thenReturn(diaryList);

        // then
        mockMvc.perform(get("/read/diaries")
                        .param("startDate",
                                JANUARY.toString())
                        .param("endDate",
                                NOVEMBER.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(JANUARY.toString()))
                .andExpect(jsonPath("$[0].text").value("첫 번째 다이어리"))
                .andExpect(jsonPath("$[1].date").value(SEPTEMBER.toString()))
                .andExpect(jsonPath("$[1].text").value("두 번째 다이어리"))
                .andExpect(jsonPath("$[2].date").value(NOVEMBER.toString()))
                .andExpect(jsonPath("$[2].text").value("세 번째 다이어리"));
    }

    @Test
    void successUpdateDiary() throws Exception {
        // given
        LocalDate date = NOVEMBER;
        String newText = "수정된 일기 내용";

        // when
        doNothing().when(diaryService).updateDiary(date, newText);

        // then
        mockMvc.perform(put("/update/diary").
                        param("date", NOVEMBER.toString())
                        .content(newText)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void successDeleteDiary() throws Exception {
        // given
        // when
        doNothing().when(diaryService).deleteDiary(NOVEMBER);

        // then
        mockMvc.perform(delete("/delete/diary")
                        .param("date", NOVEMBER.toString()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
