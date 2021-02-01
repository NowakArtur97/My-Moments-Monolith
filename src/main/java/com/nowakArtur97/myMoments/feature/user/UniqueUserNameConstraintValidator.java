package com.nowakArtur97.myMoments.feature.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUserNameConstraintValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        return userService.findByEmail(email).isEmpty();
    }
}