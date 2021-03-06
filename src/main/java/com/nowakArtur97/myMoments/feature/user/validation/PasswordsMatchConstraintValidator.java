package com.nowakArtur97.myMoments.feature.user.validation;

import com.nowakArtur97.myMoments.feature.user.resource.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatch, UserDTO> {

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {

        return user.getPassword().equals(user.getMatchingPassword());
    }
}
