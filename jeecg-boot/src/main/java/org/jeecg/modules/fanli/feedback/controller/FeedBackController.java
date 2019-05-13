package org.jeecg.modules.fanli.feedback.controller;

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
import org.jeecg.modules.fanli.feedback.entity.FeedBack;
import org.jeecg.modules.fanli.feedback.service.IFeedBackService;

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
 * @Description: 用户反馈
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
@RestController
@RequestMapping("/feedback/feedBack")
@Slf4j
public class FeedBackController {
	@Autowired
	private IFeedBackService feedBackService;
	
	/**
	  * 分页列表查询
	 * @param feedBack
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<FeedBack>> queryPageList(FeedBack feedBack,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<FeedBack>> result = new Result<IPage<FeedBack>>();
		QueryWrapper<FeedBack> queryWrapper = QueryGenerator.initQueryWrapper(feedBack, req.getParameterMap());
		Page<FeedBack> page = new Page<FeedBack>(pageNo, pageSize);
		IPage<FeedBack> pageList = feedBackService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param feedBack
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<FeedBack> add(@RequestBody FeedBack feedBack) {
		Result<FeedBack> result = new Result<FeedBack>();
		try {
			feedBackService.save(feedBack);
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
	 * @param feedBack
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<FeedBack> edit(@RequestBody FeedBack feedBack) {
		Result<FeedBack> result = new Result<FeedBack>();
		FeedBack feedBackEntity = feedBackService.getById(feedBack.getId());
		if(feedBackEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = feedBackService.updateById(feedBack);
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
	public Result<FeedBack> delete(@RequestParam(name="id",required=true) String id) {
		Result<FeedBack> result = new Result<FeedBack>();
		FeedBack feedBack = feedBackService.getById(id);
		if(feedBack==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = feedBackService.removeById(id);
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
	public Result<FeedBack> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<FeedBack> result = new Result<FeedBack>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.feedBackService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<FeedBack> queryById(@RequestParam(name="id",required=true) String id) {
		Result<FeedBack> result = new Result<FeedBack>();
		FeedBack feedBack = feedBackService.getById(id);
		if(feedBack==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(feedBack);
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
      QueryWrapper<FeedBack> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              FeedBack feedBack = JSON.parseObject(deString, FeedBack.class);
              queryWrapper = QueryGenerator.initQueryWrapper(feedBack, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<FeedBack> pageList = feedBackService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "用户反馈列表");
      mv.addObject(NormalExcelConstants.CLASS, FeedBack.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户反馈列表数据", "导出人:Jeecg", "导出信息"));
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
              List<FeedBack> listFeedBacks = ExcelImportUtil.importExcel(file.getInputStream(), FeedBack.class, params);
              for (FeedBack feedBackExcel : listFeedBacks) {
                  feedBackService.save(feedBackExcel);
              }
              return Result.ok("文件导入成功！数据行数：" + listFeedBacks.size());
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
