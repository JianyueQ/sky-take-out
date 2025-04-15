package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AddressBook> getAddressBook(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> getAddressBookList() {
        List<AddressBook> list = addressBookService.getAll();
        return Result.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping()
    public Result<AddressBook> addAddressBook(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<AddressBook> updateAddressBookDefault(@RequestBody AddressBook addressBook) {
        addressBookService.updateDefault(addressBook);
        return Result.success();
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddressBook() {
        AddressBook addressBookDefault = addressBookService.getDefault();
        return Result.success(addressBookDefault);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    @PutMapping()
    public Result<AddressBook> updateAddressBook(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping()
    public Result<Long> deleteAddressBook(Long id) {
        addressBookService.delete(id);
        return Result.success();
    }

}
