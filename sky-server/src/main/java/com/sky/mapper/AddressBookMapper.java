package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 查询当前登录用户的所有地址信息
     * @param userId
     * @return
     */
    List<AddressBook> getAllByUserId(Long userId);

    /**
     * 新增地址
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void delete(Long id);

    /**
     * 设置非默认地址
     * @param addressBook
     */
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getAllById(Long id);
}
