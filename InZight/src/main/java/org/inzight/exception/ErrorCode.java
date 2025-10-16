package org.inzight.exception;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    /// User
    UNCATEGORIES_EXCEPTION(9999, "Uncategories exception", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_EXIST(1001, "This email is already in use", HttpStatus.CONFLICT),
    PHONE_EXIST(1002, "This phone is already in use", HttpStatus.CONFLICT),
    UNAUTHORIZED(1003, "You do not have permission", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1004, "Your session has expired. Please login again!", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND),
    INCORRECT_PASSWORD(1006, "Password is incorrect", HttpStatus.BAD_REQUEST),

    /// Category
    CATEGORY_NOT_FOUND(2001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXIST(2002, "Category name already exists", HttpStatus.CONFLICT),

    ///  Budget
    BUDGET_NOT_FOUND(3001, "Budget not found", HttpStatus.NOT_FOUND),

    ///  comment
    USER_NOT_EXISTED(4001, "User not existed", HttpStatus.NOT_FOUND),

    COMMENT_NOT_FOUND(4002, "Comment not found", HttpStatus.NOT_FOUND),
    /// General
    INVALID_REQUEST(9001, "Invalid request", HttpStatus.BAD_REQUEST),

    ;
    ErrorCode(int code, String message, HttpStatusCode statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    int code;
    String message;
    HttpStatusCode statusCode;


}