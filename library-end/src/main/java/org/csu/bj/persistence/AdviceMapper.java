package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.csu.bj.entity.Advice;
import org.csu.bj.entity.Notice;

import java.util.List;

@Mapper
public interface AdviceMapper extends BaseMapper<Advice> {
    @Select("select * from advice")
    List<Advice> selectAll();
    @Select("select * from advice where is_reply = 0")
    List<Advice> selectAllNotSolve();
    @Update("UPDATE advice SET is_reply = 1 WHERE id = #{id}")
    int updateIsReply(
            @Param("id") int id
    );
}
