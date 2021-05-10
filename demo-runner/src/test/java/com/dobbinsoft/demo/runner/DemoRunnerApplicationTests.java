package com.dobbinsoft.demo.runner;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.alibaba.fastjson.JSONObject;
import com.dobbinsoft.fw.support.component.open.OpenPlatform;
import com.dobbinsoft.fw.support.component.open.model.OPClient;
import com.dobbinsoft.fw.support.component.open.model.OPData;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class DemoRunnerApplicationTests {

    @Autowired
    private OpenPlatform openPlatform;

    private OkHttpClient okHttpClient = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        String myPrivateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDBjTFpz4GEhbc1kJ+Vzjr1pYYtJQprcC2W0tIz+LqXCm+j05OngLCYyGaNpJW0/3lKJuAxPgQ/Z7TLO0JsOWKo9be+Jk+YWzc/BkiAq+dy2EXJnFqxBxWsb1Q2MSJN067TNh3XrOHitmecBjae78VQJNAb0UU4lYbrSJQa57Lywq9X/w9x3G4FaSHpvyGCy18+oeGy2+I+L4mQ15V5oI96VCHTWE/typ0sRE7EPDZXvCKZWsY/fnC8H9uwFvEN9o6nHR5cqZk/codCfzzP181m0W/MSJ388cyEZGRarPcvns4y2Wlcr8dgd/E6eRoF946lIPHmwiW2bQqkeMavbDnnAgMBAAECggEBAMCagFGK7JezeF0VTbkFQCayUJMcCQJg3vz3TbXqDcO/3thn8sb8iL+ArIzG+zLiBxCDHxXhYNi3mnGixrZrZII+Alp2RT6XhqJw3jUs22StUsUNkjIXmrm9D0/eFQTdYVtMvuWgZCf226OnizMd5nuaJn2IAhoMsfXwg3FCoBBxb/ybmyWebd4bbzZjk/LXWXXFQK6dpWFnWsQwdW7egU+Mip6qgF8F41//XBojDhHHVpYN/JnL/gcnMc2cpyC/VK6Cz1g1TxvRfxRDSIK26NhI4IhJY4CCYdxYEqAGZjfmVj2x+TsuDEE+m0n94Jdorq/glAz+DpIWSFij7ml5ugECgYEA6diULNJncrPoGYI+OuTUXT5bXc9HfHiUgdbkiPP/9ZuTC7OM0a3cONATidn+q6uApYbkh7SoO9p4OysJ4iE7n0+84oo4sdddNnOWZrAEpygIR6HO7hcmhYu0Jv9x4oNtYW96fHu61/amIp69pvFsZBbKlOMeYHdJFstvIGVaMH0CgYEA0+NcOImgHcAB3B5SonJVK2boF2gbuvEEj3gVffQWvV0RIADvzJhWpEYyiwhkEqFYeO/meCsV7LRJsiZIJAm4tyNHnzOlwihlGaQxE6a8XjWL4sKN3VSwAoRk8ELDeOszoWEb4iZkc196BFZLfIsWEhcyK9ib39A0bgLRmSxEpTMCgYEA3htWicgCd6DHF8tsHfYQesvqbgydYm0I6NXZ1PakK3zrypZsbw+Kdu+zTyCd0iIgG5gm4XpGvCrCPbigN4bHIYbT6fvM46WhpEFfHgObN8U/MAJREDUu4nMR1mSTn8ijEIOYvDms+skLfSB3E+SExgAbxBSAWrGR2zO4RfXeFuUCgYEAkb7HFWCdETmcTeNRpcmNCfCb3lXPFTRplaHc7qkx5wkYGXJrf8ideBuM8pNP+nye1Xj2h1vjldChDHhGkPta4iXTQw+MidOtTwjgdQrwcMDWOqDmYgK5mtGkeJsDx63I8s1QF6ue1rkqvcb5323wPXtVGFHDThzxo2Yu8/Q+N38CgYEAtCAovNtKedXCu/VzRwmRSg/vMSh+DHUmW+Hy/kXDkte7HCvUwSRLNjD0L2zcXCE5+WRZ33/+adMRnO1CaRQeesyBS8fKZP0IDcl5QAzWHgkf90atf3Vp+nOCw6I0GXwJNypteCHeJxYFP4Xo8JaVtCpNTqJKkJL0PuZukWJOT3M=";
        MediaType mediaType = MediaType.parse("application/json");
        List<String> paramPair = new ArrayList<>();
        paramPair.add("optimestamp=" + (System.currentTimeMillis()));
        paramPair.add("_gp=admin");
        paramPair.add("_mt=list");
        paramPair.sort(String::compareTo);
        String bodyStr = paramPair.stream().collect(Collectors.joining("&"));
        String ciphertext = SecureUtil.rsa(myPrivateKey, null).encryptBase64(bodyStr, KeyType.PrivateKey);
        OPData opData = new OPData();
        opData.setClientCode("test_open");
        opData.setCiphertext(ciphertext);
        RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(opData));
        OkHttpClient okHttpClient = new OkHttpClient();
        System.out.println(ciphertext);
        String json = okHttpClient.newCall(new Request.Builder().url("http://localhost:8801/m.api").post(body).build()).execute().body().string();
        System.out.println(json);
    }

    @Test
    void contextLoads() {
        OPClient opClient = new OPClient();
        opClient.setPublicKey2("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwY0xac+BhIW3NZCflc469aWGLSUKa3AtltLSM/i6lwpvo9OTp4CwmMhmjaSVtP95SibgMT4EP2e0yztCbDliqPW3viZPmFs3PwZIgKvncthFyZxasQcVrG9UNjEiTdOu0zYd16zh4rZnnAY2nu/FUCTQG9FFOJWG60iUGuey8sKvV/8PcdxuBWkh6b8hgstfPqHhstviPi+JkNeVeaCPelQh01hP7cqdLEROxDw2V7wimVrGP35wvB/bsBbxDfaOpx0eXKmZP3KHQn88z9fNZtFvzEid/PHMhGRkWqz3L57OMtlpXK/HYHfxOnkaBfeOpSDx5sIltm0KpHjGr2w55wIDAQAB");
        opClient.setNotifyUrl("http://www.baidu.com");
        opClient.setCode("test_open");
        opClient.setPermissionList(new ArrayList<>());
        openPlatform.init(opClient);
    }

}
