package org.inzight.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    @Bean
    public PayOS payOS() {
        return new PayOS(
                "ab0c2f76-05c3-46cc-a5cd-44cb7927d0fe",
                "049ce78b-40aa-452d-83a8-f5e8bd9d2ea3",
                "ee84ecf1be64d1cb29945a8cb2140d5dc0a11ded055f8d3087d88aa66ee97184"
        );
    }
}
