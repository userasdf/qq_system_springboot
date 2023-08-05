package com.work.qq_system_springboot.tools;

public interface QQSystemConstant {

    //激活成功
    int ACTIVATION_SUCCESS = 1;
    //重复激活
    int ACTIVATION_REPEAT = 2;
    //激活失败
    int ACTIVATION_FAIL = 3;

    //默认状态的登录凭证超时时间
    int DEFAULT_EXPIRED_SECONDS = 60 * 60 * 12;//12小时
    //记住状态的登录超时时间
    int REMEMBER_EXPIRED_SECONDS = 60 * 60 * 24 * 10;//十天

    //好友关系
    int FRIEND_RELATION = 1;
    //待验证
    int HAVE_NOT_VARIFY = 2;

    //未读
    int UNREADED = 0;
    //已读
    int ISREADED = 1;

    //实体类型
    int ENTITY_TYPE_DISCUSSPOST = 1;
    int ENTITY_TYPE_COMMENT = 2;

    //四类主题
    String TOPIC_LIKE = "like";
    String TOPIC_COMMENT = "comment";
    String TOPIC_REPLY = "reply";
    String TOPIC_MSG = "msg";//发送普通消息
    String TOPIC_FILE = "file";//发送文件消息
    String TOPIC_ADD_RELATION = "addRelation";//添加好友或群聊

    //系统用户
    int SYSTEM_USER_ID = 1;
    //未登录状态的模拟用户
    int UNSIGNED_USER_ID=2;

    //操作日志类型
    int LOG_TYPE_INTERACTIVE = 0;
    int LOG_TYPE_FILE = 1;
    int LOG_TYPE_GAME = 2;
}
