package com.ly.crud.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ly.crud.bean.Employee;
import com.ly.crud.bean.Msg;
import com.ly.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理员工CRUD请求
 */
@Controller
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     * 单个批量二合一
     * 批量删除：1-2-3
     * 单个删除1
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
    public Msg deleteEmp(@PathVariable("ids")String ids){
        //批量删除
        if(ids.contains("-")){
            List<Integer> del_ids = new ArrayList<>();
            String[] str_ids = ids.split("-");
            //组装id的集合
            for (String string : str_ids) {
                del_ids.add(Integer.parseInt(string));
            }
            employeeService.deleteBatch(del_ids);
        }else {
            int id = Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }

        return Msg.success();
    }


    /**
     * //如果直接发送ajax=PUT形式的请求
     * 封装的数据
     *
     * 问题：
     * 请求体中有数据
     * 但是Employee对象封装不上
     * update tbl_emp where emp_id = 1014 语法有问题
     *
     * 原因：
     * Tomcat
     *         1.将请求体中的数据，封装一个Map
     *         2.request.getParameter("empName") 就会从map中取值
     *         3.springMVC封装pojo对象的时候
     *                  会把pojo的每个属性只，调用request.getParameter("email")
*         ajax发送put请求引发的问题：
     *          put请求，请求体的数据 request.getParameter("empName") 拿不到
     *          Tomcat一看是put请求，就不会封装请求体中的数据位map，而只有post形式的请求才封装请求体位map
     *
     * 1.我们要能支持直接发送put之类的请求还要封装请求体中的数据
     * 2.配置上FormContentFilter
     * 它的作用，就是将请求体中的数据解析包装成一个map。
     * request被重新包装， request.getParameter() 被重写1，就会从自己封装的map中取出数据
     * 员工更新方法
     * @param employee
     * @return
     */
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    @ResponseBody
    public Msg saveEmp(Employee employee){
        System.out.println(employee);
        employeeService.updateEmp(employee);
        return Msg.success();
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id") Integer id){
        Employee employee = employeeService.getEmp(id);
        return Msg.success().add("emp",employee);
    }

    /**
     * 检查用户名是否可用
     * @param empName
     * @return
     */
    @RequestMapping("/checkuser")
    @ResponseBody
    public Msg checkuser(String empName){
        //先判断用户名是否是合法的表达式
        String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
        if(!empName.matches(regx)){
            return Msg.fail().add("va_msg","用户名必须是6-16位数字和字母的组合或者2-5位中文");
        }

        //数据库用户名重复校验
        boolean b = employeeService.checkUser(empName);
        if(b){
            return Msg.success();
        }else {
            return Msg.fail().add("va_msg","用户名不可用");
        }
    }

    /**
     * 员工保存
     * @Valid 让employee在后台进行验证
     * @return
     */
    @RequestMapping(value = "/emp",method = RequestMethod.POST)
    @ResponseBody
    public Msg saveEmp(@Valid Employee employee, BindingResult result){
        if(result.hasErrors()){
            //校验失败,应该返回失败，在模态框中显示校验失败的错误信息
            Map<String,Object> map = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            errors.forEach(fieldError -> {
                System.out.println("错误的字段名：" + fieldError.getField());
                System.out.println("错误信息：" + fieldError.getDefaultMessage());
                map.put(fieldError.getField(),fieldError.getDefaultMessage());
            });
            return Msg.fail().add("errorFields",map);

        }else {
            employeeService.saveEmp(employee);
            return Msg.success();
        }

    }

    /**
     * 查询所有员工数据
     * @return
     * 导入json包
     */
    @RequestMapping(value = "/empsList/{pn}")
    @ResponseBody
    public Msg getEmps(@PathVariable("pn") Integer pn){
        //这不是分页查询
        //引入PageHelper分页插件
        //在查询之前只需要调用,传入页码pn，以及每页的大小
        PageHelper.startPage(pn, 5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        //使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了。
        //封装了详细的分页信息，包括有我们查询出来的数据,传入连续显示的页数
        PageInfo page = new PageInfo(emps,5);
        return Msg.success().add("pageInfo",page);
    }

    @RequestMapping(value = "/emps")
    public String getEmps(){
        return "list";
    }
}
