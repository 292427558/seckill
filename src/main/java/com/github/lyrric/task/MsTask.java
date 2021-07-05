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

import java.util.List;
import java.util.Random;

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
                //1.直接秒杀、获取秒杀资格
                if(SecKillService.getSuccessMap().get(vaccineId)){
                    return;
                }
                logger.info("Thread ID：{}，发送请求", id);
                String json = httpService.secKill(vaccineId, "1", Config.memberId.toString(), Config.idCard);
                JSONObject jsonObject = JSONObject.parseObject(json);
                //获取到orderid
                String orderId = jsonObject.getString("data");
                logger.info("Thread ID：{}，订单id：{}",id,orderId);
                if(SecKillService.getSuccessMap().get(vaccineId)){
                    return;
                }
                if(orderId!=null){
                    logger.info("Thread ID：{}，获取秒杀资格成功：orderid{}", id,orderId);
                    List<SubDate> skSubDays = httpService.getSkSubDays(vaccineId, orderId);
                    if(SecKillService.getSuccessMap().get(vaccineId)){
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
                        List<SubDateTime> skSubDayTime = httpService.getSkSubDayTime(vaccineId, orderId, day);
                        if(SecKillService.getSuccessMap().get(vaccineId)){
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
                            Boolean aBoolean = httpService.subDayTime(vaccineId, orderId, day, wid);
                            if(aBoolean){
                                logger.info("Thread ID：{}，预约成功。。。",id);
                                SecKillService.getSuccessMap().put(vaccineId,aBoolean);
                                return;
                            }
                        }
                    }
                }
            } catch (BusinessException e) {
                logger.info("Thread ID: {}, 抢购失败: {}",Thread.currentThread().getId(), e.getErrMsg());
                //如果离开始时间120秒后，或者已经成功抢到则不再继续
                if(System.currentTimeMillis() > startDate+1000*60*2 ){
                    return;
                }
                if("操作过于频繁,请稍后再试!".equals(e.getErrMsg()) && new Random().nextBoolean()){
                    try {
                        logger.error("Thread ID：{}，操作过于频繁",id);
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Thread ID: {}，未知异常", Thread.currentThread().getId());
            }
        } while (!SecKillService.getSuccessMap().get(vaccineId));
    }
}
