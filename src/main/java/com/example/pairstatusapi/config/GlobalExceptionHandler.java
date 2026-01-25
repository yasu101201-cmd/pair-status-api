package com.example.pairstatusapi.config;


import com.example.pairstatusapi.exception.NotFoundException;
import com.example.pairstatusapi.dto.ApiError;
import com.example.pairstatusapi.exception.ConflictException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, e.getMessage()));
    }

    // 例: joinCode衝突/満員/二重参加などを Conflict に寄せたい場合
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, e.getMessage()));
    }

    // ✅ 入力が悪い（userId不正、joinCode不正、見つからない等をIllegalArgumentで投げてる時）
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(400, e.getMessage()));
    }

    // ✅ 状態が悪い（すでにペア済み、満員、リトライしすぎ 等）
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleStateConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, e.getMessage()));
    }

    // ✅ 最後の砦：想定外はここで“整った500”にする
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ApiError> handleAny(Exception e) {
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body(ApiError.of(500, "Internal Server Error"));
    // }
}