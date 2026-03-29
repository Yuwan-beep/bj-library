package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.ReaderCard;
import org.csu.bj.persistence.ReaderCardMapper;
import org.csu.bj.service.ReaderCardService;
import org.csu.bj.utils.SaltMD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReaderCardServiceImpl extends ServiceImpl<ReaderCardMapper, ReaderCard> implements ReaderCardService {
    @Autowired
    ReaderCardMapper readerCardMapper;
    @Override
    public ReaderCard login(String username, String password){
        if(username.length() == 0 || password.length() == 0 ){
            return null;
        }
        QueryWrapper<ReaderCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        ReaderCard readerCard = readerCardMapper.selectOne(queryWrapper);
        String saltPassword = readerCard.getPassword();
        boolean passwordFlag = SaltMD5Util.verifySaltPassword(password, saltPassword);
        if(!passwordFlag){
            return null;
        }
        return readerCard;
    }
    @Override
    public int register(String username, String password,String phone){
        QueryWrapper<ReaderCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        ReaderCard readerCard1 = readerCardMapper.selectOne(queryWrapper);
        if(readerCard1 != null){
            return -1;
        }
        QueryWrapper<ReaderCard> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("phone",phone);
        ReaderCard readerCard2 = readerCardMapper.selectOne(queryWrapper2);
        if (readerCard2 != null){
            return -2;
        }
        ReaderCard readerCard = new ReaderCard();
        readerCard.setUsername(username);
        readerCard.setPassword(SaltMD5Util.generateSaltPassword(password));
        readerCard.setPhone(phone);
        return readerCardMapper.insert(readerCard);
    }
    @Override
    public int getId(String username){
        QueryWrapper<ReaderCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        ReaderCard readerCard = readerCardMapper.selectOne(queryWrapper);
        return readerCard.getReaderId();
    }
    @Override
    public Result getUsers(){
        List<ReaderCard> readerCardList = readerCardMapper.getUsers();
        return Result.success(readerCardList);
    }
}
