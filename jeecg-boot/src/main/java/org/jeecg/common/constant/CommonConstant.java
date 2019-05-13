package org.jeecg.common.constant;

public interface CommonConstant {

	/**
	 * 正常状态
	 */
	Integer STATUS_NORMAL = 0;

	/**
	 * 禁用状态
	 */
	Integer STATUS_DISABLE = -1;

	/**
	 * 删除标志
	 */
	Integer DEL_FLAG_1 = 1;

	/**
	 * 未删除
	 */
	Integer DEL_FLAG_0 = 0;

	/**
	 * 系统日志类型： 登录
	 */
	int LOG_TYPE_1 = 1;

	/**
	 * 系统日志类型： 操作
	 */
	int LOG_TYPE_2 = 2;
	
	
	/** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_OK_200 = 200;
	//前端接口的token失效,重新登陆
	public static final Integer SC_ERROR_9999 = 9999;
	//充值未达到500元
	public static final Integer BUY_NOT_ENOUGH2333 = 2333;
	//余额不足
	public static final Integer MONEY_NOT_ENOUGH2333 = 3333;

    
    public static String PREFIX_USER_ROLE = "PREFIX_USER_ROLE";
    public static String PREFIX_USER_PERMISSION  = "PREFIX_USER_PERMISSION ";
    public static int  TOKEN_EXPIRE_TIME  = 3600; //3600秒即是一小时

	//后端token前缀
    public static String PREFIX_USER_TOKEN  = "PREFIX_USER_TOKEN ";
	//前端token前缀
	public static String PREFIX_FRONT_USER_TOKEN  = "PREFIX_FRONT_USER_TOKEN ";
    
    /**
     *  0：一级菜单
     */
    public static Integer MENU_TYPE_0  = 0;
   /**
    *  1：子菜单 
    */
    public static Integer MENU_TYPE_1  = 1;
    /**
     *  2：按钮权限
     */
    public static Integer MENU_TYPE_2  = 2;
}
