package com.nowakArtur97.myMoments.feature.user.validation;

import com.nowakArtur97.myMoments.feature.user.registration.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        return userService.findByEmail(email).isEmpty();
    }
}