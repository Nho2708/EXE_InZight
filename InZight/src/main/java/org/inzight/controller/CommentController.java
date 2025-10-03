package org.inzight.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.CommentRequest;
import org.inzight.dto.request.PostRequest;
import org.inzight.dto.response.CommentResponse;
import org.inzight.dto.response.PostResponse;
import org.inzight.service.SocialService.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAll() {
        return ResponseEntity.ok(commentService.getAll());
    }
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") Long id,
                                                   @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(id, request));
    }
    @GetMapping("/me")
    public ResponseEntity<List<CommentResponse>> getMyComments() {
        List<CommentResponse> comments = commentService.getMyComments();
        return ResponseEntity.ok(comments);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
