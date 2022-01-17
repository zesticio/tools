package com.zectic.retrofit;

import com.zectic.retrofit.stub.GithubClient;
import com.zectic.retrofit.stub.LoginForm;
import com.zectic.retrofit.stub.LoginVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpClientTest {
    @Autowired
    private GithubClient githubClient;

    @Test
    public void test() throws IOException {
        LoginForm form = new LoginForm();
        form.setAccount("15156684305");
        form.setSmsCode("123456");
        LoginVO body = githubClient.listRepos(form)
                .body();
        System.out.println(body);
    }
}
