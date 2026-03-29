package org.csu.bj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Notice;

import java.util.ArrayList;
import java.util.List;

public interface NoticeService extends IService<Notice> {
    Notice AddPosts(Notice notice);
    int DeleteOne(Integer id);
    List<Notice> GetNoticeAll();
    Result updateNotice(Notice notice);
}
