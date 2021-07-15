package testhttp;

import com.github.lyrric.conf.Config;
import com.github.lyrric.service.HttpService;

import java.util.concurrent.TimeUnit;

/**
 * mode class
 *
 * @Author LiuJun
 * @Date 2021/7/15 16:54
 */

public class TestProxy {

    public static void main(String[] args) {
        HttpService httpService = new HttpService();
        Config.cookies="_xxhm_=%7B%22id%22%3A11179755%2C%22mobile%22%3A%2215680830558%22%2C%22nickName%22%3A%22%E8%8B%A6%E5%92%96%E5%95%A1%22%2C%22headerImg%22%3A%22http%3A%2F%2Fthirdwx.qlogo.cn%2Fmmopen%2FxjC97SLpMq3dWa0DBnBZpdnDSKu7ZD6JBklc0qpCI6wiascHkcT2VLFNFL2OgXmBiaoZfj5twFRMn6hGPfQ7rRVysTNZeiblcXz%2F132%22%2C%22regionCode%22%3A%22510124%22%2C%22name%22%3A%22%E4%BB%98*%22%2C%22uFrom%22%3A%22cdbdbsy%22%2C%22wxSubscribed%22%3A1%2C%22birthday%22%3A%221997-12-09+00%3A00%3A00%22%2C%22sex%22%3A2%2C%22hasPassword%22%3Afalse%2C%22birthdayStr%22%3A%221997-12-09%22%7D; _xzkj_=wxapptoken:10:6d28b1533d9529787d6511f95c8d8ddb_71b46afe3d0215525602403578d6173d; 3e6d=90271abb122c26e03d; tgw_l7_route=448c7c53ec0b819179ba57d248db6663";
        Config.tk="wxapptoken:10:6d28b1533d9529787d6511f95c8d8ddb_71b46afe3d0215525602403578d6173d";
        Config.memberName="付柳";
        Config.memberId=9720717;
        Config.idCard="513701199712093224";
        Config.regionCode="5101";

//        System.setProperty("proxyHost", "127.0.0.1");
//        System.setProperty("proxyPort", "10809");

//        System.setProperty("javax.net.ssl.trustStore", "c:/keystore/fiddler_keystore.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "abc123456");
        long start = System.currentTimeMillis();
        try {
            for (int i = 0; i < 2; i++) {
                System.out.println("第几次请求"+i);
                Long st = httpService.getSt("1026");
                long now = System.currentTimeMillis();
                System.out.println(now-st);
                TimeUnit.MILLISECONDS.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("时间:"+(end-start));
    }
}
