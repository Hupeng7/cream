package com.icecreamGroup.major.FeignClient;

import com.icecreamGroup.major.FeignClient.FeignFallBack.CommentsFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name ="comment-service",fallback = CommentsFallback.class)
public interface CommentsClient {
    @RequestMapping("comment/hi")
    String backComments();
}

