package org.csu.bj.service;

import org.csu.bj.entity.BookInfo;

import java.util.List;

public interface BookInfoService {
    BookInfo enterBook(BookInfo bookInfo);
    BookInfo firstAddBook(BookInfo bookInfo);
    BookInfo againAddBook(BookInfo bookInfo);
    List<BookInfo> getAllBooks();
    List<BookInfo> getBooksStartWithClass(String classification);
    List<BookInfo> getBooksWithNullClassification();
    BookInfo updateBook(BookInfo bookInfo);
    void deleteBook(int id);
}
