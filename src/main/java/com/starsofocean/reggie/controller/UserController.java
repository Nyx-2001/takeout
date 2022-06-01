package com.starsofocean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starsofocean.reggie.common.R;
import com.starsofocean.reggie.domain.User;
import com.starsofocean.reggie.service.UserService;
import com.starsofocean.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用腾讯云短信服务API完成短信发送
//            SendSms.sendMsg(code,phone);
//            SendMsg.sendMsg(phone,code);
            //将生成的手机验证码保存到Session
//            session.setAttribute(phone,code);
            //将生成的手机验证码缓存到redis中，并设置有效时间为5分钟
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机短信验证码已发送");
        }
        return R.error("手机短信验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取用户手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从sessio中获取验证码
//      Object codeInSession = session.getAttribute(phone);
        //从redis缓存中获取验证码
        Object codeInSession=stringRedisTemplate.opsForValue().get(phone);
        //验证码比对
        if(codeInSession!=null&&codeInSession.equals(code)){
            //比对成功则登录成功
            //判断当前用户是否为新用户，如果是新用户则自动注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper=new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //登录成功则立即将验证码从缓存中清除
            stringRedisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 移动端用户退出登录
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
