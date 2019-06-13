package com.fprint.fingerprintaar;

import java.io.Serializable;

/**
 * Created by GlobalCharge on 13/06/2019.
 */
public class FingerprintDataObject implements Serializable {
    private String title;
    private String sub_title;
    private String amout;
    private String dialog_title;
    private String dialog_sub_title;
    private String cancel_text;
    private String pincode_text;
    private String touch_sensor_text;
    private String warning_text;
    private String warning_url;
    private String help_text;
    private String help_url;
    private String terms_and_condition;
    private String terms_and_condition_url;


    public String getTitle() {

        return checkNotNull(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return checkNotNull(sub_title);
    }

    public void setSubTitle(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getAmout() {
        return checkNotNull(amout);
    }

    public void setAmout(String amout) {
        this.amout = amout;
    }

    public String getDialogTitle() {
        return checkNotNull(dialog_title);
    }

    public void setDialogTitle(String dialog_title) {
        this.dialog_title = dialog_title;
    }

    public String getDialogSubTitle() {
        return checkNotNull(dialog_sub_title);
    }

    public void setDialogSubTitle(String dialog_sub_title) {
        this.dialog_sub_title = dialog_sub_title;
    }

    public String getCancelText() {
        return checkNotNull(cancel_text);
    }

    public void setCancelText(String cancel_text) {
        this.cancel_text = cancel_text;
    }

    public String getPincodeText() {
        return checkNotNull(pincode_text);
    }

    public void setPincodeText(String pincode_text) {

        this.pincode_text = pincode_text;
    }

    public String getTouchSensor_text() {
        return checkNotNull(touch_sensor_text);
    }

    public void setTouchSensorText(String touch_sensor_text) {
        this.touch_sensor_text = touch_sensor_text;
    }

    public String getWarningText() {
        return checkNotNull(warning_text);
    }

    public void setWarningText(String warning_text) {
        this.warning_text = warning_text;
    }

    public String getWarningUrl() {
        return checkNotNull(warning_url);
    }

    public void setWarningUrl(String warning_url) {
        this.warning_url = warning_url;
    }

    public String getHelpText()
    {
        return checkNotNull(help_text);
    }

    public void setHelpText(String help_text) {
        this.help_text = help_text;
    }

    public String getHelpUrl() {
        return checkNotNull( help_url );
    }

    public void setHelpUrl(String help_url) {
        this.help_url = help_url;
    }

    public String getTermsAndConditionUrl() {
        return checkNotNull(terms_and_condition_url);
    }

    public void setTermsAndCondition(String terms_and_condition) {
        this.terms_and_condition = terms_and_condition;
    }

    public String getTermsAndCondition() {
        return checkNotNull(terms_and_condition);
    }

    public void setTermsAndConditionUrl(String terms_and_condition_url) {
        this.terms_and_condition_url = terms_and_condition_url;
    }

    public String checkNotNull(String text){
        if(text!=null)
            return text;

        return "";

    }
}
