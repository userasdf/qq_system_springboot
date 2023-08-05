package com.work.qq_system_springboot.service.impl;

import com.work.qq_system_springboot.entity.Comment;
import com.work.qq_system_springboot.mapper.CommentMapper;
import com.work.qq_system_springboot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public int addComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }

    @Override
    public Comment getCommentById(int id) {
        return commentMapper.getCommentById(id);
    }
}
