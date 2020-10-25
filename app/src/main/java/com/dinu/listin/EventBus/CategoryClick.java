package com.dinu.listin.EventBus;

import com.dinu.listin.Model.Category;

public class CategoryClick {
    private boolean success;
    private Category category;

    public CategoryClick(boolean success, Category category) {
        this.success = success;
        this.category = category;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
