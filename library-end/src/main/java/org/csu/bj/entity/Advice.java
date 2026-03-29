package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value="advice")
public class Advice {
    @TableId(value = "id",type= IdType.INPUT)
    private int id;
    private String username;
    private String title;
    private Date date;
    private String content;
    private String imgurl;
    @TableField("is_reply")
    private int isReply;
}
