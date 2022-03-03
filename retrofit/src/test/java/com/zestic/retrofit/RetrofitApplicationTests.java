package com.zestic.retrofit;

import com.zestic.retrofit.annotation.EnableRetrofitClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import retrofit2.Retrofit;

@SpringBootTest
@EnableRetrofitClient
class RetrofitApplicationTests {

    @Autowired
    private Retrofit retrofit;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(retrofit);
    }
}
