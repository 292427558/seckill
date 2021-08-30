package com.github.lyrric.task;

import com.alibaba.fastjson.JSONObject;
import com.github.lyrric.conf.Config;
import com.github.lyrric.model.BusinessException;
import com.github.lyrric.model.SubDate;
import com.github.lyrric.model.SubDateTime;
import com.github.lyrric.service.HttpService;
import com.github.lyrric.service.SecKillService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * mode class
 *
 * @Author LiuJun
 * @Date 2021/7/5 14:34
 */

public class MsTask implements Runnable {

    private final Logger logger = LogManager.getLogger(SecKillService.class);


    private String vaccineId;
    private long startDate;
    HttpService httpService;

    AtomicReference<String> orderIdAtomic = new AtomicReference<>(null);

    public static AtomicBoolean success = new AtomicBoolean(false);

    public MsTask(String vaccineId, long startDate, HttpService httpService) {
        this.vaccineId = vaccineId;
        this.startDate = startDate;
        this.httpService = httpService;
    }

    @Override
    public void run() {

        long id = Thread.currentThread().getId();
        do {
            try {

                if(orderIdAtomic.get()==null){
                    logger.info("Thread ID：{}，发送请求", id);
                    String json = httpService.secKill(vaccineId, "1", Config.memberId.toString(), Config.idCard);
                    if(json==null){
                        continue;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    //获取到orderid
                    logger.info("Thread ID：{}，获取到秒杀成功返回参数json: {}", id,json);
                    String orderId = jsonObject.getString("data");
                    orderIdAtomic.set(orderId);

                    logger.info("Thread ID：{}，获取秒杀资格成功：orderid{}", id,orderIdAtomic.get());
                }
                if(!success.get()){
                    return;
                }
                if(orderIdAtomic.get()!=null){
                    List<SubDate> skSubDays = httpService.getSkSubDays(vaccineId, orderIdAtomic.get());
                    if(!success.get()){
                        return;
                    }
                    logger.info("预约日期： "+skSubDays);
                    for (SubDate skSubDay : skSubDays) {
                        String day = skSubDay.getDay();
                        String total = skSubDay.getTotal();
                        if(Integer.parseInt(total)<=0){
                            continue;
                        }
                        //获取预约时间段
                        List<SubDateTime> skSubDayTime = httpService.getSkSubDayTime(vaccineId, orderIdAtomic.get(), day);
                        if(!success.get()){
                            return;
                        }
                        logger.info("Thread ID：{}，{} 预约时间段：{}",id,day,skSubDays);
                        for (SubDateTime subDateTime : skSubDayTime) {
                            Integer maxSub = subDateTime.getMaxSub();
                            String wid = subDateTime.getWid();
                            if(maxSub<=0){
                                continue;
                            }
                            //提交预约
                            Boolean aBoolean = httpService.subDayTime(vaccineId, orderIdAtomic.get(), day, wid);
                            if(aBoolean){
                                logger.info("Thread ID：{}，预约成功。。。",id);
                                success.set(aBoolean);
                                return;
                            }
                        }
                    }
                }
            } catch (BusinessException e) {
                logger.info("Thread ID: {}, 抢购失败: {}",Thread.currentThread().getId(), e.getErrMsg());
                if("操作过于频繁,请稍后再试!".equals(e.getErrMsg()) && new Random().nextBoolean()){
                    try {
                        logger.error("Thread ID：{}，操作过于频繁",id);
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }catch (HttpServerErrorException e) {
                e.printStackTrace();
                logger.error("Thread ID: {}，http异常 {}", Thread.currentThread().getId(),e.getMessage());
            }
            catch (Exception e) {
                e.printStackTrace();
                logger.error("Thread ID: {}，未知异常", Thread.currentThread().getId());
            }finally {
                //如果离开始时间180秒后，或者已经成功抢到则不再继续
                if(System.currentTimeMillis() > startDate+1000*60*3 ){
                    return;
                }
            }
        } while (!success.get());
    }
}
