package com.trading.services;

import com.trading.domain.VerificationType;
import com.trading.entities.User;
import com.trading.entities.VerificationCode;

public interface VerificationCodeService {

    VerificationCode getVerificationCode(Long id) throws Exception;

    VerificationCode getVerificationCodeByUser(User user);

    void deleteVerificationCodeById(VerificationCode verificationCode);

    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    
}