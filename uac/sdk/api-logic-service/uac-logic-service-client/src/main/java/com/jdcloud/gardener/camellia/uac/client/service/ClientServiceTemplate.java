package com.jdcloud.gardener.camellia.uac.client.service;

import com.jdcloud.gardener.camellia.uac.client.atomic.ClientAtomicOperation;
import com.jdcloud.gardener.camellia.uac.client.event.schema.ClientChangedEvent;
import com.jdcloud.gardener.camellia.uac.client.event.schema.ClientCreatedEvent;
import com.jdcloud.gardener.camellia.uac.client.event.schema.ClientSecurityFieldChangedEvent;
import com.jdcloud.gardener.camellia.uac.client.event.schema.ClientStatusChangedEvent;
import com.jdcloud.gardener.camellia.uac.client.schema.criteria.ClientCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.entity.ClientEntityTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.request.*;
import com.jdcloud.gardener.camellia.uac.client.schema.response.ClientAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.client.schema.response.CreateClientResponse;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.GrantType;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.RedirectUri;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.RequireConsentFlag;
import com.jdcloud.gardener.camellia.uac.client.schema.trait.Scope;
import com.jdcloud.gardener.camellia.uac.client.skeleton.ClientSkeletons;
import com.jdcloud.gardener.camellia.uac.common.atomic.PasswordEncoder;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;
import com.jdcloud.gardener.camellia.uac.common.security.PasswordGenerator;
import com.jdcloud.gardener.camellia.uac.common.utils.GenericTypeUtils;
import com.jdcloud.gardener.fragrans.api.security.schema.Operator;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityFieldValueDetails;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityIdDetails;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.data.unique.HostIdGenerator;
import com.jdcloud.gardener.fragrans.data.unique.UniqueIdGenerator;
import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Update;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/11/14 18:55
 */
@RequiredArgsConstructor
@Slf4j
public class ClientServiceTemplate<
        C extends CreateClientParameterTemplate,
        S extends SearchClientCriteriaParameterTemplate,
        U extends UpdateClientParameterTemplate,
        A extends ClientAppearanceTemplate,
        T extends ClientEntityTemplate,
        I extends ClientCriteriaTemplate
        > implements ClientSkeletons.ManagementSkeleton<C, S, U, A>, ClientSkeletons.OpenApiSkeleton<A>, InitializingBean, ApplicationEventPublisherAware {
    private final HostIdGenerator hostIdGenerator;
    private final Converter<T, A> clientPoToVoConverter;
    private final Converter<S, DomainCriteriaWrapper<I>> searchClientCriteriaParameterToCriteriaConverter;
    private final Converter<C, T> createClientParameterToPoConverter;
    private final Converter<U, T> updateClientParameterToPoConverter;
    private final Operator operator;
    private final ClientAtomicOperation clientAtomicOperation;
    private final PasswordGenerator<? super T> passwordGenerator;
    private final PasswordEncoder<? super T> passwordEncoder;
    private final FieldScanner fieldScanner;
    private final GenericOperationLogger operationLogger;
    private UniqueIdGenerator clientIdGenerator;
    private ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateClientResponse createClient(@NonNull C createClientParameter) {
        //分配客户端id
        String clientId = clientIdGenerator.nextId("C");
        T client = Objects.requireNonNull(createClientParameterToPoConverter.convert(createClientParameter));
        //初始化密码
        client.setPassword(passwordGenerator.generate(client));
        //设置id
        client.setId(clientId);
        //写入创建人
        client.setCreator(operator.getUserId());
        //创建记录
        clientAtomicOperation.createClient(client, passwordEncoder);
        //清空密码
        client.setPassword(null);
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Create())
                        .state(new Done())
                        .detail(new EntityIdDetails<>(client.getId()))
                        .build(),
                null
        );
        //发送事件通知
        eventPublisher.publishEvent(new ClientCreatedEvent(client));
        return new CreateClientResponse(clientId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClient(@NonNull String clientId, @NonNull U updateClientParameter) {
        T client = Objects.requireNonNull(
                updateClientParameterToPoConverter
                        .convert(updateClientParameter)
        );
        //设置id
        client.setId(clientId);
        //设置更新人
        client.setUpdater(operator.getUserId());
        //执行更新
        T before = clientAtomicOperation.updateClient(client);
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityIdDetails<>(clientId)).build(),
                null
        );
        //发送事件
        eventPublisher.publishEvent(new ClientChangedEvent(
                before,
                client
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableClient(@NonNull String clientId) {
        changeClientEnableStatus(clientId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableClient(@NonNull String clientId) {
        changeClientEnableStatus(clientId, false);
    }

    private void changeClientEnableStatus(String clientId, boolean status) {
        boolean before = clientAtomicOperation.changeClientEnableStatus(clientId, status);
        if (before == status) {
            //没变化，跳过后面的逻辑
            return;
        }
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                clientId,
                                fieldScanner.field(ClientEntityTemplate.class, GenericTraits.StatusTraits.EnableFlag.class),
                                status
                        ))
                        .build(),
                null
        );
        //发事件
        eventPublisher.publishEvent(new ClientStatusChangedEvent(
                clientId,
                before,
                status,
                fieldScanner.field(ClientEntityTemplate.class, GenericTraits.StatusTraits.EnableFlag.class)
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableClientAuthorizationAutoConsent(@Valid @NotBlank String clientId) {
        changeClientRequireConsentFlag(clientId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableClientAuthorizationAutoConsent(@Valid @NotBlank String clientId) {
        changeClientRequireConsentFlag(clientId, false);
    }

    private void changeClientRequireConsentFlag(String clientId, boolean flag) {
        boolean before = clientAtomicOperation.changeClientRequireConsentFlag(clientId, flag);
        if (before == flag) {
            //没变化，跳过后面的逻辑
            return;
        }
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                clientId,
                                fieldScanner.field(ClientEntityTemplate.class, RequireConsentFlag.class),
                                flag
                        ))
                        .build(),
                null
        );
        //发布事件
        eventPublisher.publishEvent(new ClientSecurityFieldChangedEvent<>(
                clientId,
                before,
                flag,
                fieldScanner.field(ClientEntityTemplate.class, RequireConsentFlag.class)
        ));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeClientScope(@NonNull String clientId, @NonNull ChangeScopeParameter changeScopeParameter) {
        Collection<String> after = changeScopeParameter.getScope();
        Collection<String> before = clientAtomicOperation.changeClientScope(clientId, after);
        if (Objects.equals(before, after)) {
            //两者没区别
            return;
        }
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                clientId,
                                fieldScanner.field(ClientEntityTemplate.class, Scope.class),
                                after
                        ))
                        .build(),
                null
        );
        //发布事件
        eventPublisher.publishEvent(new ClientSecurityFieldChangedEvent<>(
                clientId,
                before,
                after,
                fieldScanner.field(ClientEntityTemplate.class, Scope.class)
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeClientGrantType(@NonNull String clientId, @NonNull ChangeGrantTypeParameter changeGrantTypeParameter) {
        Collection<String> after = changeGrantTypeParameter.getGrantType();
        Collection<String> before = clientAtomicOperation.changeClientGrantType(clientId, after);
        if (Objects.equals(before, after)) {
            //两者没区别
            return;
        }
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                clientId,
                                fieldScanner.field(ClientEntityTemplate.class, GrantType.class),
                                after
                        ))
                        .build(),
                null
        );
        //发布事件
        eventPublisher.publishEvent(new ClientSecurityFieldChangedEvent<>(
                clientId,
                before,
                after,
                fieldScanner.field(ClientEntityTemplate.class, GrantType.class)
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeClientRedirectUri(@NonNull String clientId, @NonNull ChangeRedirectUriParameter changeRedirectUriParameter) {
        Collection<String> after = changeRedirectUriParameter.getRedirectUri();
        Collection<String> before = clientAtomicOperation.changeClientRedirectUri(clientId, after);
        if (Objects.equals(before, after)) {
            //两者没区别
            return;
        }
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ClientEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                clientId,
                                fieldScanner.field(ClientEntityTemplate.class, RedirectUri.class),
                                after
                        ))
                        .build(),
                null
        );
        //发布事件
        eventPublisher.publishEvent(new ClientSecurityFieldChangedEvent<>(
                clientId,
                before,
                after,
                fieldScanner.field(ClientEntityTemplate.class, RedirectUri.class)
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SearchResponse<A> searchClient(@Valid S searchClientCriteriaParameter, @Valid PaginationParameter paginationParameter) {
        DomainCriteriaWrapper<I> criteriaWrapper = searchClientCriteriaParameterToCriteriaConverter.convert(searchClientCriteriaParameter);
        GenericQueryResult<T> queryResult = clientAtomicOperation.searchClient(
                Objects.requireNonNull(criteriaWrapper).getCriteria(),
                criteriaWrapper.getMust(),
                criteriaWrapper.getShould(),
                paginationParameter.getPageNo(),
                paginationParameter.getPageSize()
        );
        return SearchResponse.<A>builder()
                .total(queryResult.getTotal())
                .contents(queryResult.getContents().stream().map(
                        clientEntityTemplate -> clientPoToVoConverter.convert(GenericTypeUtils.cast(clientEntityTemplate))
                ).collect(Collectors.toList()))
                .build();
    }

    @Override
    public A authenticate(@Valid AuthenticateClientParameter parameter) {
        return null;
    }

    @Override
    public A readClient(@Valid String clientId) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clientIdGenerator = new UniqueIdGenerator(hostIdGenerator.getHostId());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
