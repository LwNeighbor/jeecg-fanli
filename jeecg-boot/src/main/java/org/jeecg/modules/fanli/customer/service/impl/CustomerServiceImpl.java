package org.jeecg.modules.fanli.customer.service.impl;

import org.jeecg.modules.fanli.customer.entity.Customer;
import org.jeecg.modules.fanli.customer.mapper.CustomerMapper;
import org.jeecg.modules.fanli.customer.service.ICustomerService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 客服管理
 * @author： jeecg-boot
 * @date：   2019-05-09
 * @version： V1.0
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

}
