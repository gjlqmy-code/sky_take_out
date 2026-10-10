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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;



        //注入BCrypt加密器
        @Autowired
        private PasswordEncoder passwordEncoder;

        /**
         * 员工登录
         * @param employeeLoginDTO
         * @return
         */
        @Override
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

            // ==========【BCrypt校验，替换原来MD5】==========
            // 不需要手动加密前端密码！使用matches方法比对明文 和 数据库密文
            boolean match = passwordEncoder.matches(password, employee.getPassword());
            if (!match) {
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
        @Override
        public void save(EmployeeDTO employeeDTO) {
            System.out.println("当前线程的id"+Thread.currentThread().getId());
            Employee employee=new Employee();
            //对象属性拷贝方法将DTO拷贝给entity，两个类中变量名必须一致
            BeanUtils.copyProperties(employeeDTO,employee);

            //设置账号状态，默认正常状态1表示正常，0表示锁定，用常量类
            employee.setStatus(StatusConstant.ENABLE);

            // ==========【BCrypt加密默认密码，替换MD5】==========
            String defaultPwd = PasswordConstant.DEFAULT_PASSWORD;
            String encodePwd = passwordEncoder.encode(defaultPwd);
            employee.setPassword(encodePwd);

            //反射代替
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());

            //设置当前记录创建人和修改人
            employee.setCreateUser(BaseContext.getCurrentId());
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

    /**
     * 根据id查询员工
     * @param id
     * @return
     */

    public Employee getById(Long id) {
        Employee employee=employeeMapper.getById(id);
        employee.setPassword("****");//查出来密码后加密再传到前端
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        /*反射employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());*/

        employeeMapper.update(employee);
    }
}
