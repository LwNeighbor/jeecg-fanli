package org.jeecg.modules.front;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.FrontCodeConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;
import org.jeecg.modules.shiro.authc.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BaseController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IVipUserService vipUserService;

    /**
     * 发送短信
     * @param phone
     * @throws Exception
     */
    public int sendMsg(String phone) throws Exception {
        String url = "https://dx.ipyy.net/sms.aspx";
        String accountName="renwu190425";							//改为实际账号名
        String password="renwu190425";								//改为实际发送密码

        int i = RandomUtil.randomInt(1000,10000);
        String text = "";
        //注册
        text = String.format(FrontCodeConstant.REGISTER_MSG_TEMPLATE,String.valueOf(i));

        Map map = new HashMap();
        map.put("action","send");
        map.put("userid", "");
        map.put("account", accountName);
        map.put("password", password);
        map.put("mobile", phone);       //多个手机号用逗号分隔
        map.put("content", text);
        map.put("sendTime", "");
        map.put("extno", "");

        HttpResponse execute = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .timeout(10000)
                .form(map)
                .execute();
        return i;
    }

    /**
     * 校验token是否过期
     * @param token
     * @return
     */
    public VipUser verify(String token){
        Object o = redisUtil.get(CommonConstant.PREFIX_FRONT_USER_TOKEN + token);
        if(o == null){
            //token 已经过期
            return null;
        }else {
            //更新过期时间
            redisUtil.set(CommonConstant.PREFIX_FRONT_USER_TOKEN + token,String.valueOf(o),JwtUtil.FRONT_EXPIRE_TIME/1000);
            String phone = String.valueOf(redisUtil.get(CommonConstant.PREFIX_FRONT_USER_TOKEN + token));
            QueryWrapper<VipUser> userWraper = new QueryWrapper<>();
            userWraper.eq("phone",phone);
            VipUser user = vipUserService.getOne(userWraper);
            return user;
        }
    }

}
