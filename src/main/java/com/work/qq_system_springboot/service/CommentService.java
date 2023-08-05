package com.work.qq_system_springboot.service;

import com.work.qq_system_springboot.entity.Comment;

public interface CommentService {

    //添加一条评论
    public int addComment(Comment comment);

    //获取评论作者id
    public Comment getCommentById(int id);

}
