package com.starsofocean.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starsofocean.reggie.domain.AddressBook;
import com.starsofocean.reggie.mapper.AddressBookMapper;
import com.starsofocean.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
