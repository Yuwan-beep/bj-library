package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.csu.bj.entity.Admin;

import java.util.List;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    @Select("select id,username,authority from admin")
    List<Admin> getAdmins();
    @Update("update admin set authority = #{authority} where id = #{id}")
    int updateAuthority(int authority,int id);
}
