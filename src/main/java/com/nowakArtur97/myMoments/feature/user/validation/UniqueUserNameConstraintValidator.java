package com.nowakArtur97.myMoments.feature.user.validation;

import com.nowakArtur97.myMoments.feature.user.registration.UserRegistrationService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueUserNameConstraintValidator implements ConstraintValidator<UniqueUserName, String> {

    private final UserRegistrationService userRegistrationService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        return !userRegistrationService.isUsernameAlreadyInUse(username);
    }
}