package com.lankerens.pp.controller;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lankerens.pp.vo.IPVo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author lankerens
 * @version 1.0
 * @description: TODO
 * @date 2023/2/25 7:17 PM
 */
@RestController
@RequestMapping("api/proxy")
public class ProxyController {

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/create")
    public JSONObject getAndSaveIpPool(String url) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url).build();
        try {

            Response response = okHttpClient.newCall(request).execute();
            String json = response.body().string();
            JSONArray jsonArray = JSONUtil.parseArray(json.substring(json.indexOf(":") + 1, json.lastIndexOf("]") + 1));
            List<IPVo> ips = JSONUtil.toList(jsonArray, IPVo.class);

            redisTemplate.opsForSet().add("ips", ips.toArray());

            return new JSONObject().set("data", ips);
        } catch (Exception e) {
            return new JSONObject().set("msg", e.getMessage());
        }
    }


    @GetMapping("get")
    public JSONObject getProxyOne() {
        IPVo member = (IPVo) redisTemplate.opsForSet().randomMember("ips");

        return new JSONObject().set("data", member);
    }


    @GetMapping("test")
    public JSONObject testIpProxy() {
        List<IPVo> ans = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            IPVo member = (IPVo) redisTemplate.opsForSet().randomMember("ips");

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .proxy(new Proxy(member.getProtocols().contains("socks") ? Proxy.Type.SOCKS : Proxy.Type.HTTP,
                            new InetSocketAddress(member.getIp(), member.getPort())))
                    .connectTimeout(10, TimeUnit.SECONDS).build();

            Request request = new Request.Builder().url("http://www.baidu.com").build();

            try {
                Response response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    ans.add(member);
                    System.out.println("成功 -- " + member);
                }

            } catch (Exception e) {
//                return new JSONObject().set("msg", e.getMessage());
                System.out.println("失败 -- " + member.getProtocols() + ":" + member.getIp() + ":" + member.getPort());
                System.out.println(e.getMessage());
            }
        }

        return new JSONObject().set("data", ans);
    }

}
