package org.jeecg.modules.fanli.vipUser.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;
import org.jeecg.modules.fanli.vipUser.service.IVipUserService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Title: Controller
 * @Description: 会员管理
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@RestController
@RequestMapping("/vipuser/vipUser")
@Slf4j
public class VipUserController {
	@Autowired
	private IVipUserService vipUserService;
	
	/**
	  * 分页列表查询
	 * @param vipUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<VipUser>> queryPageList(VipUser vipUser,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<VipUser>> result = new Result<IPage<VipUser>>();
		QueryWrapper<VipUser> queryWrapper = QueryGenerator.initQueryWrapper(vipUser, req.getParameterMap());
		Page<VipUser> page = new Page<VipUser>(pageNo, pageSize);
		IPage<VipUser> pageList = vipUserService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param vipUser
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<VipUser> add(@RequestBody VipUser vipUser) {
		Result<VipUser> result = new Result<VipUser>();
		try {
			vipUserService.save(vipUser);
			result.success("添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param vipUser
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<VipUser> edit(@RequestBody VipUser vipUser) {
		Result<VipUser> result = new Result<VipUser>();
		VipUser vipUserEntity = vipUserService.getById(vipUser.getId());
		if(vipUserEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = vipUserService.updateById(vipUser);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		return result;
	}
	 /**
	  *  充值
	  * @param vipUser
	  * @return
	  */
	 @PutMapping(value = "/recharge")
	 public Result<VipUser> recharge(@RequestBody VipUser vipUser) {
		 Result<VipUser> result = new Result<VipUser>();
		 VipUser vipUserEntity = vipUserService.getById(vipUser.getId());
		 if(vipUserEntity==null) {
			 result.error500("未找到对应实体");
		 }else {
		 	//充值,更新用户余额以及生成充值记录
			 try {
				 vipUserService.rechargeVip(vipUser);
				 result.success("操作成功");
			 } catch (Exception e) {
				 e.printStackTrace();
				 result.error500("操作失败");
			 }

		 }
		 return result;
	 }
	 /**
	  *  提现
	  * @param vipUser
	  * @return
	  */
	 @PutMapping(value = "/cash")
	 public Result<VipUser> cash(@RequestBody VipUser vipUser) {
		 Result<VipUser> result = new Result<VipUser>();
		 VipUser vipUserEntity = vipUserService.getById(vipUser.getId());
		 if(vipUserEntity==null) {
			 result.error500("未找到对应实体");
		 }else {
		 	//判断该用户是否绑定账号
			 if(vipUserEntity.getCashAccount().equals("-1")){
				 result.error500("该用户未绑定提现账号,暂无法提现");
				 return result;
			 }
			 //充值,更新用户余额以及生成提现记录
			 try {
				 vipUserService.cashVip(vipUser);
				 result.success("操作成功");
			 } catch (Exception e) {
				 e.printStackTrace();
				 result.error500("操作失败");
			 }

		 }
		 return result;
	 }
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<VipUser> delete(@RequestParam(name="id",required=true) String id) {
		Result<VipUser> result = new Result<VipUser>();
		VipUser vipUser = vipUserService.getById(id);
		if(vipUser==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = vipUserService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<VipUser> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<VipUser> result = new Result<VipUser>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.vipUserService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<VipUser> queryById(@RequestParam(name="id",required=true) String id) {
		Result<VipUser> result = new Result<VipUser>();
		VipUser vipUser = vipUserService.getById(id);
		if(vipUser==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(vipUser);
			result.setSuccess(true);
		}
		return result;
	}

  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<VipUser> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              VipUser vipUser = JSON.parseObject(deString, VipUser.class);
              queryWrapper = QueryGenerator.initQueryWrapper(vipUser, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<VipUser> pageList = vipUserService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "会员管理列表");
      mv.addObject(NormalExcelConstants.CLASS, VipUser.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("会员管理列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }

  /**
      * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<VipUser> listVipUsers = ExcelImportUtil.importExcel(file.getInputStream(), VipUser.class, params);
              for (VipUser vipUserExcel : listVipUsers) {
                  vipUserService.save(vipUserExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listVipUsers.size());
          } catch (Exception e) {
              log.error(e.getMessage());
              return Result.error("文件导入失败！");
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
  }

}
