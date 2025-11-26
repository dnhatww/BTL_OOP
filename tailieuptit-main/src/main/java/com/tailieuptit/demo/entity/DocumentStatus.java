package com.tailieuptit.demo.entity;

/**
 * Định nghĩa các trạng thái của tài liệu
 * PENDING: Chờ duyệt
 * APPROVED: Đã duyệt, hiển thị công khai
 * REJECTED: Bị từ chối
 */
public enum DocumentStatus {
    PENDING,
    APPROVED,
    REJECTED
}