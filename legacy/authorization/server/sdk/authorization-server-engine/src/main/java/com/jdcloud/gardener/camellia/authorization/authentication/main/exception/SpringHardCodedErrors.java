package com.jdcloud.gardener.camellia.authorization.authentication.main.exception;

import com.jdcloud.gardener.fragrans.log.annotation.LogTarget;
import com.jdcloud.gardener.fragrans.messages.MessageArgumentsSupplier;
import lombok.AllArgsConstructor;

/**
 * @author ZhangHan
 * @date 2022/5/20 0:33
 */
@LogTarget("Spring硬编码错误")
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
