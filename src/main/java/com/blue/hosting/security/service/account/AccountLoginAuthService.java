package com.blue.hosting.security.service.account;

import com.blue.hosting.entity.account.AccountInfoDAO;
import com.blue.hosting.entity.account.AccountInfoRepo;
import com.blue.hosting.entity.account.AccountInfoVO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Component("accountLoginAuthSerivce")
public class AccountLoginAuthService implements UserDetailsService {
    private AccountInfoRepo mAccountInfoRepo;

    @Resource(name="accountInfoRepo")
    private void setAccountInfoRepo(AccountInfoRepo accountInfoRepo){
        mAccountInfoRepo = accountInfoRepo;
    }

    private AccountInfoDAO findById(String id) {
        AccountInfoDAO accountInfo = null;
        try {
            accountInfo = mAccountInfoRepo.findById(id).get();
        } catch (Exception except){
            return null;
        }
        return accountInfo;
    }

    @Override
    public AccountInfoDAO loadUserByUsername(String accountId) throws UsernameNotFoundException {
        AccountInfoDAO accountInfo = findById(accountId);
        if(accountInfo == null){
            throw new UsernameNotFoundException(accountId);
        }

        return accountInfo;
    }
}
