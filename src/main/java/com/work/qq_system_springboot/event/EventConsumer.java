package com.work.qq_system_springboot.event;

import com.alibaba.fastjson.JSONObject;
import com.work.qq_system_springboot.entity.Event;
import com.work.qq_system_springboot.entity.Message;
import com.work.qq_system_springboot.service.MessageService;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements QQSystemConstant{

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    //消费事件
    @KafkaListener(topics = {TOPIC_LIKE,TOPIC_COMMENT,TOPIC_REPLY})
    public void consumEvent(ConsumerRecord record){
        if(record==null||record.value()==null){
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误！");
            return;
        }

        Map<String,Object> msg = new HashMap<>();
        msg.put("fromId",event.getUserId());
        msg.put("entityType",event.getEntityType());
        msg.put("entityId",event.getEntityId());
        msg.put("comment",event.getComment());

        Message message = new Message();
        message.setTopic(record.topic());
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getTargetId());
        message.setContent(JSONObject.toJSONString(msg));
        message.setCreateTime(new Date());
        message.setStatus(UNREADED);//设置为未读状态
        messageService.sendMessage(message);
    }

}
