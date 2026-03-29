package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("lecture")
public class Lecture {
    @TableId(value = "id",type= IdType.INPUT)
    private int id;
    private String title;
    private String content;
    private String imgurl;
    private String date;
    private String address;
    private String moderator;
    private String location;
}
