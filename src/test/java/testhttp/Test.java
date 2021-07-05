package testhttp;

import com.github.lyrric.conf.Config;
import com.github.lyrric.model.Member;
import com.github.lyrric.model.SubDate;
import com.github.lyrric.model.VaccineList;
import com.github.lyrric.service.HttpService;
import com.github.lyrric.util.HttpConnectionPoolUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

/**
 * mode class
 *
 * @Author LiuJun
 * @Date 2021/5/21 10:43
 */

public class Test {

    HttpService httpService;

    @Before
    public void before(){
        httpService = new HttpService();
        Config.cookies="_xxhm_=%7B%22birthday%22%3A881596800000%2C%22birthdayStr%22%3A%221997-12-09%22%2C%22hasPassword%22%3Afalse%2C%22headerImg%22%3A%22http%3A%2F%2Fthirdwx.qlogo.cn%2Fmmopen%2FxjC97SLpMq3dWa0DBnBZpdnDSKu7ZD6JBklc0qpCI6wiascHkcT2VLFNFL2OgXmBiaoZfj5twFRMn6hGPfQ7rRVysTNZeiblcXz%2F132%22%2C%22id%22%3A11179755%2C%22mobile%22%3A%22156****0558%22%2C%22name%22%3A%22%E4%BB%98*%22%2C%22nickName%22%3A%22%E8%8B%A6%E5%92%96%E5%95%A1%22%2C%22regionCode%22%3A%22510124%22%2C%22sex%22%3A2%2C%22uFrom%22%3A%22cdbdbsy%22%2C%22wxSubscribed%22%3A1%7D; _xxhmen_=FE532F1A1381E663A2FDF6D198204C4592A1BF70511FB5B1E8A848001E0701F08342D5D909BC077A3568FE251E8FEDB49C15FF705C846ECF113E09E15B2C2E0A7B4E69425D67005FA84E2777340E41CFFA1933CEC9A49B1A9ADBB792B446B6F9F99DF67C4064C49EE043AAB96FC940907770D9037C32B5BF900DA2DC7BB5B074046E3C80F6FD8E07F36AE3AA8CCC93DF2AA3E714B17653090CE4BB12B4F2BFCC5EA2DF3A4D4110A7BD8F47580782833C3FC14276409CA23012FE2F485F54DBD3964F449C7B8C3F01D724B69E0CE24B210B306E5DC59E9AD8B75B51471711014E7148C9AD89B7713F226691CFB854379BE866AAAD06BCEB9BD019CA4930CB636BD1ED9A3C99073BA8F6650ECDA026CD08303FBAF33B70627D5CACD10323419D670D3AE54632B98BE9FB7B9C3676A0FEEC60ED276217F67A5ECAC39A300772D3AD7D9D700B7F664877967958602283C08BCBB0CC3122E8ACBF5BA0F11E302F3EA7840DE6299F024F8B1BEDE508AB624D7D6FA15FD12F1013623092CE1E99DC96D69E426525E5FBE40F4AF7ED6AD4E8A34D59739BFB935B059C1F54F22C17DA1998823E1EA46B8BCE7DAF66CC6F93EBF9875436151F0CD8EB5E2E60D2993B3C1718; _xzkj_=wxapptoken:10:6d28b1533d9529787d6511f95c8d8ddb_b4df86c4a18d24d0ee9ca55ce000f6f1";
        Config.tk="wxapptoken:10:6d28b1533d9529787d6511f95c8d8ddb_b4df86c4a18d24d0ee9ca55ce000f6f1";
        Config.memberName="付柳";
        Config.memberId=9720717;
        Config.idCard="513701199712093224";
        Config.regionCode="5101";

    }

    @org.junit.Test
    //获取疫苗信息
    public void test() throws IOException {
        List<VaccineList> vaccineList = httpService.getVaccineList();
        System.out.println(vaccineList);
    }



    @org.junit.Test
    //获取接种人
    public void test2() throws IOException {
        List<Member> members = httpService.getMembers();
        System.out.println(members);
    }

    @org.junit.Test
    //获取接种人
    public void test4() throws IOException {
        System.out.println(httpService.getSt("978"));
    }

    @org.junit.Test
    //秒杀
    public void test3() throws IOException {
        String s = httpService.secKill("978", "1", Config.memberId + "", Config.idCard);
        System.out.println(s);
    }

    @org.junit.Test
    //获取日期
    public void test5() throws IOException {
        List<SubDate> skSubDays = httpService.getSkSubDays("978", "231");
        System.out.println(skSubDays);
    }
}
