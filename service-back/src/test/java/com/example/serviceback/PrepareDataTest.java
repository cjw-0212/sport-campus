package com.example.serviceback;

import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.User;
import com.example.serviceback.util.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author CJW
 * @since 2024/7/29
 */
@SpringBootTest
public class PrepareDataTest {
    private static RestTemplate restTemplate;
    private static Random random;
    private static final String RANDOM_USERNAME_URL = "https://api.mir6.com/api/sjname";
    private static final String RANDOM_USER_AVATAR_URL = "https://v2.api-m.com/api/head?return=302";
    private static final String TEMP_FILE_PATH = "D:\\MyProject\\sport-campus\\service-back\\file\\temp.png";
    private static final String FILE_UPLOAD_URL = "http://localhost:9000/file/uploadOne";
    @Autowired
    private UserMapper userMapper;
    @BeforeAll
    public static void beforeAll() {
        restTemplate = new RestTemplate();
        random = new Random();
    }

    @Test
    public void setUserData() throws IOException, ParseException {
        for (long i = 21251104201L; i < 21251104201L + 1; i++) {
            int sex = random.nextInt(3);
            String username = restTemplate.getForObject(RANDOM_USERNAME_URL, String.class);
            System.out.println(username);
            String intro = "一个热爱运动的学生";
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(RANDOM_USER_AVATAR_URL, HttpMethod.GET,
                    null, byte[].class);
            byte[] body = responseEntity.getBody();
            System.out.println(body);
            FileOutputStream fileOutputStream = new FileOutputStream(TEMP_FILE_PATH);
            fileOutputStream.write(body);
            fileOutputStream.flush();
            fileOutputStream.close();
            //请求上传文件
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            //请求参数
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("file", new FileSystemResource(new File(TEMP_FILE_PATH)));
            //组装请求体
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);
            Result<String> result = restTemplate.postForObject(FILE_UPLOAD_URL, httpEntity, Result.class);
            String avatar = result.getData();
            User user = new User();
            user.setId(i);
            user.setName(username);
            user.setPassword(String.valueOf(i));
            user.setAvatar(avatar);
            user.setSex(sex);
            user.setBirthday(randomDate("2001-01-01", "2006-12-30"));
            user.setIntro(intro);
            userMapper.insert(user);
        }
    }

    public String randomDate(String stratDate, String endDate) throws ParseException {
        SimpleDateFormat st = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = st.parse(stratDate);
        Date d2 = st.parse(endDate);
        //  开始的毫秒数
        long l1 = d1.getTime();
        //  结束的毫秒数
        long l2 = d2.getTime();
        long l3 = (long) ((Math.random() * (l2 - l1 + 1)) + l1);
        Date date = new Date(l3);
        return st.format(date);
    }
}
