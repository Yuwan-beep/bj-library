package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.bj.entity.Comment;
import org.csu.bj.persistence.AdviceMapper;
import org.csu.bj.persistence.CommentMapper;
import org.csu.bj.result.Result;
import org.csu.bj.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    AdviceMapper adviceMapper;

    @Override
    public Result addComment(Comment comment) {
        try{
            commentMapper.insert(comment);
            return new Result(200);
        } catch(Exception e){
            e.printStackTrace();
            return new Result(400);
        }
    }

    @Override
    public List<Comment> getComments(int adviceId) {
        try{
            return commentMapper.selectCommentByAdviceId(adviceId);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
