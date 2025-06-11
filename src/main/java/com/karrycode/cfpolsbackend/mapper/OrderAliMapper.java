package com.karrycode.cfpolsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrycode.cfpolsbackend.domain.po.OrderAli;
import com.karrycode.cfpolsbackend.domain.vo.ExtOrder;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 支付记录(OrderAli)表数据库访问层
 *
 * @author makejava
 * @since 2025-02-05 19:31:52
 */
public interface OrderAliMapper extends BaseMapper<OrderAli> {

    @Select("select order_ali.id oid, money, payment_method, status, order_ali.create_time, nick_name\n" +
            "from order_ali\n" +
            "         left join `cf-pols-db`.user u on u.id = order_ali.user_id\n" +
            "order by create_time desc;\n")
    List<ExtOrder> listAll();

    @Select("SELECT count(id)\n" +
            "FROM order_ali\n" +
            "WHERE DATE(create_time) = CURDATE();")
    Integer getNowDayCount();

    @Select("SELECT sum(money)\n" +
            "FROM order_ali\n" +
            "WHERE DATE(create_time) = CURDATE();")
    Double getSumNowDay();

    @Select("select COALESCE(SUM(money), 0) from order_ali where user_id=#{userid};")
    double getUserSum(String userId);
}
