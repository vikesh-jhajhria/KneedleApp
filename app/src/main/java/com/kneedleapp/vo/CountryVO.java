package com.kneedleapp.vo;

/**
 * Created by Vikesh on 31-03-2017.
 */
public class CountryVO {
    public CountryVO(String name) {
        setName(name);
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebCode() {
        return webCode;
    }

    public void setWebCode(String webCode) {
        this.webCode = webCode;
    }



    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    private String countryID;
    private String stateID;
    private String name;
    private String webCode;
}
