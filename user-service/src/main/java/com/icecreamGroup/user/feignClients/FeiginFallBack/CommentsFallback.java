package com.icecreamGroup.user.feignClients.FeiginFallBack;

import com.icecreamGroup.user.feignClients.CommentsClient;
import org.springframework.stereotype.Component;

@Component
public class CommentsFallback implements CommentsClient {
    @Override
    public String backComments() {
        return "default";
    }
}
