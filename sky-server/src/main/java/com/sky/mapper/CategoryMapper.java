package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 菜单状态
     * @param category
     */
    void update(Category category);

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    /**
     * 新增分类
     * @param category
     */
    void save(Category category);

    /**
     * 删除分类
     * @param id
     */
    void delete(Long id);
}
