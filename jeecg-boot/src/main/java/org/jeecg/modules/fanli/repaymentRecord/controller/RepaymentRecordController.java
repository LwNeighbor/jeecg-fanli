package org.jeecg.modules.fanli.repaymentRecord.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;

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
 * @Description: 返利记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@RestController
@RequestMapping("/repaymentRecord/repaymentRecord")
@Slf4j
public class RepaymentRecordController {
	@Autowired
	private IRepaymentRecordService repaymentRecordService;
	
	/**
	  * 分页列表查询
	 * @param repaymentRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<RepaymentRecord>> queryPageList(RepaymentRecord repaymentRecord,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<RepaymentRecord>> result = new Result<IPage<RepaymentRecord>>();
		QueryWrapper<RepaymentRecord> queryWrapper = QueryGenerator.initQueryWrapper(repaymentRecord, req.getParameterMap());
		Page<RepaymentRecord> page = new Page<RepaymentRecord>(pageNo, pageSize);
		IPage<RepaymentRecord> pageList = repaymentRecordService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  * 分页列表查询
	  * @param repaymentRecord
	  * @param req
	  * @return
	  */
	 @GetMapping(value = "/inlineList")
	 public Result<List<RepaymentRecord>> queryPageList(RepaymentRecord repaymentRecord,HttpServletRequest req) {
		 Result<List<RepaymentRecord>> result = new Result<List<RepaymentRecord>>();
		 QueryWrapper<RepaymentRecord> queryWrapper = QueryGenerator.initQueryWrapper(repaymentRecord, req.getParameterMap());
		 List<RepaymentRecord> pageList = repaymentRecordService.list(queryWrapper);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }

	/**
	  *   添加
	 * @param repaymentRecord
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<RepaymentRecord> add(@RequestBody RepaymentRecord repaymentRecord) {
		Result<RepaymentRecord> result = new Result<RepaymentRecord>();
		try {
			repaymentRecordService.save(repaymentRecord);
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
	 * @param repaymentRecord
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<RepaymentRecord> edit(@RequestBody RepaymentRecord repaymentRecord) {
		Result<RepaymentRecord> result = new Result<RepaymentRecord>();
		RepaymentRecord repaymentRecordEntity = repaymentRecordService.getById(repaymentRecord.getId());
		if(repaymentRecordEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = repaymentRecordService.updateById(repaymentRecord);
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
	public Result<RepaymentRecord> delete(@RequestParam(name="id",required=true) String id) {
		Result<RepaymentRecord> result = new Result<RepaymentRecord>();
		RepaymentRecord repaymentRecord = repaymentRecordService.getById(id);
		if(repaymentRecord==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = repaymentRecordService.removeById(id);
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
	public Result<RepaymentRecord> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<RepaymentRecord> result = new Result<RepaymentRecord>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.repaymentRecordService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<RepaymentRecord> queryById(@RequestParam(name="id",required=true) String id) {
		Result<RepaymentRecord> result = new Result<RepaymentRecord>();
		RepaymentRecord repaymentRecord = repaymentRecordService.getById(id);
		if(repaymentRecord==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(repaymentRecord);
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
      QueryWrapper<RepaymentRecord> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              RepaymentRecord repaymentRecord = JSON.parseObject(deString, RepaymentRecord.class);
              queryWrapper = QueryGenerator.initQueryWrapper(repaymentRecord, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<RepaymentRecord> pageList = repaymentRecordService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "返利记录列表");
      mv.addObject(NormalExcelConstants.CLASS, RepaymentRecord.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("返利记录列表数据", "导出人:Jeecg", "导出信息"));
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
              List<RepaymentRecord> listRepaymentRecords = ExcelImportUtil.importExcel(file.getInputStream(), RepaymentRecord.class, params);
              for (RepaymentRecord repaymentRecordExcel : listRepaymentRecords) {
                  repaymentRecordService.save(repaymentRecordExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listRepaymentRecords.size());
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
