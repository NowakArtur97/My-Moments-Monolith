package com.nowakArtur97.myMoments.feature.user.registration;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, BasicUserValidationConstraints.class})
interface UserValidationGroupSequence {
}
