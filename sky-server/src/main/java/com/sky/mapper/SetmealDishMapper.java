package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 关联菜品和套餐的mapper
 */
@Mapper
public interface  SetmealDishMapper {

    /**
     * 根据菜品id查询关联的套餐数量
     * @param dishIds
     * @return
     */
    //select setmeal id from setmeal dish where dish_id in (1,2,3,4)
    List<Long> getSetmealIdByDishIds(List<Long> dishIds);
}
