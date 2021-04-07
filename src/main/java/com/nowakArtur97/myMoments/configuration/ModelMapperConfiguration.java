package com.nowakArtur97.myMoments.configuration;

import com.nowakArtur97.myMoments.feature.user.shared.UserConverter;
import com.nowakArtur97.myMoments.feature.user.registration.UserRegistrationDTO;
import com.nowakArtur97.myMoments.feature.user.shared.UserEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ModelMapperConfiguration {

    @Bean
    ModelMapper getModelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(UserRegistrationDTO.class, UserEntity.class).setPostConverter(UserConverter.userDTOConverter);

        return modelMapper;
    }
}
