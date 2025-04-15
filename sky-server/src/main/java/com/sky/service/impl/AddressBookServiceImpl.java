package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @Override
    public List<AddressBook> getAll() {
        Long userId = BaseContext.getCurrentId();
        return  addressBookMapper.getAllByUserId(userId);
    }

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.save(addressBook);
    }

    /**
     * 修改地址信息
     *
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
    }

    public static final Integer ADDRESS_BOOK = 1;

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefault() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookMapper.getAllByUserId(userId);
        for (AddressBook addressBook : list) {
            if (addressBook.getIsDefault() == ADDRESS_BOOK){
                return addressBook;
            }
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    @Override
    public void updateDefault(AddressBook addressBook) {
        //1、将当前用户的所有地址修改为非默认地址 update address_book set is_default = ? where user_id = ?
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}
