package org.csu.bj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.Advice;

import java.util.List;

public interface AdviceService extends IService<Advice> {
    Advice AddAdvices(Advice advice);
    int DeleteOne(Integer id);
    List<Advice> GetAdvicesAllNotSolve();
    List<Advice> GetByUser(String username);
    Advice changeSolve(int id);
    Result updateAdvice(Advice advice);
    List<Advice> getAll();
}
