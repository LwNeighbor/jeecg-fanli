package org.jeecg.modules.fanli.fanli.controller;

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
import org.jeecg.modules.fanli.fanli.entity.Fanli;
import org.jeecg.modules.fanli.fanli.service.IFanliService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Title: Controller
 * @Description: 平台基本设置
 * @author： jeecg-boot
 * @date：   2019-05-09
 * @version： V1.0
 */
@RestController
@RequestMapping("/fanli/fanli")
@Slf4j
public class FanliController {
	@Autowired
	private IFanliService fanliService;
	
	/**
	  * 分页列表查询
	 * @param fanli
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<Fanli>> queryPageList(Fanli fanli,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<Fanli>> result = new Result<IPage<Fanli>>();
		QueryWrapper<Fanli> queryWrapper = QueryGenerator.initQueryWrapper(fanli, req.getParameterMap());
		Page<Fanli> page = new Page<Fanli>(pageNo, pageSize);
		IPage<Fanli> pageList = fanliService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param fanli
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<Fanli> add(@RequestBody Fanli fanli) {
		Result<Fanli> result = new Result<Fanli>();
		try {

			List<Fanli> list = fanliService.list();
			if(list.size() > 0){
				Fanli fanli1 = list.get(0);
				if(fanli.getLowMoney() != null){
					fanli1.setLowMoney(fanli.getLowMoney());
				}
				if(fanli.getBuyDesc() != null){
					fanli1.setBuyDesc(fanli.getBuyDesc());
				}
				if(fanli.getNewCourse() != null){
					fanli1.setNewCourse(fanli.getNewCourse());
				}
				if(fanli.getRepaymentNotice() != null){
					fanli1.setRepaymentNotice(fanli.getRepaymentNotice());
				}
				fanliService.updateById(fanli1);
			}else {
				fanliService.save(fanli);
			}
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
	 * @param fanli
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<Fanli> edit(@RequestBody Fanli fanli) {
		Result<Fanli> result = new Result<Fanli>();
		Fanli fanliEntity = fanliService.getById(fanli.getId());
		if(fanliEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = fanliService.updateById(fanli);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
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
	public Result<Fanli> delete(@RequestParam(name="id",required=true) String id) {
		Result<Fanli> result = new Result<Fanli>();
		Fanli fanli = fanliService.getById(id);
		if(fanli==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = fanliService.removeById(id);
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
	public Result<Fanli> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<Fanli> result = new Result<Fanli>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.fanliService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<Fanli> queryById(@RequestParam(name="id",required=true) String id) {
		Result<Fanli> result = new Result<Fanli>();
		Fanli fanli = fanliService.getById(id);
		if(fanli==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(fanli);
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
      QueryWrapper<Fanli> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              Fanli fanli = JSON.parseObject(deString, Fanli.class);
              queryWrapper = QueryGenerator.initQueryWrapper(fanli, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<Fanli> pageList = fanliService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "平台基本设置列表");
      mv.addObject(NormalExcelConstants.CLASS, Fanli.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("平台基本设置列表数据", "导出人:Jeecg", "导出信息"));
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
              List<Fanli> listFanlis = ExcelImportUtil.importExcel(file.getInputStream(), Fanli.class, params);
              for (Fanli fanliExcel : listFanlis) {
                  fanliService.save(fanliExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listFanlis.size());
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
