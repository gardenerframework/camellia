package com.jdcloud.gardener.camellia.uac.application.exception.client;

import com.jdcloud.gardener.camellia.uac.application.exception.ApplicationExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhanghan30
 * @date 2022/11/7 13:48
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApplicationDisabledException extends ApplicationExceptions.ClientSideException {
    public ApplicationDisabledException(String applicationId) {
        super(applicationId);
    }
}
