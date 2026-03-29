package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value="book_info")
public class BookInfo {
    @TableId(value = "book_id",type= IdType.INPUT)
    private int bookId;
    private String name;
    private String author;
    private String publish;
    private String isbn;
    private String introduction;
    private String language;
    private String price;
    @TableField("pub_date")
    private String pubDate;
    private String classification;
    @TableField("all_number")
    private int allNumber;
    @TableField("remain_number")
    private int remainNumber;
    private String imgurl;
    @TableField("cumulative_borrow")
    private int cumulativeBorrow;
}
