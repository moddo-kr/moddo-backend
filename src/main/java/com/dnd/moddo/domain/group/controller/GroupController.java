package com.dnd.moddo.domain.group.controller;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.group.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.service.CommandGroupService;
import com.dnd.moddo.domain.group.service.QueryGroupService;
import com.dnd.moddo.global.jwt.dto.GroupTokenResponse;
import com.dnd.moddo.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController {
    private final CommandGroupService commandGroupService;
    private final JwtService jwtService;
    private final QueryGroupService queryGroupService;

    @PostMapping
    public ResponseEntity<GroupTokenResponse> saveGroup(HttpServletRequest request, @RequestBody GroupRequest groupRequest) {
        Long userId = jwtService.getUserId(request);

        GroupTokenResponse response = commandGroupService.createGroup(groupRequest, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/account")
    public ResponseEntity<GroupResponse> updateAccount(
            HttpServletRequest request,
            @RequestParam("groupToken") String groupToken,
            @RequestBody GroupAccountRequest groupAccountRequest) {
        Long userId = jwtService.getUserId(request);
        Long groupId = jwtService.getGroupId(groupToken);

        GroupResponse response = commandGroupService.updateAccount(groupAccountRequest, userId, groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GroupDetailResponse> getGroup(
            HttpServletRequest request,
            @RequestParam("groupToken") String groupToken) {
        Long userId = jwtService.getUserId(request);
        Long groupId = jwtService.getGroupId(groupToken);

        GroupDetailResponse response = queryGroupService.findOne(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<GroupPasswordResponse> verifyPassword(
            HttpServletRequest request,
            @RequestParam("groupToken") String groupToken,
            @RequestBody GroupPasswordRequest groupPasswordRequest) {
        Long userId = jwtService.getUserId(request);
        Long groupId = jwtService.getGroupId(groupToken);

        GroupPasswordResponse response = commandGroupService.verifyPassword(groupId, userId, groupPasswordRequest);
        return ResponseEntity.ok(response);
    }
}