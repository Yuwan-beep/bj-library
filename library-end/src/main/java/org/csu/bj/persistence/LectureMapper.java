package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.csu.bj.entity.Lecture;
import org.csu.bj.entity.Notice;

import java.util.List;

@Mapper
public interface LectureMapper extends BaseMapper<Lecture> {
    @Select("select * from lecture")
    List<Lecture> selectAll();
}
