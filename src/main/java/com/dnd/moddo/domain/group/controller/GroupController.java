package com.dnd.moddo.domain.group.controller;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.service.CommandGroupService;
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

}