package com.jdcloud.gardener.camellia.uac.application.atomic;

import com.jdcloud.gardener.camellia.uac.application.atomic.verifer.ApplicationMustExistVerifier;
import com.jdcloud.gardener.camellia.uac.application.atomic.verifer.ApplicationMustNotExistedVerifier;
import com.jdcloud.gardener.camellia.uac.application.dao.mapper.ApplicationMapperTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.criteria.ApplicationCriteriaTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.entity.ApplicationEntityTemplate;
import com.jdcloud.gardener.fragrans.data.practice.operation.CommonOperations;
import com.jdcloud.gardener.fragrans.data.practice.operation.checker.RecordChecker;
import com.jdcloud.gardener.fragrans.data.schema.query.GenericQueryResult;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author zhanghan30
 * @date 2022/11/7 11:56
 */
@RequiredArgsConstructor
public class ApplicationAtomicOperationTemplate<A extends ApplicationEntityTemplate, C extends ApplicationCriteriaTemplate> {
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private ApplicationMapperTemplate<A, C> applicationMapper;
    @Getter(AccessLevel.PROTECTED)
    @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
    private CommonOperations commonOperations;

    /**
     * 创建应用
     *
     * @param application 应用
     */
    public void createApplication(@NonNull A application) {
        //允许输入id。所以要检查id是否存在
        readApplication(
                application.getId(),
                ApplicationMustNotExistedVerifier
                        .builder()
                        .recordId(application.getId())
                        .build()
        );
        applicationMapper.createApplication(application);
    }

    /**
     * 读取应用数据
     *
     * @param applicationId 应用id
     * @param checkers      检查器
     * @return 数据
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public A readApplication(
            @NonNull String applicationId, RecordChecker<? super A>... checkers) {
        return commonOperations.readThenCheck().single(
                () -> applicationMapper.readApplication(applicationId),
                checkers
        );
    }

    /**
     * 读取应用数据且不存在时报错
     *
     * @param applicationId 应用id
     * @param checkers      检查器
     * @return 数据
     */
    @SuppressWarnings("unchecked")
    public A safeReadApplication(
            @NonNull String applicationId, RecordChecker<? super A>... checkers) {
        Collection<RecordChecker<? super A>> checkerList = new LinkedList<>();
        checkerList.add(
                ApplicationMustExistVerifier.builder().recordId(applicationId).build()
        );
        if (checkers != null && checkers.length > 0) {
            checkerList.addAll(Arrays.asList(checkers));
        }
        RecordChecker<? super A>[] array = checkerList.toArray(new RecordChecker[]{});
        return readApplication(applicationId, array);
    }

    /**
     * 查询应用
     *
     * @param criteria 查询参数
     * @param must     那些条件是and
     * @param should   那些条件是or
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 查询结果
     */
    public GenericQueryResult<A> searchApplication(
            @NonNull C criteria,
            Collection<Class<?>> must,
            Collection<Class<?>> should,
            long pageNo,
            long pageSize
    ) {
        return new GenericQueryResult<>(
                applicationMapper.searchApplication(criteria,
                        must,
                        should,
                        pageNo,
                        pageSize),
                commonOperations.getFoundRows());
    }

    /**
     * 更新应用信息
     *
     * @param application 新的应用信息
     * @return 旧的应用信息
     */
    @SuppressWarnings("unchecked")
    public A updateApplication(
            @NonNull A application
    ) {
        A before = safeReadApplication(application.getId());
        applicationMapper.updateApplication(application);
        return before;
    }

    /**
     * 变更应用启用状态
     *
     * @param applicationId 应用id
     * @param status        状态
     * @return 之前的状态
     */
    @SuppressWarnings("unchecked")
    public boolean changeApplicationEnableStatus(
            @NonNull String applicationId,
            boolean status
    ) {
        boolean before = safeReadApplication(applicationId).isEnabled();
        applicationMapper.changeApplicationEnableStatus(applicationId, status);
        return before;
    }
}
