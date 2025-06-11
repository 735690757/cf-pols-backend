package com.karrycode.cfpolsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrycode.cfpolsbackend.domain.po.Order;
import com.karrycode.cfpolsbackend.domain.vo.*;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 订单表(Order)表数据库访问层
 *
 * @author makejava
 * @since 2025-01-11 17:46:24
 */
public interface OrderMapper extends BaseMapper<Order> {
    @Select("select title, amount, o.create_time from `order` o left join `cf-pols-db`.course c " +
            "on o.course_id = c.id where c.teacher_id = #{teacherId} order by create_time desc;")
    List<CourseOrderVO> getTeacherEarning(Integer teacherId);

    @Select("WITH RECURSIVE date_series AS (\n" +
            "  SELECT DATE('2025-04-04') AS date\n" +
            "  UNION ALL\n" +
            "  SELECT DATE_ADD(date, INTERVAL 1 DAY)\n" +
            "  FROM date_series\n" +
            "  WHERE date < CURDATE()\n" +
            ")\n" +
            "SELECT\n" +
            "  ds.date,\n" +
            "  COUNT(o.id) AS now_day_count,\n" +
            "  SUM(COUNT(o.id)) OVER (ORDER BY ds.date) AS cumulative_order_count\n" +
            "FROM date_series ds\n" +
            "LEFT JOIN `order` o\n" +
            "  ON DATE(o.create_time) = ds.date AND o.course_id = #{courseId}\n" +
            "GROUP BY ds.date\n" +
            "ORDER BY ds.date;")
    List<OrderCumulativeVO> listCourseTrend(Integer courseId);

    @Select("select course_id, score, teacher_id, nick_name, avatar, cover, title, `order`.create_time\n" +
            "from `order`\n" +
            "         left join `cf-pols-db`.course c on c.id = `order`.course_id\n" +
            "         left join `cf-pols-db`.user u on c.teacher_id = u.id\n" +
            "where user_id =#{uid}")
    List<StudentCourse> listStudentCourse(Integer uid);

    @Select("select cover, title, status, view_count, buy_count, price,ROUND(AVG(score), 2) avgScore\n" +
            "from course\n" +
            "         left join `cf-pols-db`.`order` o on course.id = o.course_id\n" +
            "where teacher_id = #{uid} group by course_id;")
    List<TeacherCourseVO> listTeacherCourse(Integer uid);

    @Select("select title, avatar, nick_name, amount, score, `order`.create_time\n" +
            "from `order`\n" +
            "         left join `cf-pols-db`.course c on c.id = `order`.course_id\n" +
            "         left join `cf-pols-db`.user u on `order`.user_id = u.id\n" +
            "order by create_time desc")
    List<SysInnerOrder> listSysOrderUpper();

    @Select("select sum(amount) from `order`")
    Double getAmount();

    @Select("select round(count(case when score >= 4 then 1 end) / count(id) * 100, 2) as 'rate'\n" +
            "from `order`\n" +
            "where course_id = #{courseId};")
    double getAcclaimCount(Integer courseId);

    @Select("select user_id,\n" +
            "       course_id,\n" +
            "       score\n" +
            "from `order`;")
    List<UccVO> listUcc();
}
