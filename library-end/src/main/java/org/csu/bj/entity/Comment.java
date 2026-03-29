package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Comment {
    private int id;
    @TableField("reply_person")
    private String replyPerson;
    @TableField("advice_id")
    private int adviceId;
    private String content;
}
