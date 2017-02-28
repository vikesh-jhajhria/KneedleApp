package com.kneedleapp.vo;

import android.graphics.drawable.Drawable;

/**
 * Created by saloni.bhansali on 11/7/2016.
 */

public class ListVo {
    public String ProjectName;
    public Drawable image;


    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
