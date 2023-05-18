package com.company;

import com.company.models.City;
import com.company.models.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String GET_URL = "https://procodeday-01.herokuapp.com/meet-up/get-country-list";
    private static final String POST_URL = "https://procodeday-01.herokuapp.com/meet-up/post-request";
    public static void main(String[] args) throws IOException {

        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        // Шаг 1: Выполнить GET-запрос и получить данные
        Request request = new Request.Builder()
                .url(GET_URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        // Шаг 2: Преобразовать полученные данные в список объектов City
        List<City> cities = objectMapper.readValue(responseBody, objectMapper.getTypeFactory().constructCollectionType(List.class, City.class));


        // Шаг 3: Группировать города по странам и сортировать города в списке по алфавиту
        Map<String, List<City>> citiesByCountry = cities.stream()
                .collect(Collectors.groupingBy(City::getCountry));
        citiesByCountry.forEach((country, cityList) -> cityList.sort(Comparator.comparing(City::getCity)));

        // Шаг 4: Посчитать количество городов в каждой стране
        Map<String, Integer> cityCountByCountry = citiesByCountry.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size()));

        Student student = new Student();
        student.setName("Taalai Kulonbekov");
        student.setPhone("+996700051170");
        student.setGithubUrl("https://github.com/kulonbekov/beelineMeetup.git");

        // Шаг 6: Создать объект, содержащий результаты
        List<Map<String, Object>> results = new ArrayList<>();
        cityCountByCountry.forEach((country, count) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("country", country);
            result.put("cities", citiesByCountry.get(country).stream().map(City::getCity).collect(Collectors.toList()));
            result.put("cities_count", count.toString());
            results.add(result);
        });

        // Шаг 7: Создать объект для POST-запроса
        Map<String, Object> postData = new HashMap<>();
        postData.put("student", student);
        postData.put("result", results);

        // Шаг 8: Отправить POST-запрос
        /*String postDataJson = objectMapper.writeValueAsString(postData);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), postDataJson);
        Request postRequest = new Request.Builder()
                .url(POST_URL)
                .post(requestBody)
                .build();
        Response postResponse = client.newCall(postRequest).execute();
        String postResponseBody = postResponse.body().string();

        System.out.println("POST Response:");
        System.out.println(postResponseBody);*/

        String postDataJson = objectMapper.writeValueAsString(postData);
        System.out.println("POST Request:");
        System.out.println(postDataJson);

    }
}