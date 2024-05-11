package ${basePackage}.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * ${entity.comment!} 前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping("/${entity.entityName}")
@RequiredArgsConstructor
public ${entity.entityName}Controller {

    private final ${entity.entityName}Service service;

    @PostMapping
    public Boolean save(@RequestBody ${entity.entityName} request) {
        return service.saveOrUpdate(request);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return service.removeById(id);
    }

    @GetMapping
    public List<${entity.entityName}> findAll() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ${entity.entityName} findOne(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/page")
    public Page<${entity.entityName}> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return service.page(new Page<>(pageNum, pageSize));
    }
}


