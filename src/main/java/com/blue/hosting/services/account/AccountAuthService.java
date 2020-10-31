package com.blue.hosting.services.account;

import com.blue.hosting.entity.AccountInfoDAO;
import com.blue.hosting.entity.AccountInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("accountAuthSerivce")
public class AccountAuthService implements UserDetailsService {
    private AccountInfoRepo mAccountInfoRepo;

    @Resource(name="accountInfoRepo")
    private void setAccountInfoRepo(AccountInfoRepo accountInfoRepo){
        accountInfoRepo = accountInfoRepo;
    }

    private AccountInfoDAO findById(String id) throws Exception{
        AccountInfoDAO accountInfo = null;
        try {
            accountInfo = mAccountInfoRepo.findById(id).get();
        } catch (Exception except){
            throw except;
        }
        return accountInfo;
    }

    @Override
    public AccountInfoVO loadUserByUsername(String accountId) throws UsernameNotFoundException {
        eSecurityVal securityErrorMsg = null;
        AccountInfoVO accountInfoVO = null;
        try {
            AccountInfoDAO accountInfo = findById(accountId);
            String userName = accountInfo.getmUsername();
            String password = accountInfo.getmPassword();
            accountInfoVO = new AccountInfoVO(userName, password);
        } catch(Exception except){
            eSecurityVal securityVal = eSecurityVal.INPUT_NOT_FOUND;
            throw new UsernameNotFoundException(securityVal.getmErrorMsg());
        }

        return accountInfoVO;
    }
}
