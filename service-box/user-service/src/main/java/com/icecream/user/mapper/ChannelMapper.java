package com.icecream.user.mapper;

import com.icecream.common.model.pojo.Channel;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:57 2018/9/10 0010
 */
@Mapper
public interface ChannelMapper extends tk.mybatis.mapper.common.Mapper<Channel>, MySqlMapper<Channel> {
}
