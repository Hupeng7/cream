package com.icecreamGroup.major.FeignClient.FeignFallBack;

import com.icecreamGroup.major.FeignClient.CommentsClient;
import org.springframework.stereotype.Component;

@Component
public class CommentsFallback implements CommentsClient {
    @Override
    public String backComments() {
        return "default";
    }
}
