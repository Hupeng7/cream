package com.icecreamGroup.major.feignClients.FeiginFallBack;

import com.icecreamGroup.major.feignClients.CommentsClient;
import org.springframework.stereotype.Component;

@Component
public class CommentsFallback implements CommentsClient {
    @Override
    public String backComments() {
        return "default";
    }
}
