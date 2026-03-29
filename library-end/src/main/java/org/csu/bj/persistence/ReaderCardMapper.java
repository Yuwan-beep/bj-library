package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.ReaderCard;

import java.util.List;

@Mapper
public interface ReaderCardMapper extends BaseMapper<ReaderCard> {
    @Select("select reader_id,username,phone,ban from reader_card")
    List<ReaderCard> getUsers();
}
