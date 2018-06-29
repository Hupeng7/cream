package com.icecream.comment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mr_h
 * @version 1.0
 * 评论相关api
 */
@RestController
@RequestMapping("comment")
public class CommentsController {

    @RequestMapping("hi")
    public String backComments(){
        return "I am Comments";
    }
}
