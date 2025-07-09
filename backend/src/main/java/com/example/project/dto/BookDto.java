package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {
    private String title;        // 책 제목
    private String author;       // 저자
    private String publisher;    // 출판사
    private String pubDate;      // 출판일 (YYYY-MM-DD)
    private String isbn;         // ISBN
    private String imageUrl;     // 책 표지 이미지 URL
    private String link;         // 알라딘 책 상세 페이지 URL
    private String category;     // 장르
    private int totalPages;      // 전체 페이지 수

    public BookDto(String title, String author, String publisher, String pubDate, String isbn, 
                   String imageUrl, String link, String category, int totalPages) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.isbn = isbn;
        this.imageUrl = imageUrl;
        this.link = link;
        this.category = category;
        this.totalPages = totalPages;
    }
}