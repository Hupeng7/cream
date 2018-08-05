package com.icecream.user.service.login;


import com.icecream.common.util.res.ResultVO;

/**
 * @version 2.0
 */
public interface SuperLogin<T> {

    ResultVO login(T model);
}
