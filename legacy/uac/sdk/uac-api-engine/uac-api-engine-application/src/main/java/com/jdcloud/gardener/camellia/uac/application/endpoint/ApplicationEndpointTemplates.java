package com.jdcloud.gardener.camellia.uac.application.endpoint;

import com.jdcloud.gardener.camellia.uac.application.schema.request.CreateApplicationParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.SearchApplicationCriteriaParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.request.UpdateApplicationParameterTemplate;
import com.jdcloud.gardener.camellia.uac.application.schema.response.ApplicationAppearanceTemplate;
import com.jdcloud.gardener.camellia.uac.application.service.ApplicationServiceTemplate;
import com.jdcloud.gardener.camellia.uac.application.skeleton.ApplicationSkeletons;
import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.ManagementApi;
import com.jdcloud.gardener.camellia.uac.common.endpoint.grouping.annotation.OpenApi;
import com.jdcloud.gardener.camellia.uac.common.schema.request.PaginationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.request.SecurityOperationParameter;
import com.jdcloud.gardener.camellia.uac.common.schema.response.SearchResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author zhanghan30
 * @date 2022/11/9 11:45
 */
public interface ApplicationEndpointTemplates {
    @ManagementApi
    @RequestMapping("/application")
    abstract class ManagementApiEndpointTemplate<
            C extends CreateApplicationParameterTemplate,
            S extends SearchApplicationCriteriaParameterTemplate,
            U extends UpdateApplicationParameterTemplate,
            A extends ApplicationAppearanceTemplate
            > implements ApplicationSkeletons.ManagementSkeleton<C, S, U, A> {
        @Getter(AccessLevel.PROTECTED)
        @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
        private ApplicationServiceTemplate<C, S, U, A, ?, ?> applicationService;

        @Override
        @PostMapping
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void createApplication(@Valid @RequestBody C createApplicationParameter) {
            applicationService.createApplication(createApplicationParameter);
        }

        @Override
        @PutMapping("/{applicationId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void updateApplication(
                @Valid @NotBlank @PathVariable("applicationId") String applicationId,
                @Valid @RequestBody U updateApplicationParameter
        ) {
            applicationService.updateApplication(applicationId, updateApplicationParameter);
        }

        @Override
        @PostMapping("/{applicationId}:enable")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void enableApplication(@Valid @NotBlank @PathVariable("applicationId") String applicationId,
                                      @Valid @RequestBody SecurityOperationParameter securityOperationParameter) {
            applicationService.enableApplication(applicationId, securityOperationParameter);
        }

        @Override
        @PostMapping("/{applicationId}:disable")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void disableApplication(@Valid @NotBlank @PathVariable("applicationId") String applicationId,
                                       @Valid @RequestBody SecurityOperationParameter securityOperationParameter) {
            applicationService.disableApplication(applicationId, securityOperationParameter);
        }

        @Override
        @GetMapping
        public SearchResponse<A> searchApplication(
                @Valid S searchApplicationCriteriaParameter,
                @Valid PaginationParameter paginationParameter
        ) {
            return applicationService.searchApplication(searchApplicationCriteriaParameter, paginationParameter);
        }

        @Override
        @GetMapping("/{applicationId}")
        public A readApplication(@Valid @NotBlank @PathVariable("applicationId") String applicationId) {
            return applicationService.readApplication(applicationId);
        }
    }

    @OpenApi
    @RequestMapping("/application")
    abstract class OpenApiTemplate<A extends ApplicationAppearanceTemplate> implements
            ApplicationSkeletons.OpenApiSkeleton<A> {
        @Getter(AccessLevel.PROTECTED)
        @Setter(value = AccessLevel.PRIVATE, onMethod = @__({@Autowired}))
        private ApplicationServiceTemplate<?, ?, ?, A, ?, ?> applicationService;

        @Override
        @GetMapping("/{applicationId}")
        public A readApplication(@Valid @NotBlank @PathVariable("applicationId") String applicationId) {
            return applicationService.readApplication(applicationId);
        }
    }
}
