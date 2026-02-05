package com.lxl.yz.controller;

import com.lxl.yz.dto.ApiResponse;
import com.lxl.yz.entity.Config;
import com.lxl.yz.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/config")
@Slf4j
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigRepository configRepository;

    @GetMapping("/{key}")
    public ApiResponse<Config> getConfig(@PathVariable String key) {
        try {
            Optional<Config> config = configRepository.findByConfigKey(key);
            if (config.isPresent()) {
                return ApiResponse.success(config.get());
            } else {
                return ApiResponse.error("配置不存在");
            }
        } catch (Exception e) {
            log.error("查询配置失败", e);
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    @PutMapping("/{key}")
    public ApiResponse<String> updateConfig(@PathVariable String key, @RequestBody Config configData) {
        try {
            Optional<Config> configOpt = configRepository.findByConfigKey(key);
            if (configOpt.isPresent()) {
                Config config = configOpt.get();
                config.setConfigValue(configData.getConfigValue());
                configRepository.save(config);
                return ApiResponse.success("配置更新成功");
            } else {
                return ApiResponse.error("配置不存在");
            }
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }
}
