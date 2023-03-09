package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.common.annotation.AuthenticationServerEnginePreserved;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedException;
import io.gardenerframework.camellia.authentication.server.main.event.listener.annotation.CareForAuthenticationServerEnginePreservedPrincipal;
import io.gardenerframework.camellia.authentication.server.main.event.schema.AuthenticationFailedEvent;
import io.gardenerframework.camellia.authentication.server.main.event.schema.UserAboutToLoadEvent;
import io.gardenerframework.camellia.authentication.server.main.event.support.AuthenticationEventBuilder;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.Principal;
import io.gardenerframework.camellia.authentication.server.main.schema.subject.principal.UsernamePrincipal;
import io.gardenerframework.camellia.authentication.server.test.AuthenticationServerEngineTestApplication;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author ZhangHan
 * @date 2022/4/28 0:13
 */
@SpringBootTest(classes = AuthenticationServerEngineTestApplication.class)
@DisplayName("事件上下文验证")
@Import({EventListenerTest.NoCamelliaListener.class, EventListenerTest.CamelliaListener.class})
public class EventListenerTest implements ApplicationEventPublisherAware, AuthenticationEventBuilder {
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private NoCamelliaListener noCamelliaPrincipalListener;
    @Autowired
    private CamelliaListener camelliaPrincipalListener;

    @Test
    @DisplayName("简单冒烟测试")
    public void simpleSmokeTest() {
        applicationEventPublisher.publishEvent(
                buildAuthenticationEvent(
                        UserAboutToLoadEvent.builder(),
                        new DummyRequest(),
                        "",
                        UsernamePrincipal.builder().name(UUID.randomUUID().toString()).build(),
                        null,
                        new HashMap<>()
                ).build());
        Assertions.assertTrue(noCamelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
        Assertions.assertTrue(camelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaPrincipalEventReceived());
        Assertions.assertFalse(camelliaPrincipalListener.isCamelliaPrincipalEventReceived());
        noCamelliaPrincipalListener.reset();
        camelliaPrincipalListener.reset();
        applicationEventPublisher.publishEvent(
                buildAuthenticationEvent(
                        UserAboutToLoadEvent.builder(),
                        new DummyRequest(),
                        "",
                        TestPreservedPrincipal.builder().name("").build(),
                        null,
                        new HashMap<>()
                ).build());
        Assertions.assertFalse(noCamelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaPrincipalEventReceived());
        Assertions.assertTrue(camelliaPrincipalListener.isCamelliaPrincipalEventReceived());
        Assertions.assertFalse(camelliaPrincipalListener.isNonCamelliaPrincipalEventReceived());
        noCamelliaPrincipalListener.reset();
        camelliaPrincipalListener.reset();
        applicationEventPublisher.publishEvent(
                buildAuthenticationEvent(
                        AuthenticationFailedEvent.builder(),
                        new DummyRequest(),
                        "",
                        UsernamePrincipal.builder().name("").build(),
                        null,
                        new HashMap<>()
                ).exception(new AuthenticationServiceException("")).build());
        Assertions.assertTrue(noCamelliaPrincipalListener.isNonCamelliaExReceived());
        Assertions.assertTrue(camelliaPrincipalListener.isNonCamelliaExReceived());
        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaExReceived());
        Assertions.assertFalse(camelliaPrincipalListener.isCamelliaExReceived());
        noCamelliaPrincipalListener.reset();
        camelliaPrincipalListener.reset();
        applicationEventPublisher.publishEvent(
                buildAuthenticationEvent(
                        AuthenticationFailedEvent.builder(),
                        new DummyRequest(),
                        "",
                        UsernamePrincipal.builder().name("").build(),
                        null,
                        new HashMap<>()
                ).exception(new TestPreservedException()).build());
        Assertions.assertFalse(noCamelliaPrincipalListener.isCamelliaExReceived());
        Assertions.assertFalse(noCamelliaPrincipalListener.isNonCamelliaExReceived());
        Assertions.assertTrue(camelliaPrincipalListener.isCamelliaExReceived());
        Assertions.assertFalse(camelliaPrincipalListener.isNonCamelliaExReceived());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Getter
    public static class NoCamelliaListener {
        private boolean camelliaPrincipalEventReceived;
        private boolean nonCamelliaPrincipalEventReceived;
        private boolean camelliaExReceived;
        private boolean nonCamelliaExReceived;

        public void reset() {
            this.camelliaPrincipalEventReceived = false;
            this.nonCamelliaPrincipalEventReceived = false;
            this.camelliaExReceived = false;
            this.nonCamelliaExReceived = false;
        }

        @EventListener
        public void onEvent(UserAboutToLoadEvent event) {
            if (AnnotationUtils.findAnnotation(event.getPrincipal().getClass(), AuthenticationServerEnginePreserved.class) != null) {
                this.camelliaPrincipalEventReceived = true;
            } else {
                this.nonCamelliaPrincipalEventReceived = true;
            }
        }

        @EventListener
        public void onException(AuthenticationFailedEvent event) {
            if (AnnotationUtils.findAnnotation(event.getException().getClass(), AuthenticationServerEnginePreserved.class) != null) {
                this.camelliaExReceived = true;
            } else {
                this.nonCamelliaExReceived = true;
            }
        }
    }

    @Getter
    public static class CamelliaListener {
        private boolean camelliaPrincipalEventReceived;
        private boolean nonCamelliaPrincipalEventReceived;
        private boolean camelliaExReceived;
        private boolean nonCamelliaExReceived;

        @EventListener
        @CareForAuthenticationServerEnginePreservedPrincipal
        public void onEvent(UserAboutToLoadEvent event) {
            if (AnnotationUtils.findAnnotation(event.getPrincipal().getClass(), AuthenticationServerEnginePreserved.class) != null) {
                this.camelliaPrincipalEventReceived = true;
            } else {
                this.nonCamelliaPrincipalEventReceived = true;
            }
        }

        @EventListener
        @CareForAuthenticationServerEnginePreservedException
        public void onException(AuthenticationFailedEvent event) {
            if (AnnotationUtils.findAnnotation(event.getException().getClass(), AuthenticationServerEnginePreserved.class) != null) {
                this.camelliaExReceived = true;
            } else {
                this.nonCamelliaExReceived = true;
            }
        }

        public void reset() {
            this.camelliaPrincipalEventReceived = false;
            this.nonCamelliaPrincipalEventReceived = false;
            this.camelliaExReceived = false;
            this.nonCamelliaExReceived = false;
        }
    }

    @AuthenticationServerEnginePreserved
    @SuperBuilder
    public static class TestPreservedPrincipal extends Principal {
    }

    @AuthenticationServerEnginePreserved
    public static class TestPreservedException extends AuthenticationException {

        public TestPreservedException() {
            super("");
        }
    }

    private static class DummyRequest implements HttpServletRequest {

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String username, String password) throws ServletException {

        }

        @Override
        public void logout() throws ServletException {

        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {

        }

        @Override
        public void removeAttribute(String name) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }
    }
}
