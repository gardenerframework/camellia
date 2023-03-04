package io.gardenerframework.camellia.authentication.server.user;

import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.user.schema.response.UserAppearance;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DemoUserVoConverter implements Converter<User, UserAppearance> {
    @Nullable
    @Override
    public UserAppearance convert(@NonNull User source) {
        return new UserAppearance(source.getId(), source.getName(), source.getAvatar());
    }
}
