package cn.poile.blog.service.impl;

import cn.poile.blog.entity.Client;
import cn.poile.blog.mapper.ClientMapper;
import cn.poile.blog.service.IClientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户端表 服务实现类
 * </p>
 *
 * @author yaohw
 * @since 2019-12-06
 */
@Service
@Log4j2
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements IClientService {

    /**
     * 根据客户端id获取客户端
     *
     * @param clientId
     * @return
     */
    @Override
    @Cacheable(value = "client", key = "#clientId")
    public Client getClientByClientId(String clientId) {
        QueryWrapper<Client> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Client::getClientId,clientId);
        return getOne(queryWrapper,false);
    }

    /**
     * 清空缓存
     */
    @Override
    @CacheEvict(value = "client",allEntries = true)
    public void clearCache() {
        log.info("清空client缓存");
    }
}
