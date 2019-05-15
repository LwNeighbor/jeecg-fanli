package org.jeecg.modules.fanli.cash.controller;

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
import org.jeecg.modules.fanli.cash.entity.Cash;
import org.jeecg.modules.fanli.cash.service.ICashService;

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
 * @Description: 提现
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@RestController
@RequestMapping("/cash/cash")
@Slf4j
public class CashController {
	@Autowired
	private ICashService cashService;
	
	/**
	  * 分页列表查询
	 * @param cash
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Cash>> queryPageList(Cash cash,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Cash>> result = new Result<IPage<Cash>>();
		QueryWrapper<Cash> queryWrapper = QueryGenerator.initQueryWrapper(cash, req.getParameterMap());
		Page<Cash> page = new Page<Cash>(pageNo, pageSize);
		IPage<Cash> pageList = cashService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param cash
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Cash> add(@RequestBody Cash cash) {
		Result<Cash> result = new Result<Cash>();
		try {
			cashService.save(cash);
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
	 * @param cash
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Cash> edit(@RequestBody Cash cash) {
		Result<Cash> result = new Result<Cash>();
		Cash cashEntity = cashService.getById(cash.getId());
		if(cashEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = cashService.updateById(cash);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
	}

	 /**
	  *  确认体现
	  * @param cash
	  * @return
	  */
	 @PutMapping(value = "/cash")
	 public Result<Cash> cash(@RequestBody Cash cash) {
		 Result<Cash> result = new Result<Cash>();
		 Cash cashEntity = cashService.getById(cash.getId());
		 if(cashEntity==null) {
			 result.error500("未找到对应实体");
		 }else {
		 	 cash.setCashStatus("2");
			 boolean ok = cashService.updateById(cash);
			 result.success("修改成功!");
		 }

		 return result;
	 }
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<Cash> delete(@RequestParam(name="id",required=true) String id) {
		Result<Cash> result = new Result<Cash>();
		Cash cash = cashService.getById(id);
		if(cash==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = cashService.removeById(id);
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
	public Result<Cash> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Cash> result = new Result<Cash>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.cashService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Cash> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Cash> result = new Result<Cash>();
		Cash cash = cashService.getById(id);
		if(cash==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(cash);
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
      QueryWrapper<Cash> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Cash cash = JSON.parseObject(deString, Cash.class);
              queryWrapper = QueryGenerator.initQueryWrapper(cash, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Cash> pageList = cashService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "提现列表");
      mv.addObject(NormalExcelConstants.CLASS, Cash.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("提现列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Cash> listCashs = ExcelImportUtil.importExcel(file.getInputStream(), Cash.class, params);
              for (Cash cashExcel : listCashs) {
                  cashService.save(cashExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listCashs.size());
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
