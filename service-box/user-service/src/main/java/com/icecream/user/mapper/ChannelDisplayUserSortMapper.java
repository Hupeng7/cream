package com.icecream.user.mapper;

import com.icecream.common.model.pojo.ChannelDisplayUserSort;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author hp
 * @version 1.0
 * @description: ${description}
 * @date: 14:40 2018/9/10 0010
 */
@Mapper
public interface ChannelDisplayUserSortMapper extends tk.mybatis.mapper.common.Mapper<ChannelDisplayUserSort>, MySqlMapper<ChannelDisplayUserSort> {
}
