package org.jeecg.modules.fanli.project.service;

import org.jeecg.modules.fanli.project.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.fanli.vipUser.entity.VipUser;

/**
 * @Description: 项目管理
 * @author： jeecg-boot
 * @date：   2019-05-08
 * @version： V1.0
 */
public interface IProjectService extends IService<Project> {

    //购买项目
    String buyProject(String projectId, VipUser user) throws Exception;
}
