package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.BookInfo;
import org.csu.bj.persistence.BookInfoMapper;
import org.csu.bj.service.BookInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookInfoServiceImpl implements BookInfoService {
    @Autowired
    BookInfoMapper bookInfoMapper;
    @Override
    public BookInfo enterBook(BookInfo bookInfo){
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isbn",bookInfo.getIsbn());
        BookInfo bookInfo1 = bookInfoMapper.selectOne(queryWrapper);
        if(bookInfo1 == null){
//            新插入；
            return this.firstAddBook(bookInfo);
        } else {
//            库里已有，数量+1;
            return this.againAddBook(bookInfo);
        }
    }
    @Override
    public BookInfo firstAddBook(BookInfo bookInfo){
        bookInfo.setAllNumber(1);
        bookInfo.setRemainNumber(1);
        bookInfoMapper.insert(bookInfo);
        return bookInfo;
    }
    @Override
    public BookInfo againAddBook(BookInfo bookInfo){
        bookInfo.setRemainNumber(bookInfo.getRemainNumber()+1);
        bookInfo.setAllNumber(bookInfo.getAllNumber()+1);
        bookInfoMapper.updateBookInfo(bookInfo.getIsbn(),bookInfo.getAllNumber()+1,bookInfo.getRemainNumber()+1);
        return bookInfo;
    }
    @Override
    public List<BookInfo> getAllBooks(){
        return bookInfoMapper.getAllBook();
    }
    @Override
    public List<BookInfo> getBooksStartWithClass(String classification){
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("SUBSTRING(classification, 1, 1)", classification);
        return bookInfoMapper.selectList(queryWrapper);
    }
    @Override
    public List<BookInfo> getBooksWithNullClassification(){
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("classification");
        return bookInfoMapper.selectList(queryWrapper);
    }
    @Override
    public BookInfo updateBook(BookInfo bookInfo){
        bookInfoMapper.updateById(bookInfo);
        return bookInfo;
    }
    @Override
    public void deleteBook(int id){
        bookInfoMapper.delectBook(id);
    }
}
