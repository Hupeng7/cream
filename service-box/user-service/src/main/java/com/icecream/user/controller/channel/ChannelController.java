package com.icecream.user.controller.channel;

import com.icecream.common.model.model.ListChannelDisplayModel;
import com.icecream.common.util.res.ResultVO;
import com.icecream.user.service.channel.ChannelService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:41 2018/9/10 0010
 */
@RestController
@RequestMapping("channel")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    @GetMapping("updateAndListDisplayWithFan")
    public ResultVO updateAndListChannelDisplay(@Param("specialTokenId") String specialTokenId, @Validated @RequestBody ListChannelDisplayModel listChannelDisplayModel) {
        return channelService.updateAndListChannelDisplay(specialTokenId, listChannelDisplayModel);
    }


}
