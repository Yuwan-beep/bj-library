package org.csu.bj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.csu.bj.api.vo.Result;
import org.csu.bj.entity.LendList;
import org.csu.bj.vo.LendDetailVO;

import java.util.List;

public interface LendListService extends IService<LendList> {
    Result lendBook(String isbn,String username);
    Result backBook(String isbn,String username);
    List<LendDetailVO> getLendDetails();
}
