package com.nowakArtur97.myMoments.feature.user.validation;

import com.nowakArtur97.myMoments.feature.user.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueUserNameConstraintValidator implements ConstraintValidator<UniqueUserName, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        return userService.findByUsername(email).isEmpty();
    }
}