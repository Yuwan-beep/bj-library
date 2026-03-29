package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.bj.entity.LendList;
import org.csu.bj.vo.LendDetailVO;

import java.util.List;

@Mapper
public interface LendListMapper extends BaseMapper<LendList> {
    @Select("select * from lend_list where book_id = #{book_id} and reader_id = #{reader_id} and is_lending = 0")
    LendList selectLendBook(@Param("book_id") int book_id,
                            @Param("reader_id") int reader_id
    );
    @Select("SELECT *" +
            "FROM lend_list ll " +
            "LEFT JOIN book_info bi ON ll.book_id = bi.book_id " +
            "LEFT JOIN reader_card rc ON ll.reader_id = rc.reader_id")
    List<LendDetailVO> selectLendDetails();
}
