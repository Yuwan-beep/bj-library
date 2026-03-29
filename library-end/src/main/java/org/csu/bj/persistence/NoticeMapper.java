package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.csu.bj.entity.Notice;

import java.util.Date;
import java.util.List;
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    @Select("select * from notice")
    List<Notice> selectAll();
    @Update("UPDATE notice SET title=#{title}, content=#{content}, imgurl=#{imgurl}, date=#{date} WHERE id=#{id}")
    int updateByNotice(@Param("id") int id,
                       @Param("title") String title,
                       @Param("imgurl") String imgurl,
                       @Param("date")Date date,
                       @Param("content") String content);
}
