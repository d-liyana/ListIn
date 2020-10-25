package com.dinu.listin.EventBus;

import com.dinu.listin.Model.PopularCategory;

public class PoplarCategoryClick {
    private PopularCategory popularCategory;

    public PoplarCategoryClick(PopularCategory popularCategory) {
        this.popularCategory = popularCategory;
    }

    public PopularCategory getPopularCategory() {
        return popularCategory;
    }

    public void setPopularCategory(PopularCategory popularCategory) {
        this.popularCategory = popularCategory;
    }
}
