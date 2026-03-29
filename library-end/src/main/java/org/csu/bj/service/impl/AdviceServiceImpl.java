package org.csu.bj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Advice;
import org.csu.bj.entity.LendList;
import org.csu.bj.entity.Notice;
import org.csu.bj.entity.ReaderCard;
import org.csu.bj.persistence.AdminMapper;
import org.csu.bj.persistence.AdviceMapper;
import org.csu.bj.persistence.ReaderCardMapper;
import org.csu.bj.service.AdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class AdviceServiceImpl extends ServiceImpl<AdviceMapper, Advice> implements AdviceService {
    @Autowired
    AdviceMapper adviceMapper;
    @Autowired
    ReaderCardMapper readerCardMapper;
    @Override
    public Advice AddAdvices(Advice advice){
        Date date = new Date(System.currentTimeMillis());
        advice.setDate(date);
        adviceMapper.insert(advice);
        return advice;
    }
    @Override
    public int DeleteOne(Integer id){
        return adviceMapper.delete(new QueryWrapper<Advice>().eq("id",id));
    }
    @Override
    public List<Advice> GetAdvicesAllNotSolve(){
        return adviceMapper.selectAllNotSolve();
    }
    @Override
    public List<Advice> GetByUser(String username){
        QueryWrapper<Advice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return adviceMapper.selectList(queryWrapper);
    }
    @Override
    public Advice changeSolve(int id){
        adviceMapper.updateIsReply(id);
        return adviceMapper.selectById(id);
    }
    @Override
    public Result updateAdvice(Advice advice){
        try{
            adviceMapper.updateById(advice);
            return Result.success(advice);
        }catch (Exception e){
            return Result.failed("");
        }

    }
    @Override
    public List<Advice> getAll(){
        return adviceMapper.selectAll();
    }
}
