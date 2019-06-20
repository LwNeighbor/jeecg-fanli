package org.jeecg.modules.fanli.projectRecord.controller;

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
import org.jeecg.modules.fanli.project.entity.Project;
import org.jeecg.modules.fanli.projectRecord.entity.ProjectRecord;
import org.jeecg.modules.fanli.projectRecord.service.IProjectRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.fanli.repaymentRecord.entity.RepaymentRecord;
import org.jeecg.modules.fanli.repaymentRecord.service.IRepaymentRecordService;
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
 * @Description: 理财记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@RestController
@RequestMapping("/projectRecord/projectRecord")
@Slf4j
public class 	ProjectRecordController {
	@Autowired
	private IProjectRecordService projectRecordService;
	@Autowired
	private IRepaymentRecordService repaymentRecordService;
	
	/**
	  * 分页列表查询
	 * @param projectRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<ProjectRecord>> queryPageList(ProjectRecord projectRecord,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<ProjectRecord>> result = new Result<IPage<ProjectRecord>>();
		QueryWrapper<ProjectRecord> queryWrapper = QueryGenerator.initQueryWrapper(projectRecord, req.getParameterMap());
		Page<ProjectRecord> page = new Page<ProjectRecord>(pageNo, pageSize);
		IPage<ProjectRecord> pageList = projectRecordService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param projectRecord
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<ProjectRecord> add(@RequestBody ProjectRecord projectRecord) {
		Result<ProjectRecord> result = new Result<ProjectRecord>();
		try {
			projectRecordService.save(projectRecord);
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
	 * @param projectRecord
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<ProjectRecord> edit(@RequestBody ProjectRecord projectRecord) {
		Result<ProjectRecord> result = new Result<ProjectRecord>();
		ProjectRecord projectRecordEntity = projectRecordService.getById(projectRecord.getId());
		if(projectRecordEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = projectRecordService.updateById(projectRecord);
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
	public Result<ProjectRecord> delete(@RequestParam(name="id",required=true) String id) {
		Result<ProjectRecord> result = new Result<ProjectRecord>();
		ProjectRecord projectRecord = projectRecordService.getById(id);
		if(projectRecord==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = projectRecordService.removeById(id);
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
	public Result<ProjectRecord> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<ProjectRecord> result = new Result<ProjectRecord>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.projectRecordService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<ProjectRecord> queryById(@RequestParam(name="id",required=true) String id) {
		Result<ProjectRecord> result = new Result<ProjectRecord>();
		ProjectRecord projectRecord = projectRecordService.getById(id);
		if(projectRecord==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(projectRecord);
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
      QueryWrapper<ProjectRecord> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              ProjectRecord projectRecord = JSON.parseObject(deString, ProjectRecord.class);
              queryWrapper = QueryGenerator.initQueryWrapper(projectRecord, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<ProjectRecord> pageList = projectRecordService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "理财记录列表");
      mv.addObject(NormalExcelConstants.CLASS, ProjectRecord.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("理财记录列表数据", "导出人:Jeecg", "导出信息"));
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
              List<ProjectRecord> listProjectRecords = ExcelImportUtil.importExcel(file.getInputStream(), ProjectRecord.class, params);
              for (ProjectRecord projectRecordExcel : listProjectRecords) {
                  projectRecordService.save(projectRecordExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listProjectRecords.size());
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

	 /**
	  *  任务中断
	  * @param record
	  * @return
	  */
	 @PostMapping(value = "/breakUpDown")
	 public Result<ProjectRecord> breakUpDown(@RequestBody ProjectRecord record) {
		 Result<ProjectRecord> result = new Result<ProjectRecord>();
		 try {
			 ProjectRecord projectRecord = projectRecordService.getById(record.getId());
			 if(projectRecord.getUpdown().equalsIgnoreCase("Y")){
			 	//说明该记录已经被中断了
				 result.success("修改成功！");
				 return result;
			 }
			 //中断任务,将已返金额更新至用户余额
			 projectRecordService.breakUpDown(projectRecord);

			 result.success("添加成功！");
		 } catch (Exception e) {
			 e.printStackTrace();
			 log.info(e.getMessage());
			 result.error500("操作失败");
		 }
		 return result;
	 }

}
