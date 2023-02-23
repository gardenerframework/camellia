package com.jdcloud.gardener.camellia.authorization.demo.sns.alipay;

import com.jdcloud.gardener.camellia.authorization.sns.alipay.configuration.AlipayConfiguration;
import com.jdcloud.gardener.camellia.authorization.sns.alipay.configuration.AlipayOption;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * 需要在{@link AlipayConfiguration}之前完成初始化
 *
 * @author zhanghan30
 * @date 2022/11/10 16:19
 */
@Configuration
@AutoConfigureBefore(AlipayConfiguration.class)
@ConditionalOnClass(AlipayConfiguration.class)
public class DemoAlipayOptionConfigurer {
    public DemoAlipayOptionConfigurer(AlipayOption alipayOption) {
        alipayOption.setAppId(
                //应用id
                "2021003161656103"
        );
        alipayOption.setAliPublicKey(
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz8H3CttnGb8SHh/G79Kc9DJmLjtHlRL6PoSlr4uMfBgci619nmvvnmD6FJJT8aYngK+0kesAFmxLs71FYotXHG+Kid4tdJSczMNq+2Nh39JKzjJT1kdU08zV0fRdAMI/i/ZRJ67SBRI7ifm+8evN9CfxRkJQwBZjRc9DXfbnGzLuNpNYB4id7ejUS3ovVnIWaWK/jc5ckSWk+crIUwpIzvHRWLn9LhU7t1ER3fIXg4I06svujN0CuNr0XftMvMDSHqbCLITUCQIOBCx5Js9al76pbn0ukFG/W6p8D/liclytsuz6C23gP0kjp+jREVflaImlV8ZGH5vhSxxC0yBFUQIDAQAB"
        );
        alipayOption.setPrivateKey("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCOGxFutWnJdYd0KnPf7eOCJXCSxdxupNneYbaJJDDcEV0lokRA71aXQRB5jtWFrH33a4oID+4+FCvzQ+sadSOTfT9hfbjteRj+UXnQ2mpkcLhldD6UFrp5wdlRalzOwsDc3Vyk6SZY+yCMILo1vowTyNQta+LVPJ4g04AxtnMwQKXBy92KomUTxE/qaMnW2AIOHfTbwwjQoDjvpmQZiVgqxac2/ZmzFHv39yemVSHN63SM3RZCeZ7zCNqC6+nvqy20o/Q735eXT7AXZQhl/3m62UekGfm2TEsXjCeR/72YoHc1yNL5Duvja6K+e10KbQTJ0rMsw9aHHri/tkJe2yrDAgMBAAECggEAOfYqbxiJtIU+oZArQTv7nvr8ZrFTjsGop779QATF9Q0UDCSaaE326Ky+Noae6u4HVyLC4zT0PNfncUrTmzAKOb9NWct44UuSeTDsWdasrJkZbTAz0h1PJBiANmgGwMLI/Y1Am3cJkCOmC+HRcdhttCgm+vvVQpp9+O+rixta8mPgX3dOzNXGr7SZ8lllNFv8C/D1DJ5TVo0wCgics4IAf0cR7irzfFKljm75zx4HRBphx0MV7hcMDs+Dq9jtLyQ+xVJFrza4kZnYtOQxohj+KkPtmI4OP9cCk2jYHjhyN8jwYbi4f8N3A95rigBBElpF76zUguzGpeCu/J3rBs1GCQKBgQC/6+6YN2l7RMbTC8Pp7Ta3avgBm+LlboU0TaO4cJ827VpX6m2HVQODKYfkEOpIlzkVH4jTx93mhR181FyNmb287NCh/qCZkbpxYEvOGY/pZ8MxdZ0o34sejySWlEp1B3MhAzHjuMz6D0xPkfktJcDD6CEFjcqprqC0b+DjDdsYxQKBgQC9jTx44EtG3NXrW9vSU0pFoRlHsWzaGhxpIXQaEhm3puc7mNzwcYZ41F4BDVTMfolTy2d/cZsxOWAEeq0dpQdO/1eitZD4+zUNO+IWqQAE5tGOQvxGzGsrYSxu/hEQcbwRtPftYP3ksgspYFJPvX2QNSFVXNeVYXyP/O1dqYud5wKBgQCid6CAXRA/WOaTTDdqNPSH6tbNzeAS5y9+Kmd5QGWXwvAi0oIr+UzC4Qp3h9Y84z9gYVScK2rutStUPBWpjUdwwmyPZhUgS5wwVBt3+m24Ya18lhsXub10fiq0Cg7J6SeN/71hEFT9LqwkA+eTT41SipRBtlmksP68IKWYsGcUHQKBgQCKbjk5teSD7ge0iTNy2S0myM5jk2oBpowVIz9dQtbRTu0Uk0DXwLEKUV4NuqSxa7/zGFuPsXI7csFyw/zl53387XIc/CjSDPPjpFYrYmUHL2LfWIBRXDzwQ1ll/dqMfeNxsw5VqD4D5RtNrnCul5650HN1mgY/rCCCL59Shd3XBQKBgQClTZU8TI1nliYdyXWg29VOuGaiYRBZV6FXDhwLhffQcCEs3gtey/qvbfIWGHkwU2XtyxFPoRZpVknu4XjAnHZxTDMVwyBBCsQqn+SRB8PBYvYXzx4dkGPJLxG7vfMYfWrGPv9BjxQNFWCImRTExbhG4ykKZYnKIk9XyWN4BSTqSg==");
        alipayOption.setEncryptKey("t1lpInhUkDQxthv93CIFrg==");
    }
}
