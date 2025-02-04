package com.dnd.moddo.domain.group.controller;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.service.CommandGroupService;
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
    public ResponseEntity<GroupResponse> saveGroup(HttpServletRequest request, @RequestBody GroupRequest groupRequest) {
        Long userId = jwtService.getUserId(request);

        GroupResponse response = commandGroupService.createGroup(groupRequest, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/account/{postId}")
    public ResponseEntity<GroupResponse> updateAccount(@RequestBody GroupAccountRequest groupAccountRequest, @PathVariable Long postId) {
        GroupResponse response = commandGroupService.updateAccount(groupAccountRequest, postId);
        return ResponseEntity.ok(response);
    }
}
