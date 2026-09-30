package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的加密密码进行MD5加密处理
         password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
         return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {

        System.out.println("当前线程的id"+Thread.currentThread().getId());

        Employee employee=new Employee();

        //对象属性拷贝方法将DTO拷贝给后一个，spring自带类中的静态方法，两者中变量名必须一致
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置账号状态，默认正常状态1表示正常，0表示锁定，用常量类
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认密码为123456，digestutils摘要工具类通过MD5转换成32位十六进制数hex
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录人id和修改人id
        employee.setCreateUser(BaseContext.getCurrentId());//校验令牌时存进去这里取出来
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
     public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        //select * from employee limit 0,10
         //开始分页查询,使用插件Pagehelper
         //                                       第几页                           每页多少条
         PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
         //底层把分页参数传到ThreadLocal，紧跟后面一句select语句自动加上limit

         Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);
         //用Page对象获得所需值
         long total=page.getTotal();
         List<Employee> records=page.getResult();
        return new PageResult(total,records);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    
    public void startOrStop(Integer status, Long id) {
        //update employee set status =? where id=?

        /*Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);写法一*/

        //写法二运用@Biudler注解，链式创建对象
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);

    }
}
