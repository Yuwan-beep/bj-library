package org.csu.bj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("reader_card")
public class ReaderCard {
    @TableId(value = "reader_id",type= IdType.INPUT)
    private int readerId;
    private String username;
    private String password;
    private String phone;
    private int ban;
}
