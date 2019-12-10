package cn.poile.blog.service;

import cn.poile.blog.entity.Client;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 客户端表 服务类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-06
 */
public interface IClientService extends IService<Client> {

    /**
     * 根据客户端id获取客户端
     * @param clientId
     * @return
     */
    Client getClientByClientId(String clientId);

    /**
     * 清空缓存
     */
    void clearCache();

}
