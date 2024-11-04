package zerobase.weatherproject.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weatherproject.WeatherProjectApplication;
import zerobase.weatherproject.domain.DateWeather;
import zerobase.weatherproject.domain.Diary;
import zerobase.weatherproject.exception.DiaryException;
import zerobase.weatherproject.repository.DateWeatherRepository;
import zerobase.weatherproject.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static zerobase.weatherproject.type.ErrorCode.*;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;
    private final static Logger logger = LoggerFactory.getLogger(WeatherProjectApplication.class);

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private final WeatherApiService service = new WeatherApiService();

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDateEveryDayAtAm1() {
        dateWeatherRepository.save(service.getWeatherFromApi());
        logger.info("finished to save weather date at 1am");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        validateCreateDiary(date);

        DateWeather dateWeather = service.getDateWeather(date);

        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);
        diary.setDate(date);

        diaryRepository.save(diary);

        logger.info("finished to create diary");
    }

    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary firstByDate = diaryRepository.getFirstByDate(date);
        firstByDate.setText(text);
        diaryRepository.save(firstByDate);

        logger.info("finished to update diary");
    }

    @Transactional()
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);

        logger.info("finished to delete diary");
    }

    private void validateCreateDiary(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            logger.error("date is after current date");
            throw new DiaryException(FUTURE_DATE_NOT_ALLOWED);
        }
    }

    private class WeatherApiService {

        private DateWeather getDateWeather(LocalDate date) {
            List<DateWeather> dateWeatherRepositoryFromDB =
                    dateWeatherRepository.findAllByDate(date);
            if (dateWeatherRepositoryFromDB.isEmpty()) {
                return getWeatherFromApi();
            }
            return dateWeatherRepositoryFromDB.getFirst();
        }

        private DateWeather getWeatherFromApi() {
            HashMap<String, Object> parsedWeather = parseWeather(getWeatherString());

            DateWeather dateWeather = new DateWeather();
            dateWeather.setDate(LocalDate.now());
            dateWeather.setWeather(parsedWeather.get("main").toString());
            dateWeather.setIcon(parsedWeather.get("icon").toString());
            dateWeather.setTemperature(Double.parseDouble((parsedWeather.get("temp")
                    .toString())));

            return dateWeather;
        }

        private String getWeatherString() {
            try {
                URI uri = new URI("https", "api.openweathermap.org",
                        "/data/2.5/weather", "q=seoul&appid=" + apiKey,
                        null
                );

                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("GET");

                try (
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                conn.getResponseCode() == HttpURLConnection.HTTP_OK
                                        ? conn.getInputStream() : conn.getErrorStream()
                        ))
                ) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                }
            } catch (Exception e) {
                throw new DiaryException(FAILED_API_REQUEST);
            }
        }

        private HashMap<String, Object> parseWeather(String jsonSting) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject;

            try {
                jsonObject = (JSONObject) jsonParser.parse(jsonSting);
            } catch (ParseException e) {
                throw new DiaryException(FAILED_JSON_PARSING);
            }

            HashMap<String, Object> resultMap = new HashMap<>();

            JSONObject mainData = (JSONObject) jsonObject.get("main");
            if (mainData != null) {
                resultMap.put("temp", mainData.get("temp"));

            }

            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            if (weatherArray != null && !weatherArray.isEmpty()) {
                JSONObject weatherData = (JSONObject) weatherArray.getFirst();
                if (weatherData != null) {
                    resultMap.put("main",
                            weatherData.get("main"));
                    resultMap.put("icon",
                            weatherData.get("icon"));
                }
            }

            return resultMap;
        }
    }
}
