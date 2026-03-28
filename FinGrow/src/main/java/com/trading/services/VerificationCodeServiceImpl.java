package com.trading.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.VerificationCodeRepo;
import com.trading.domain.VerificationType;
import com.trading.entities.User;
import com.trading.entities.VerificationCode;
//import com.trading.utils.OtpUtils;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeRepo verificationCodeRepo;

    @Override
    public VerificationCode getVerificationCode(Long id) throws Exception {
        Optional<VerificationCode> verificationCode = verificationCodeRepo.findById(id);

        if (verificationCode.isEmpty()) {
            throw new Exception("verification code not found");
        }

        return verificationCode.get();
    }

    @Override
    public VerificationCode getVerificationCodeByUser(User user) {
        return verificationCodeRepo.findByUser_Id(user.getId());
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
        verificationCodeRepo.delete(verificationCode);
    }

    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {

        VerificationCode verificationCode = new VerificationCode();
       // verificationCode.setOtp(OtpUtils.generateOTP());
        verificationCode.setVetificationType(verificationType);
        verificationCode.setUser(user);

        return verificationCodeRepo.save(verificationCode);
    }
}