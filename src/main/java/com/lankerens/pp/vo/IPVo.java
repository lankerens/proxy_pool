package com.lankerens.pp.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


/**
 * @author lankerens
 * @version 1.0
 * @description: TODO
 * @date 2023/2/25 7:16 PM
 */
@Setter
@Getter
@ToString
public class IPVo implements Serializable {
    // 解决
    // com.lankerens.pp.vo.IPVo; local class incompatible: stream classdesc serialVersionUID = -1705161403766261116, local class serialVersionUID = 7377799952463193426
    private static final long serialVersionUID = -6743567631108323096L;


    private String id;
    private String ip;
    private int port;
    // 协议
    private String protocols;
    private String city;
    private String country;

    // 匿名等级
    private String anonymityLevel;
    private String updateAt;
    
}

