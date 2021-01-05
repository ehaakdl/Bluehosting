package com.blue.hosting.service.account;

import com.blue.hosting.entity.account.AccountInfoDAO;
import com.blue.hosting.entity.account.AccountInfoRepo;
import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.security.eRoleName;
import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.security.exception.CustomAuthenticationException;
import com.blue.hosting.security.exception.eAuthenticationException;
import com.blue.hosting.security.exception.eSystemException;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.token.JwtTokenManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import org.hibernate.exception.DataException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Service("accountManagement")
public class AccountManagement {
    private AccountInfoRepo mAccountInfoRepo;

    @Resource(name = "accountInfoRepo")
    private void setAccountInfoRepo(AccountInfoRepo accountInfoRepo) {
        mAccountInfoRepo = accountInfoRepo;
    }

    public boolean findById(String id) {
        try {
            mAccountInfoRepo.findById(id).get();
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    protected AccountInfoDAO insertAccountInfo(AccountInfoDAO accountInfoDAO) throws Exception{
        return mAccountInfoRepo.save(accountInfoDAO);
    }

    public boolean create(AccountInfoVO accountInfoVO) {
        boolean bResult = findById(accountInfoVO.getId());
        if (bResult == true) {
            return false;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        eRoleName roleName = eRoleName.ROLE_USER;
        String id = accountInfoVO.getId();
        String email = accountInfoVO.getEmail();
        String passwd = passwordEncoder.encode(accountInfoVO.getPasswd());
        AccountInfoDAO accountInfoDAO = new AccountInfoDAO(roleName.getmRole(), passwd, id, email);
        try {
            if (insertAccountInfo(accountInfoDAO).equals(accountInfoDAO) == false) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
