package io.gardenerframework.camellia.authentication.server.main.exception;

import io.gardenerframework.fragrans.messages.MessageArgumentsSupplier;
import lombok.AllArgsConstructor;

/**
 * @author ZhangHan
 * @date 2022/5/20 0:33
 */
public interface SpringHardCodedErrors {
    @AllArgsConstructor
    class OAuth2ParameterError extends RuntimeException implements MessageArgumentsSupplier {
        private String parameterName;

        @Override
        public Object[] getMessageArguments() {
            return new Object[]{parameterName};
        }
    }

    @AllArgsConstructor
    class OAuth2ClientAuthenticationError extends RuntimeException implements MessageArgumentsSupplier {
        private String parameterName;

        @Override
        public Object[] getMessageArguments() {
            return new Object[]{parameterName};
        }
    }
}
