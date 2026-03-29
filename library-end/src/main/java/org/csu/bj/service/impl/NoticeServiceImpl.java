package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Notice;
import org.csu.bj.persistence.NoticeMapper;
import org.csu.bj.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.ResolverStyle;
import java.util.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper,Notice> implements NoticeService {
    @Autowired
    NoticeMapper noticeMapper;
    public Notice AddPosts(Notice notice){
        Date date = new Date(System.currentTimeMillis());
        notice.setDate(date);
        noticeMapper.insert(notice);
        return notice;
    }
    public int DeleteOne(Integer id){
        return noticeMapper.delete(new QueryWrapper<Notice>().eq("id",id));
    }
    public List<Notice> GetNoticeAll(){
        return noticeMapper.selectAll();
    }
    @Override
    public Result updateNotice(Notice notice){
        try {
            int updateByNotice = noticeMapper.updateByNotice(notice.getId(),notice.getTitle(),notice.getImgurl(),notice.getDate(),notice.getContent());
            if(updateByNotice != 0)
                return Result.success(notice);
            else
                return Result.failed("");
        }catch (Exception e){
            e.printStackTrace();
            return Result.failed("");
        }
    }
}
