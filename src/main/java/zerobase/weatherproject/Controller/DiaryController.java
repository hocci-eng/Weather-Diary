package zerobase.weatherproject.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weatherproject.domain.Diary;
import zerobase.weatherproject.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @Operation(summary = "다이어리 생성", description = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장")
    @PostMapping("/create/diary")
    void createDiary(
            @RequestParam
            @Parameter(description = "생성할 날짜", required = true, example = "2024-11-04")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {

        diaryService.createDiary(date, text);
    }

    @Operation(summary = "다이어리 조회", description = "선택한 날짜의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diary")
    List<Diary> readDiary(
            @RequestParam
            @Parameter(description = "조회할 날짜", required = true, example = "2024-11-04")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return diaryService.readDiary(date);
    }

    @Operation(summary = "다이어리 기간 조회", description = "선택한 기간의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam
            @Parameter(description = "조회할 기간의 첫번째 날짜", required = true, example = "2024-11-04")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam
            @Parameter(description = "조회할 기간의 마지막 날짜", required = true, example = "2024-11-04")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return diaryService.readDiaries(startDate, endDate);
    }

    @Operation(summary = "다이어리 수정", description = "선택한 날짜의 첫번째 일기를 수정합니다")
    @PutMapping("/update/diary")
    void updateDiary(
            @RequestParam
            @Parameter(description = "수정할 날짜", required = true, example = "2024-11-04")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {

        diaryService.updateDiary(date, text);
    }

    @Operation(summary = "다이어리 삭제", description = "선택한 날짜의 모든 일기 데이터를 삭제합니다.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(
            @RequestParam
            @Parameter(description = "삭제할 날짜", required = true, example = "2024-11-04")
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        diaryService.deleteDiary(date);
    }
}
