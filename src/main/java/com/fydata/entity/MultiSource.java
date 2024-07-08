package com.fydata.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author xd
 * @create 2019/12/10 19:14
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(indexName = "#{esIndexBean.getIndexName()}", createIndex = false)
public class MultiSource extends EsDocument {
    private static final long serialVersionUID = 1L;

    /**
     * 主对象号码
     */
    @Field(type = FieldType.Keyword)
    private String senderId;
    /**
     * 主对象名称
     */
    @Field(type = FieldType.Keyword)
    private String sender;
    /**
     * 主对象id
     */
    @Field(type = FieldType.Keyword)
    private String senderOther;
    /**
     * 时间
     */
    @Field(type = FieldType.Long)
    private Long date;
    /**
     * 从对象号码
     */
    @Field(type = FieldType.Keyword)
    private String receiverId;
    /**
     * 从对象名称
     */
    @Field(type = FieldType.Keyword)
    private String receiver;
    /**
     * 从对象id
     */
    @Field(type = FieldType.Keyword)
    private String receiverOther;
    /**
     * 内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;
    /**
     * 附件
     */
    @Field(type = FieldType.Keyword)
    private String attachment;


    /**
     * 发送消息类型
     */
    @Field(type = FieldType.Keyword)
    private String messageType;
    /**
     * 查看状态
     */
    @Field(type = FieldType.Keyword)
    private String checkStatus;

    /**
     * 存储位置
     */
    @Field(type = FieldType.Keyword)
    private String storageLoc;
    /**
     * 短信状态
     */
    @Field(type = FieldType.Keyword)
    private String smsStatus;
    /**
     * 通话时长
     */
    @Field(type = FieldType.Integer)
    private Integer callDuration;
    /**
     * 是否节假日
     */
    @Field(type = FieldType.Boolean)
    private Boolean ifDays;
    /**
     * 应用类型
     */
    @Field(type = FieldType.Keyword)
    private String applicationType;

    /**
     * 通话结束时间
     */
    @Field(type = FieldType.Long)
    private Long endDate;


    @Field(type = FieldType.Keyword)
    private String sourceType;
    /**
     * 附件id
     */
    @Field(type = FieldType.Keyword)
    private String fjid;
    /**
     * 通话状态
     */
    @Field(type = FieldType.Keyword)
    private String telStatus;

    private String tableName;

    /**
     * 通话时段
     */
    @Field(type = FieldType.Keyword)
    private String thsd;

    /**
     * 组群用户id
     */
    @Field(type = FieldType.Keyword)
    private String groupUserId;
    /**
     * 组群用户账户
     */
    @Field(type = FieldType.Keyword)
    private String groupAccount;

    @Field(type = FieldType.Keyword)
    private String modifyField;

    /**
     * 借方金额
     */
    @Field(type = FieldType.Double)
    private Double jfje;

    /**
     * 备注
     */
    @Field(type = FieldType.Keyword)
    private String bz;


    /**
     * 本机imsi
     */
    @Field(type = FieldType.Keyword)
    private String bjimsi;

    /**
     * 本机imei
     */
    @Field(type = FieldType.Keyword)
    private String bjimei;

    /**
     * 记录唯一ID
     */
    @Field(type = FieldType.Keyword)
    private String jlid;

    /**
     * 本机通话所在地
     */
    @Field(type = FieldType.Keyword)
    private String bjthszd;

    /**
     * 对方imsi
     */
    @Field(type = FieldType.Keyword)
    private String dfimsi;

    /**
     * 对方imei
     */
    @Field(type = FieldType.Keyword)
    private String dfimei;

    /**
     * 对方 rac
     */
    @Field(type = FieldType.Keyword)
    private String dfrac;

    /**
     * 对方lac
     */
    @Field(type = FieldType.Keyword)
    private String dflac;

    /**
     * 对方基站id
     */
    @Field(type = FieldType.Keyword)
    private String dfjzid;

    /**
     * 对方cellid
     */
    @Field(type = FieldType.Keyword)
    private String dfcellid;

    /**
     * 对方归属运营商
     */
    @Field(type = FieldType.Keyword)
    private String dfgsyys;

    /**
     * 对方通话所在地
     */
    @Field(type = FieldType.Keyword)
    private String dfthszd;

    /**
     * 前转主叫号码
     */
    @Field(type = FieldType.Keyword)
    private String qzzjhm;

    /**
     * 呼叫开始时间
     */
    @Field(type = FieldType.Long)
    private Long hjkssj;

    /**
     * 对方号码归属地
     */
    @Field(type = FieldType.Keyword)
    private String dfhmgsd;

    /**
     * 是否群内呼叫
     */
    @Field(type = FieldType.Keyword)
    private String qzhj;

    /**
     * 群组编号
     */
    @Field(type = FieldType.Keyword)
    private String qzbh;

    /**
     * 群组名称
     */
    @Field(type = FieldType.Keyword)
    private String qzmc;

    /**
     * 话单类型
     */
    @Field(type = FieldType.Keyword)
    private String hdlx;


    /**
     * 本机rac
     */
    @Field(type = FieldType.Keyword)
    private String bjrac;

    /**
     * 本机lac
     */
    @Field(type = FieldType.Keyword)
    private String bjlac;

    /**
     * 本机基站id
     */
    @Field(type = FieldType.Keyword)
    private String bjjzid;

    /**
     * 本机cellid
     */
    @Field(type = FieldType.Keyword)
    private String bjcellid;

    /**
     * 本机归属运营商
     */
    @Field(type = FieldType.Keyword)
    private String bjgsyys;
    /**
     * 批次号
     */
    @Field(type = FieldType.Keyword)
    private String zl_wjm;
    /**
     * 贷方金额
     */
    @Field(type = FieldType.Double)
    private Double dfje;
    /**
     * 交易余额
     */
    @Field(type = FieldType.Double)
    private Double jyye;
    /**
     * 币种
     */
    @Field(type = FieldType.Keyword)
    private String biz;
    /**
     * 现金标志
     */
    @Field(type = FieldType.Keyword)
    private String xjbz;
    /**
     * 交易网点名称
     */
    @Field(type = FieldType.Keyword)
    private String jywdmc;
    /**
     * 交易发生地
     */
    @Field(type = FieldType.Keyword)
    private String jyfsd;
    /**
     * 终端号
     */
    @Field(type = FieldType.Keyword)
    private String zdh;
    /**
     * 交易流水号
     */
    @Field(type = FieldType.Keyword)
    private String jylsh;
    /**
     * 任务流水号
     */
    @Field(type = FieldType.Keyword)
    private String rwlsh;
    /**
     * mac地址
     */
    @Field(type = FieldType.Keyword)
    private String macdz;
    /**
     * ip地址
     */
    @Field(type = FieldType.Keyword)
    private String ipdz;
    /**
     * 本方卡号
     */
    @Field(type = FieldType.Keyword)
    private String bfkh;
    /**
     * 交易方卡号
     */
    @Field(type = FieldType.Keyword)
    private String jydfkh;
    /**
     * 交易类型
     */
    @Field(type = FieldType.Keyword)
    private String jylx;
    /**
     * 身份证号
     */
    @Field(type = FieldType.Keyword)
    private String sfzh;
    /**
     * 开户行
     */
    @Field(type = FieldType.Keyword)
    private String khh;
    /**
     * 对方开户行
     */
    @Field(type = FieldType.Keyword)
    private String jydfzhkhx;
    /**
     * 通话类型
     */
    @Field(type = FieldType.Keyword)
    private String thlx;
    /**
     * 修改时间
     */
    @Field(type = FieldType.Long)
    private long modifyTime;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private String jysj;
    /**
     * 该好友是否被删除0未删除，1已删除
     */
    private String deleteFlag;
    /**
     * 金融 0为重复数据,1为正常数据
     */
    @Field(type = FieldType.Keyword)
    private String flag;

    /**
     * 该群消息条数
     */
    private Integer chatNum;

    private String hashId;

    @Field(type = FieldType.Keyword)
    private String jdbz;

    @Field(type = FieldType.Keyword)
    private String qqdbs;

    @Field(type = FieldType.Keyword)
    private String ckztlb;

    @Field(type = FieldType.Keyword)
    private String sqjgdm;

    @Field(type = FieldType.Keyword)
    private String mbjgdm;

    @Field(type = FieldType.Keyword)
    private String hzsj;

    @Field(type = FieldType.Keyword)
    private String cxfkjg;

    @Field(type = FieldType.Keyword)
    private String cxfkjgyy;

    @Field(type = FieldType.Keyword)
    private String cxkh;

    @Field(type = FieldType.Keyword)
    private String jydfzjhm;

    @Field(type = FieldType.Double)
    private Double jydsye;

    @Field(type = FieldType.Keyword)
    private String jywddm;

    @Field(type = FieldType.Keyword)
    private String rzh;

    @Field(type = FieldType.Keyword)
    private String cph;

    @Field(type = FieldType.Keyword)
    private String pzzl;

    @Field(type = FieldType.Keyword)
    private String pzh;

    @Field(type = FieldType.Keyword)
    private String jysfcg;

    @Field(type = FieldType.Keyword)
    private String shmc;

    @Field(type = FieldType.Keyword)
    private String shh;

    @Field(type = FieldType.Keyword)
    private String jygyh;

    @Field(type = FieldType.Keyword)
    private String ipgsd;

    @Field(type = FieldType.Keyword)
    private String ipld;
}
