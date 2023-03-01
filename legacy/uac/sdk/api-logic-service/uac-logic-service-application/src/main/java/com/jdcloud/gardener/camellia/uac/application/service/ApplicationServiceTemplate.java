package com.jdcloud.gardener.camellia.uac.application.service;

import com.jdcloud.gardener.camellia.uac.application.atomic.ApplicationAtomicOperationTemplate;
import com.jdcloud.gardener.camellia.uac.application.event.schema.ApplicationChangedEvent;
import com.jdcloud.gardener.camellia.uac.application.event.schema.ApplicationCreatedEvent;
import com.jdcloud.gardener.camellia.uac.application.event.schema.ApplicationStatusChangedEvent;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.CreateApplicationParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.SearchApplicationCriteriaParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.UpdateApplicationParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.response.ApplicationAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.application.skeleton.ApplicationSkeletons;
import com.jdcloud.gardener.camellia.uac.common.schema.criteria.DomainCriteriaWrapper;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;
import com.jdcloud.gardener.camellia.uac.common.utils.GenericTypeUtils;
import com.jdcloud.gardener.fragrans.api.security.schema.Operator;
import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityFieldValueDetails;
import com.jdcloud.gardener.fragrans.data.practice.log.schema.details.EntityIdDetails;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;
import com.jdcloud.gardener.fragrans.log.GenericOperationLogger;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Update;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhanghan30
 * @date 2022/11/9 11:48
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ApplicationServiceTemplate<
        C extends CreateApplicationParameterTemplate,
        S extends SearchApplicationCriteriaParameterTemplate,
        U extends UpdateApplicationParameterTemplate,
        A extends ApplicationAppearanceTemplate,
        T extends ApplicationEntityTemplate,
        I extends ApplicationCriteriaTemplate
        > implements
        ApplicationSkeletons.MethodPrototype.ReadApplication<A>,
        ApplicationSkeletons.MethodPrototype.CreateApplication<C>,
        ApplicationSkeletons.MethodPrototype.UpdateApplication<U>,
        ApplicationSkeletons.MethodPrototype.EnableApplication,
        ApplicationSkeletons.MethodPrototype.DisableApplication,
        ApplicationSkeletons.MethodPrototype.SearchApplication<S, A>,
        ApplicationEventPublisherAware {
    /**
     * 转换器组
     */
    private final Converter<C, T> createApplicationParameterToPoConverter;
    private final Converter<S, DomainCriteriaWrapper<I>> searchApplicationCriteriaParameterToCriteriaConverter;
    private final Converter<U, T> updateApplicationParameterToPoConverter;
    private final Converter<T, A> applicationPoToVoConverter;
    private final GenericOperationLogger operationLogger = new GenericOperationLogger();
    private final FieldScanner fieldScanner = new FieldScanner();
    /**
     * 原子操作
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private ApplicationAtomicOperationTemplate<T, I> applicationAtomicOperation;
    /**
     * 获取操作者详情
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private Operator operator;
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApplication(@NonNull @Valid C createApplicationParameter) {
        T applicationEntity = Objects.requireNonNull(createApplicationParameterToPoConverter
                .convert(createApplicationParameter));
        //设置创建人
        applicationEntity.setCreator(operator.getUserId());
        //创建记录
        applicationAtomicOperation.createApplication(applicationEntity);
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ApplicationEntityTemplate.class)
                        .operation(new Create())
                        .state(new Done())
                        .detail(new EntityIdDetails<>(applicationEntity.getId()))
                        .build(),
                null
        );
        //发送事件通知
        eventPublisher.publishEvent(new ApplicationCreatedEvent(applicationEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplication(@NonNull @Valid @NotBlank String applicationId, @Valid U updateApplicationParameter) {
        T application = Objects.requireNonNull(
                updateApplicationParameterToPoConverter
                        .convert(updateApplicationParameter)
        );
        //设置id
        application.setId(applicationId);
        //设置更新人
        application.setUpdater(operator.getUserId());
        //执行更新
        T before = applicationAtomicOperation.updateApplication(application);
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ApplicationEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityIdDetails<>(applicationId)).build(),
                null
        );
        //发送事件
        eventPublisher.publishEvent(new ApplicationChangedEvent(
                before,
                application
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableApplication(@Valid @NotBlank String applicationId,
                                  @Valid SecurityOperationParameter securityOperationParameter) {
        changeApplicationEnableStatus(applicationId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableApplication(@Valid @NotBlank String applicationId,
                                   @Valid SecurityOperationParameter securityOperationParameter) {
        changeApplicationEnableStatus(applicationId, false);
    }

    private void changeApplicationEnableStatus(String applicationId, boolean status) {
        boolean before = applicationAtomicOperation.changeApplicationEnableStatus(applicationId, status);
        if (before == status) {
            //没变化，跳过后面的逻辑
            return;
        }
        //记录日志
        operationLogger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ApplicationEntityTemplate.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new EntityFieldValueDetails<>(
                                applicationId,
                                fieldScanner.field(ApplicationEntityTemplate.class, GenericTraits.StatusTraits.EnableFlag.class),
                                status
                        ))
                        .build(),
                null
        );
        //发事件
        eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                applicationId,
                before,
                status,
                fieldScanner.field(ApplicationEntityTemplate.class, GenericTraits.StatusTraits.EnableFlag.class)
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SearchResponse<A> searchApplication(@NonNull @Valid S searchApplicationCriteriaParameter, @NonNull @Valid PaginationParameter paginationParameter) {
        DomainCriteriaWrapper<I> criteriaWrapper = Objects.requireNonNull(
                searchApplicationCriteriaParameterToCriteriaConverter
                        .convert(searchApplicationCriteriaParameter)
        );
        GenericQueryResult<T> result = applicationAtomicOperation.searchApplication(
                criteriaWrapper.getCriteria(),
                criteriaWrapper.getMust(), criteriaWrapper.getShould(),
                paginationParameter.getPageNo(),
                paginationParameter.getPageSize()
        );
        return SearchResponse.<A>builder().contents(
                result.getContents().stream().map(
                        applicationEntityTemplate -> applicationPoToVoConverter.convert(
                                GenericTypeUtils.cast(applicationEntityTemplate))
                ).collect(Collectors.toList())
        ).total(result.getTotal()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public A readApplication(@NonNull @Valid @NotBlank String applicationId) {
        return Objects.requireNonNull(applicationPoToVoConverter.convert(
                applicationAtomicOperation.safeReadApplication(applicationId)
        ));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
