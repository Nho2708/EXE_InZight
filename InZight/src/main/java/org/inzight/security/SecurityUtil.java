package org.inzight.security;

import org.inzight.entity.Wallet;
import org.inzight.service.SocialService.CommentService;
import org.inzight.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityUtil {

    private WalletService walletService;
    private CommentService commentService;

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) return null;

        return Long.parseLong(auth.getName());
        // hoặc lấy từ UserDetails nếu bạn dùng username khác
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean hasPermissionToModifyWallet(Long walletId) {
        Long currentUserId = getCurrentUserId();

        // ADMIN luôn được phép
        if (isAdmin()) return true;

        // Chỉ chủ sở hữu wallet mới sửa/xoá
        List<Wallet> ownerId = walletService.getWalletsByUser(walletId);
        return ownerId.equals(currentUserId);
    }

    public boolean hasPermissionToModifyComment(Long commentId) {
        Long currentUserId = getCurrentUserId();

        // ADMIN luôn được phép
        if (isAdmin()) return true;

        // Người tạo comment mới được sửa/xoá
        List ownerId = commentService.getCommentsByPostId(commentId);
        return ownerId.equals(currentUserId);
    }
}
