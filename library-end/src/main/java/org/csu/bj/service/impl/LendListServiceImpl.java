package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Advice;
import org.csu.bj.entity.BookInfo;
import org.csu.bj.entity.LendList;
import org.csu.bj.entity.ReaderCard;
import org.csu.bj.persistence.BookInfoMapper;
import org.csu.bj.persistence.LendListMapper;
import org.csu.bj.persistence.ReaderCardMapper;
import org.csu.bj.service.BookInfoService;
import org.csu.bj.service.LendListService;
import org.csu.bj.vo.LendDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Date;
import java.util.List;

@Service
public class LendListServiceImpl extends ServiceImpl<LendListMapper, LendList> implements LendListService {
    @Autowired
    LendListMapper lendListMapper;
    @Autowired
    ReaderCardMapper readerCardMapper;
    @Autowired
    BookInfoMapper bookInfoMapper;
    @Autowired
    BookInfoService bookInfoService;
    @Override
    public Result lendBook(String isbn,String username){
        LendList lendList = new LendList();
        Date date = new Date(System.currentTimeMillis());
        lendList.setLendDate(date);
        QueryWrapper<ReaderCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        ReaderCard readerCard = readerCardMapper.selectOne(queryWrapper);
        int readerId = readerCard.getReaderId();
        QueryWrapper<BookInfo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("isbn", isbn);
        BookInfo bookInfo = bookInfoMapper.selectOne(queryWrapper2);
        if(bookInfo == null){
            return Result.failed("该书未入库!");
        }
        int remainNumber = bookInfo.getRemainNumber();
        if(remainNumber <= 0){
            return Result.failed("余书不足!");
        }
        int cumulativeBorrow = bookInfo.getCumulativeBorrow();
        bookInfo.setRemainNumber(remainNumber-1);
        bookInfo.setCumulativeBorrow(cumulativeBorrow+1);
        int bookId = bookInfo.getBookId();
        lendList.setBookId(bookId);
        lendList.setReaderId(readerId);
        lendListMapper.insert(lendList);
        bookInfoService.updateBook(bookInfo);
        return Result.success("借书成功!");
    }
    @Override
    public Result backBook(String isbn,String username){
        Date date = new Date(System.currentTimeMillis());
        QueryWrapper<ReaderCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        ReaderCard readerCard = readerCardMapper.selectOne(queryWrapper);
        int readerId = readerCard.getReaderId();
        QueryWrapper<BookInfo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("isbn", isbn);
        BookInfo bookInfo = bookInfoMapper.selectOne(queryWrapper2);
        if(bookInfo == null){
            return Result.failed("该书未入库!");
        }
        int remainNumber = bookInfo.getRemainNumber();
        int allNumber = bookInfo.getAllNumber();
        if(remainNumber>=allNumber){
            return Result.failed("该书已满!");
        }
        int bookId = bookInfo.getBookId();
        LendList lendList = lendListMapper.selectLendBook(bookId,readerId);
        if(lendList == null){
            return Result.failed("未找到借书记录!");
        }
        lendList.setBackDate(date);
        lendList.setIsLending(1);
        bookInfo.setRemainNumber(remainNumber+1);
        bookInfoService.updateBook(bookInfo);
        System.out.println(lendList);
        lendListMapper.updateById(lendList);
        return Result.success("还书成功!");
    }
    @Override
    public List<LendDetailVO> getLendDetails(){
        return lendListMapper.selectLendDetails();
    }
}
