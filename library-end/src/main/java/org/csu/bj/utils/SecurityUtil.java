package org.csu.bj.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.csu.bj.entity.Admin;
import org.csu.bj.entity.ReaderCard;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Admin getLoginUser(){
        Subject user = SecurityUtils.getSubject();
        Admin admin = (Admin) user.getPrincipal();
        return admin;
    }
    public ReaderCard getLoginReader(){
        Subject user = SecurityUtils.getSubject();
        ReaderCard readerCard = (ReaderCard) user.getPrincipal();
        return readerCard;
    }
}
