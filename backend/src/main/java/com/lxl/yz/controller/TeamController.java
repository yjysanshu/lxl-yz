package com.lxl.yz.controller;

import com.lxl.yz.dto.ApiResponse;
import com.lxl.yz.entity.Team;
import com.lxl.yz.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;

    @GetMapping("/list")
    public ApiResponse<List<Team>> listTeams() {
        try {
            List<Team> teams = teamRepository.findAll();
            return ApiResponse.success(teams);
        } catch (Exception e) {
            log.error("查询团队列表失败", e);
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
}
