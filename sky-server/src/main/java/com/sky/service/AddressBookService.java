package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    List<AddressBook> getAll();

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
     *查询默认地址
     * @return
     */
    AddressBook getDefault();

    /**
     * 根据id删除地址
     * @param id
     */
    void delete(Long id);

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
     void updateDefault(AddressBook addressBook);
}
