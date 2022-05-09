package com.ly.crud.service;

import com.ly.crud.bean.Employee;

import java.util.List;

public interface EmployeeService {

    /**
     * 返回所有员工信息
     * @return
     */
    List<Employee> getAll();

    /**
     * 员工保存
     * @param employee
     */
    void saveEmp(Employee employee);

    /**
     * 验证用户名是否可用
     * @param empName
     * @return true:代表当前用户名可用 false 不可用
     */
    boolean checkUser(String empName);

    /**
     * 按照员工id查询员工
     * @param id
     * @return
     */
    Employee getEmp(Integer id);

    /**
     * 员工更新
     * @param employee
     */
    void updateEmp(Employee employee);

    /**
     * 根据指定id删除员工
     * @param id
     */
    void deleteEmp(Integer id);

    void deleteBatch(List<Integer> ids);
}
