package com.nowakArtur97.myMoments.configuration;

import com.nowakArtur97.myMoments.feature.user.UserConverter;
import com.nowakArtur97.myMoments.feature.user.UserDTO;
import com.nowakArtur97.myMoments.feature.user.UserEntity;
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

        modelMapper.addConverter(UserConverter.userDTOConverter, UserDTO.class, UserEntity.class);

        return modelMapper;
    }
}
