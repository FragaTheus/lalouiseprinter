package br.com.matheusfragadev.lalouise;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class LalouiseApplicationTests {

    @Test
    void mainShouldDelegateToSpringApplicationRun() {
        String[] args = new String[]{"--spring.main.banner-mode=off"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            LalouiseApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(LalouiseApplication.class, args));
        }
    }
}
