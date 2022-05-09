package com.ly.crud.service;

import com.ly.crud.bean.Department;

import java.util.List;

public interface DepartmentService {

    /**
     * 查询所有部门信息
     * @return
     */
    List<Department> getDepts();
}
