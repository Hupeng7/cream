package com.icecreamGroup.common.util.res;

/**
 * 统一的结果返回工具
 */
public class ResultUtil {
    /**
     * 返回成功
     * @param data
     * @param <T>
     * @return
     */
    public static<T> ResultVO success(T data) {
        ResultVO resultVO = new ResultVO();
        ResultEnum successEnum = ResultEnum.SUCCESS;
        resultVO.setCode(successEnum.getCode());
        resultVO.setMsg(successEnum.getMsg());
        resultVO.setData(data);
        return resultVO;
    }

    /**
     * 无返回值data结果集
     * @return
     */
    public static ResultVO success() {
        return success(null);
    }


    /**
     * 返回错误
     * @param data
     * @return
     */
    public static<T> ResultVO error(T data,ResultEnum resultEnum) {
        ResultVO resultVO = new ResultVO();
        resultVO.setData(data);
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMsg(resultEnum.getMsg());
        return resultVO;
    }


    public static ResultVO error(Integer code,String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }


}