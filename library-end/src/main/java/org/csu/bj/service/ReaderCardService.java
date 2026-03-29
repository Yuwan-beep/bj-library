package org.csu.bj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.ReaderCard;

public interface ReaderCardService extends IService<ReaderCard> {
    ReaderCard login(String username, String password);
    int register(String username, String password,String phone);
    int getId(String username);
    Result getUsers();
}
