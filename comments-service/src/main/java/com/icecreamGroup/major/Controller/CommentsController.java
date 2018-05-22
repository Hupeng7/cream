package com.icecreamGroup.major.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("comment")
public class CommentsController {


    @ResponseBody
    @RequestMapping("hi")
    public String backComments(){
        return "I am Comments";
    }
}
