package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.bj.entity.Lecture;
import org.csu.bj.entity.Notice;
import org.csu.bj.persistence.LectureMapper;
import org.csu.bj.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LectureServiceImpl extends ServiceImpl<LectureMapper, Lecture> implements LectureService {
    @Autowired
    LectureMapper lectureMapper;
    @Override
    public Lecture AddLecture(Lecture lecture){
        lectureMapper.insert(lecture);
        return lecture;
    }

    @Override
    public int DeleteOne(Integer id){
        return lectureMapper.delete(new QueryWrapper<Lecture>().eq("id",id));
    }
    @Override
    public List<Lecture> GetLectureAll(){
        return lectureMapper.selectAll();
    }
}
