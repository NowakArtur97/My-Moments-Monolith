package com.nowakArtur97.myMoments.feature.user.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, BasicUserValidationConstraints.class})
public interface UserValidationGroupSequence {
}
