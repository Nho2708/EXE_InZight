package org.inzight;

import org.inzight.config.NgrokRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InZightAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(InZightAppApplication.class, args);

        // run server for backend
        NgrokRunner.startNgrok();
    }
}
