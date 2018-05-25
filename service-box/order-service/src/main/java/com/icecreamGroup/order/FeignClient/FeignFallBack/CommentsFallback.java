package com.icecreamGroup.order.FeignClient.FeignFallBack;

import com.icecreamGroup.order.FeignClient.CommentsClient;
import org.springframework.stereotype.Component;

@Component
public class CommentsFallback implements CommentsClient {
    @Override
    public String backComments() {
        return "default";
    }
}
