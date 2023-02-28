//package com.jdcloud.gardener.camellia.authorization.test.cases;
//
//import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation.CareForAuthorizationEnginePreservedException;
//import com.jdcloud.gardener.camellia.authorization.authentication.main.event.listener.annotation.CareForAuthorizationEnginePreservedPrincipal;
//import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.AuthenticationFailedEvent;
//import com.jdcloud.gardener.camellia.authorization.authentication.main.event.schema.BeforeLoadingUserEvent;
//import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.BasicPrincipal;
//import com.jdcloud.gardener.camellia.authorization.authentication.main.schema.principal.UsernamePrincipal;
//import com.jdcloud.gardener.camellia.authorization.common.annotation.AuthorizationEnginePreserved;
//import com.jdcloud.gardener.camellia.authorization.test.AuthorizationServerEngineTestApplication;
//import lombok.Getter;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import org.springframework.context.annotation.Import;
//import org.springframework.context.event.EventListener;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.core.AuthenticationException;
//
//import java.util.UUID;
//
///**
// * @author ZhangHan
// * @date 2022/4/28 0:13
// */
//@SpringBootTest(classes = AuthorizationServerEngineTestApplication.class)
//@DisplayName("事件上下文验证")
//@Import({EventListenerTest.NoCamelliaListener.class, EventListenerTest.CamelliaListener.class})
//public class EventListenerTest implements ApplicationEventPublisherAware {
//    private ApplicationEventPublisher applicationEventPublisher;
//    @Autowired
//    private NoCamelliaListener noCamelliaPrincipalListener;
//    @Autowired
//    private CamelliaListener camelliaPrincipalListener;
//
//    @Test
//    @DisplayName("简单冒烟测试")
//    public void simpleSmokeTest() {
//        applicationEventPublisher.publishEvent(new BeforeLoadingUserEvent(null, null, new UsernamePrincipal(UUID.randomUUID().toString()), null, null, null));
//        Assertions.assertTrue(noCamelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
//        Assertions.assertTrue(camelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
//        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaPrincipalEventReceived());
//        Assertions.assertFalse(camelliaPrincipalListener.isCamelliaPrincipalEventReceived());
//        noCamelliaPrincipalListener.reset();
//        camelliaPrincipalListener.reset();
//        applicationEventPublisher.publishEvent(new BeforeLoadingUserEvent(null, null, new TestPreservedPrincipal(), null, null, null));
//        Assertions.assertFalse(noCamelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
//        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaPrincipalEventReceived());
//        Assertions.assertTrue(camelliaPrincipalListener.isCamelliaPrincipalEventReceived());
//        Assertions.assertFalse(camelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
//        noCamelliaPrincipalListener.reset();
//        camelliaPrincipalListener.reset();
//        applicationEventPublisher.publishEvent(new AuthenticationFailedEvent(null, null, new UsernamePrincipal(""), null, null, null, null, new AuthenticationServiceException("")));
//        Assertions.assertTrue(noCamelliaPrincipalListener.isNonCamelliaExReceived());
//        Assertions.assertTrue(camelliaPrincipalListener.isNonCamelliaExReceived());
//        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaExReceived());
//        Assertions.assertFalse(camelliaPrincipalListener.isCamelliaExReceived());
//        noCamelliaPrincipalListener.reset();
//        camelliaPrincipalListener.reset();
//        applicationEventPublisher.publishEvent(new AuthenticationFailedEvent(null, null, new UsernamePrincipal(""), null, null, null, null, new TestPreservedException()));
//        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaExReceived());
//        Assertions.assertFalse(noCamelliaPrincipalListener.isNonCamelliaExReceived());
//        Assertions.assertTrue(camelliaPrincipalListener.isCamelliaExReceived());
//        Assertions.assertFalse(camelliaPrincipalListener.isNonCamelliaExReceived());
//    }
//
//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//
//    @Getter
//    public static class NoCamelliaListener {
//        private boolean camelliaPrincipalEventReceived;
//        private boolean nonCamelliaPrincipalEventReceived;
//        private boolean camelliaExReceived;
//        private boolean nonCamelliaExReceived;
//
//        public void reset() {
//            this.camelliaPrincipalEventReceived = false;
//            this.nonCamelliaPrincipalEventReceived = false;
//            this.camelliaExReceived = false;
//            this.nonCamelliaExReceived = false;
//        }
//
//        @EventListener
//        public void onEvent(BeforeLoadingUserEvent event) {
//            if (AnnotationUtils.findAnnotation(event.getPrincipal().getClass(), AuthorizationEnginePreserved.class) != null) {
//                this.camelliaPrincipalEventReceived = true;
//            } else {
//                this.nonCamelliaPrincipalEventReceived = true;
//            }
//        }
//
//        @EventListener
//        public void onException(AuthenticationFailedEvent event) {
//            if (AnnotationUtils.findAnnotation(event.getException().getClass(), AuthorizationEnginePreserved.class) != null) {
//                this.camelliaExReceived = true;
//            } else {
//                this.nonCamelliaExReceived = true;
//            }
//        }
//    }
//
//    @Getter
//    public static class CamelliaListener {
//        private boolean camelliaPrincipalEventReceived;
//        private boolean nonCamelliaPrincipalEventReceived;
//        private boolean camelliaExReceived;
//        private boolean nonCamelliaExReceived;
//
//        @EventListener
//        @CareForAuthorizationEnginePreservedPrincipal
//        public void onEvent(BeforeLoadingUserEvent event) {
//            if (AnnotationUtils.findAnnotation(event.getPrincipal().getClass(), AuthorizationEnginePreserved.class) != null) {
//                this.camelliaPrincipalEventReceived = true;
//            } else {
//                this.nonCamelliaPrincipalEventReceived = true;
//            }
//        }
//
//        @EventListener
//        @CareForAuthorizationEnginePreservedException
//        public void onException(AuthenticationFailedEvent event) {
//            if (AnnotationUtils.findAnnotation(event.getException().getClass(), AuthorizationEnginePreserved.class) != null) {
//                this.camelliaExReceived = true;
//            } else {
//                this.nonCamelliaExReceived = true;
//            }
//        }
//
//        public void reset() {
//            this.camelliaPrincipalEventReceived = false;
//            this.nonCamelliaPrincipalEventReceived = false;
//            this.camelliaExReceived = false;
//            this.nonCamelliaExReceived = false;
//        }
//    }
//
//    @AuthorizationEnginePreserved
//    public static class TestPreservedPrincipal extends BasicPrincipal {
//
//        public TestPreservedPrincipal() {
//            super("");
//        }
//    }
//
//    @AuthorizationEnginePreserved
//    public static class TestPreservedException extends AuthenticationException {
//
//        public TestPreservedException() {
//            super("");
//        }
//    }
//}
