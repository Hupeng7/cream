package com.icecream.user.mapper;

import com.icecream.common.model.pojo.ChannelDisplay;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 11:52 2018/9/10 0010
 */
@Mapper
public interface ChannelDisplayMapper extends tk.mybatis.mapper.common.Mapper<ChannelDisplay>, MySqlMapper<ChannelDisplay> {
}

