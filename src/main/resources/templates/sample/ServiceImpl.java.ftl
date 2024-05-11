package ${basePackage}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${basePackage}.entity.${entity.entityName}
import ${basePackage}.mapper.${entity.entityName}Mapper
import ${basePackage}.service.${entity.entityName}Service
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${entity.comment!} 服务实现类
 * </p>
 *
 */
@Service
public class ${entity.entityName}ServiceImpl extends ServiceImpl<${entity.entityName}Mapper, ${entity.entityName}> implements ${entity.entityName}Service {

}