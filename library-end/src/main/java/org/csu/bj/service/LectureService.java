package org.csu.bj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.bj.entity.Lecture;

import java.util.List;

public interface LectureService extends IService<Lecture> {
    Lecture AddLecture(Lecture lecture);
    int DeleteOne(Integer id);
    List<Lecture> GetLectureAll();
}
