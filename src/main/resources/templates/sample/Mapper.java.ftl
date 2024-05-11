package ${basePackage}.mapper;

import ${basePackage}.entity.${entity.entityName};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * ${entity.comment!} Mapper 接口
 * </p>
 *
 */
@Mapper
public interface ${entity.entityName}Mapper extends BaseMapper<${entity.entityName}> {

}