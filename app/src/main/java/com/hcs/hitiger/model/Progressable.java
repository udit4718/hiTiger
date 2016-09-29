package com.hcs.hitiger.model;

/**
 * Created by anuj gupta on 5/9/16.
 */
public interface Progressable {
    void showProgress(String title, String message);

    void hideProgress();
}
