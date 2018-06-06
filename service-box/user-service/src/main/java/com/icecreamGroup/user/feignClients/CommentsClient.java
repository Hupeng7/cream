package com.icecreamGroup.user.feignClients;

import com.icecreamGroup.user.feignClients.FeiginFallBack.CommentsFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(name ="comment-service",fallback = CommentsFallback.class)
public interface CommentsClient {
    @RequestMapping("comment/hi")
    String backComments();
}

