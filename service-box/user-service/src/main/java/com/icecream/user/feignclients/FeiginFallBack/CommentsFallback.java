package com.icecream.user.feignclients.FeiginFallBack;

import com.icecream.user.feignclients.CommentsClient;
import org.springframework.stereotype.Component;

@Component
public class CommentsFallback implements CommentsClient {
    @Override
    public String backComments() {
        return "default";
    }
}
