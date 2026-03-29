package org.csu.bj.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.csu.bj.entity.BookInfo;

import java.util.List;

@Mapper
public interface BookInfoMapper extends BaseMapper<BookInfo> {
    @Update("UPDATE book_info SET all_number = #{allNumber}, remain_number = #{remainNumber} WHERE isbn = #{isbn}")
    int updateBookInfo(
            @Param("isbn") String isbn,
            @Param("allNumber") int allNumber,
            @Param("remainNumber") int remainNumber
    );
    @Select("select * from book_info")
    List<BookInfo> getAllBook();
    @Delete("delete from book_info where book_id = #{id}")
    void delectBook(int id);
}
