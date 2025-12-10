package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.PostRequest;
import org.inzight.dto.response.PostResponse;
import org.inzight.entity.Post;
import org.inzight.entity.Transaction;
import org.inzight.service.SocialService.PostService;
import org.inzight.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class PostController {

    private final PostService postService;

    // add post - nhận multipart/form-data với file ảnh
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PostResponse> create(
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        PostRequest request = PostRequest.builder()
                .content(content != null ? content : "")
                .imageUrl(null) // Sẽ được set trong service sau khi upload
                .build();
        return ResponseEntity.ok(postService.createPost(request, image));
    }
    // edit post - nhận multipart/form-data với file ảnh
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("id") Long id,
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        PostRequest request = PostRequest.builder()
                .content(content != null ? content : "")
                .imageUrl(null) // Sẽ được set trong service sau khi upload
                .build();
        return ResponseEntity.ok(postService.updatePost(id, request, image));
    }
    // get post by id
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }
    // get all post
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getAll(userDetails));
    }
    // get user's post
    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts() {
        List<PostResponse> posts = postService.getMyPosts();
        return ResponseEntity.ok(posts);
    }
    // delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
