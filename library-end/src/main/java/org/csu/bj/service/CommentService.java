package org.csu.bj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.bj.entity.Comment;
import org.csu.bj.result.Result;

import java.util.List;

public interface CommentService extends IService<Comment> {
    Result addComment(Comment comment);

    List<Comment> getComments(int adviceId);
}
