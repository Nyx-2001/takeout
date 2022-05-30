package com.starsofocean.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starsofocean.reggie.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
