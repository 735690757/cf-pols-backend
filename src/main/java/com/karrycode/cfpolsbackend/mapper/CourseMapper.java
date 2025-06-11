package com.karrycode.cfpolsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.vo.ScoreLevelVO;
import com.karrycode.cfpolsbackend.domain.vo.UserCourseVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 课程表(Course)表数据库访问层
 *
 * @author makejava
 * @since 2025-01-11 17:45:11
 */
public interface CourseMapper extends BaseMapper<Course> {
    @Select("SELECT COUNT(IF(score = 1, 1, NULL)) AS score1,\n" +
            "       COUNT(IF(score = 2, 1, NULL)) AS score2,\n" +
            "       COUNT(IF(score = 3, 1, NULL)) AS score3,\n" +
            "       COUNT(IF(score = 4, 1, NULL)) AS score4,\n" +
            "       COUNT(IF(score = 5, 1, NULL)) AS score5\n" +
            "FROM (SELECT score\n" +
            "      FROM user u\n" +
            "               JOIN `order` o ON o.user_id = u.id\n" +
            "      WHERE o.course_id = #{courseId}\n" +
            "      GROUP BY u.id) t;")
    ScoreLevelVO getCourseScoreFan(Integer courseId);

    @Select("SELECT COUNT(IF(score = 1, 1, NULL)) AS score1,\n" +
            "       COUNT(IF(score = 2, 1, NULL)) AS score2,\n" +
            "       COUNT(IF(score = 3, 1, NULL)) AS score3,\n" +
            "       COUNT(IF(score = 4, 1, NULL)) AS score4,\n" +
            "       COUNT(IF(score = 5, 1, NULL)) AS score5\n" +
            "FROM (SELECT score\n" +
            "      FROM `order` o) t;")
    ScoreLevelVO getAllCourseScoreFan();

    @Select("SELECT * FROM course " +
            "WHERE title LIKE CONCAT('%', #{courseName}, '%') " +
            "OR course_describe LIKE CONCAT('%', #{courseName}, '%')")
    List<Course> fuzzyList(String courseName);

    @Select("select * from course where price>0;")
    List<Course> listNotFree();

    @Select("SELECT count(id) FROM `order` WHERE DATE(create_time) = CURDATE();")
    Integer getNowDayCount();

    @Select("select *\n" +
            "from course\n" +
            "where view_count >= (select max(view_count) from course)\n" +
            "order by view_count desc\n" +
            "limit 1;\n")
    Course getMostPopularCourse();

    @Select({
            "<script>",
            "SELECT c.*",
            "FROM course c",
            "LEFT JOIN interaction i ON c.id = i.course_id AND i.user_id = #{userId}",
            "WHERE c.id IN",
            "<foreach item='id' collection='courseIDList' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "ORDER BY i.view_count DESC",
            "</script>"
    })
    List<Course> sortByUserViewCount(
            @Param("courseIDList") List<Integer> courseIDList,
            @Param("userId") Integer userId
    );

    @Select("select cover,avatar, title, nick_name, score\n" +
            "from `order` o\n" +
            "         left join course c on o.course_id = c.id,\n" +
            "     user\n" +
            "where user_id = #{userId1}\n" +
            "  and o.user_id = user.id;")
    List<UserCourseVO> listUserCourse(Integer userId1);
}
