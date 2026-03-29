package org.csu.bj.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class LendDetailVO {
    // lend_list 表的字段
    private Long id;
    @TableField("lend_date")
    private Date lendDate;
    @TableField("back_date")
    private Date backDate;
    @TableField("is_lending")
    private int isLending;

    // book_info 表的字段
    private String name;

    // reader_card 表的字段
    private String username;
    private String phone;
}
