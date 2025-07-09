package com.example.project.service;

import com.example.project.dto.BookDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AladinService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ALADIN_API_URL = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    private static final String TTB_KEY = "ttbheebi16541715001";

    public List<BookDto> searchBooks(String keyword) {
        String requestUrl = ALADIN_API_URL +
                "?TTBKey=" + TTB_KEY +
                "&Query=" + keyword +
                "&QueryType=Title&MaxResults=10&SearchTarget=Book&output=js&Version=20131101";

        List<BookDto> books = new ArrayList<>();
        
        try {
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);

            // 응답이 정상적인 경우에만 처리
            if (response.getStatusCode().is2xxSuccessful()) {
                // JSON 파싱
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode items = root.path("item");

                // 'item'이 존재하지 않으면 빈 리스트 반환
                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String category = item.path("categoryName").asText(); // 🔹 장르
                        int totalPages = item.path("subInfo").path("itemPage").asInt(0); // 🔹 전체 페이지 수 (없으면 0)

                        BookDto book = new BookDto(
                                item.path("title").asText(),
                                item.path("author").asText(),
                                item.path("publisher").asText(),
                                item.path("pubDate").asText(),
                                item.path("isbn").asText(),
                                item.path("cover").asText(),
                                item.path("link").asText(),
                                category,
                                totalPages
                        );
                        books.add(book);
                    }
                }
            } else {
                // 응답 코드가 2xx가 아닌 경우 오류 로그 추가
                System.err.println("API 요청 실패, 상태 코드: " + response.getStatusCode());
            }
        } catch (Exception e) {
            // API 호출, JSON 파싱, 기타 예외 처리
            e.printStackTrace();
        }

        return books;
    }
}