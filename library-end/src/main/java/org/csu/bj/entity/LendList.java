package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("lend_list")
public class LendList {
    @TableId(value = "id",type= IdType.INPUT)
    private int id;
    @TableField("book_id")
    private int bookId;
    @TableField("reader_id")
    private int readerId;
    @TableField("lend_date")
    private Date lendDate;
    @TableField("back_date")
    private Date backDate;
    @TableField("is_lending")
    private int isLending;
}
