package com.blue.hosting.service.account;

import java.util.List;
import com.blue.hosting.entity.account.AccountInfoDAO;
import com.blue.hosting.entity.account.AccountInfoRepo;
import com.blue.hosting.entity.email.EmailStateDAO;
import com.blue.hosting.entity.email.EmailStateRepo;
import com.blue.hosting.security.eRoleName;
import com.blue.hosting.entity.account.AccountInfoVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service("accountManagement")
public class AccountManagement {
    private final char YES_STORE_FLAG ='Y';

    private final char NO_AUTHENTICATED_FLAG = 'N';

    @Resource(name="emailStateRepo")
    public void setmEmailStateRepo(EmailStateRepo mEmailStateRepo) {
        this.mEmailStateRepo = mEmailStateRepo;
    }

    private EmailStateRepo mEmailStateRepo;

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

    @Transactional
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
            Optional<EmailStateDAO> optional = mEmailStateRepo.findById(email);
            EmailStateDAO emailStateDAO = optional.get();
            if(emailStateDAO.getAuthenticatedFlag() == NO_AUTHENTICATED_FLAG){
                throw new Exception();
            }
            List<AccountInfoDAO> list = mAccountInfoRepo.findByEmail(email);
            if(list.size() > 0){
                throw new Exception();
            }
            if (insertAccountInfo(accountInfoDAO).equals(accountInfoDAO) == false) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
