package com.vibeshop.domain.category.dto;

import lombok.Getter;

@Getter
public class CategoryRequest {
    private String name;
    private String description;
    private Long parentId;
}
