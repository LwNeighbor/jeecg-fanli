package org.jeecg.modules.fanli.projectRecord.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.fanli.projectRecord.entity.ProjectRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;

import javax.websocket.server.PathParam;

/**
 * @Description: 理财记录
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface ProjectRecordMapper extends BaseMapper<ProjectRecord> {

    @Select("select * from fanli_vip_user where phone=(select phone from fanli_project_record where ID= #{recordId})")
    VipUser selectVipUserByProjectRecordId(@Param("recordId") String recordId);
}
