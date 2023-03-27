package io.gardenerframework.camellia.authentication.server.test.beans;

import io.gardenerframework.camellia.authentication.server.common.annotation.AdministrationServerComponent;
import io.gardenerframework.camellia.authentication.server.common.api.group.AdministrationServerRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhanghan30
 * @date 2023/3/22 18:09
 */
@AdministrationServerRestController
@AdministrationServerComponent
@RequestMapping("/TestAdministrationServerRestController")
public class TestAdministrationServerRestController {
    @GetMapping
    public void test() {

    }
}
