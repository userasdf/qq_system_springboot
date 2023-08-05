$(function(){

    //当前socket会话id
    var sessionId = $("#userId").val()+"_header";//socket的id

    var friendVarifyCount = 0;//好友验证个数
    var joinGroupApplyCount = 0;//进群申请个数
    var adviceCount = 0;//系统通知个数
    var adviceCountFor = [];//js修改vue数据的话，目前只能通过v-for动态增加或减少数据来实现修改
    var unreadMsgCount = 0;//未读消息个数
    var unreadMsgCountFor = [];



    $.ajax({
        url:"/qq_system/relation/getAllVarifyFriend",
        async:false,
        success:function (data) {
            if(data!=null&&data!=""){
                friendVarifyCount = data.split(';').length;
            }
        }
    });
    $.ajax({
        url:"/qq_system/relation/getJoinGroupApplyCount",
        async:false,
        success:function (data) {
            joinGroupApplyCount = parseInt(data);
        }
    });
    adviceCount = friendVarifyCount+joinGroupApplyCount;
    if(adviceCount>0){
        adviceCountFor.push(adviceCount);
    }
    $.ajax({
        url:"/qq_system/message/getAllUnreadCount?topic=msg&topic=file",
        async:false,
        success:function (data) {
            unreadMsgCount = parseInt(data);
            if(unreadMsgCount>0)
                unreadMsgCountFor.push(unreadMsgCount);
        }
    });

    var websocket;
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://" + document.location.host + "/qq_system/imserver/"+sessionId);
    } else if ('MozWebSocket' in window) {
        websocket = new MozWebSocket("ws://" + document.location.host + "/qq_system/imserver/"+sessionId);
    } else {
        websocket = new SockJS("http://" + document.location.host + "/qq_system/imserver/sockjs/"+sessionId);
    }
    //连接成功
    websocket.onopen = function(evnt) {
    };
    //收到消息
    websocket.onmessage = function(evnt) {

        var data = JSON.parse(evnt.data);
        if(data.topic=="msg"||data.topic=="file"){//发送普通消息或文件
            unreadMsgCount++;
            unreadMsgCountFor.pop();
            unreadMsgCountFor.push(unreadMsgCount);
        }else if(data.topic=="addRelation"){//请求添加好友，或申请入群
            adviceCount++;
            adviceCountFor.pop();
            adviceCountFor.push(adviceCount);
        }

    };


    var vue = new Vue({
        el:"#header",
        data:{
            adviceCountFor:adviceCountFor,
            unreadMsgCountFor:unreadMsgCountFor
        }
    });

    // var cmtCount = 0;//系统通知：评论
    // var replyCount = 0;//系统通知：回复
    // var likeCount = 0;//系统通知：点赞
    // var valFriendCount = 0;//验证好友
    // var unreadedMsgCount = 0;//未读消息数量
    //
    // //获取系统通知数量
    // function getSystemAdviceCount() {
    //     //异步获取所有评论数量
    //     $.ajax({
    //         url:'/qq_system/message/getAllUnreadCount?topic=comment',
    //         success:function (data) {
    //             cmtCount = parseInt(data)
    //         }
    //     });
    //     //异步获取所有回复数量
    //     $.ajax({
    //         url:'/qq_system/message/getAllUnreadCount?topic=reply',
    //         success:function (data) {
    //             replyCount = parseInt(data)
    //         }
    //     });
    //     //异步获取所有点赞数量
    //     $.ajax({
    //         url:'/qq_system/message/getAllUnreadCount?topic=like',
    //         success:function (data) {
    //             likeCount = parseInt(data)
    //         }
    //     });
    // }
    //
    // //获取好友验证消息数量
    // function getVarFriendCount() {
    //     $.ajax({
    //         type:"POST",
    //         url:'/qq_system/relation/getAllVarifyFriend',
    //         contentType:"application/json;charset=utf-8",
    //         success:function (data) {
    //             if(data!=null&&data!=""){
    //                 var friend_list = data.split(';');//好友列表
    //                 valFriendCount = friend_list.length;
    //             }else{
    //                 valFriendCount = 0;
    //             }
    //         }
    //     });
    // }
    //
    // //获取未读消息数量
    // function getUnreadedMsgCount() {
    //     //实时获取好友发送的消息数量
    //     $.ajax({
    //         type:"post",
    //         url:"/qq_system/message/getAllUnreadCount?topic=msg",
    //         contentType:"application/json;charset=utf-8",
    //         success:function (data) {
    //             unreadedMsgCount = parseInt(data);
    //         }
    //     });
    // }
    //
    // //显示消息数量、通知数量
    // function showInfo() {
    //
    //     //系统通知数量
    //     getSystemAdviceCount();
    //     //好友验证数量
    //     getVarFriendCount();
    //     //未读消息数量
    //     getUnreadedMsgCount();
    //
    //
    //     //通知总数量
    //     var sumCount = valFriendCount+cmtCount+replyCount+likeCount;
    //     if(sumCount>0){
    //         $("#allAdviceCount").text(sumCount);
    //         $("#allAdviceCount").show();
    //     }else{
    //         $("#allAdviceCount").hide();
    //     }
    //
    //     //未读消息数量
    //     if(unreadedMsgCount==0){
    //         $("#varMsgCount").hide();
    //     }else{
    //         $("#varMsgCount").text(unreadedMsgCount);
    //         $("#varMsgCount").show();
    //     }
    // }
    //
    //
    // showInfo();
    // setInterval(function () {
    //     showInfo();
    // },100);

});
