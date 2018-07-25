package com.icecream.good.sql;

import com.icecream.common.model.pojo.GoodsSpec;

import java.util.List;
import java.util.Map;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/7/24 0024
 */
@SuppressWarnings("all")
public class SqlProvider {

    public String batchInsertGoodsSpec(Map<String, Object> map) {
        List<GoodsSpec> list = (List<GoodsSpec>) map.get("goodsSpecList");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into goods_spec ");
        sb.append("(id,good_id,spec,price,stock,spec_opt) ");
        sb.append("values ");
        String buf="(#{goodsSpecList[*].id,jdbcType=VARCHAR}" +
                        ",#{goodsSpecList[*].goodId, jdbcType=VARCHAR}" +
                        ",#{goodsSpecList[*].spec, jdbcType=VARCHAR}" +
                        ",#{goodsSpecList[*].price, jdbcType=INTEGER}" +
                        ",#{goodsSpecList[*].stock, jdbcType=INTEGER}" +
                        ",#{goodsSpecList[*].specOpt, jdbcType=VARCHAR})";

        for (int i = 0; i < list.size(); i++) {
            sb.append(buf.replace("*",Integer.toString(i)));
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

