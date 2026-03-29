package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value="notice")

public class Notice {
    @TableId
    private int id;
    private String title;
    private Date date;
    private String content;
    private String imgurl;
}
