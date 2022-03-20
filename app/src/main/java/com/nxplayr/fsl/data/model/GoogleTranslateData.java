package com.nxplayr.fsl.data.model;

import java.util.List;

public class GoogleTranslateData {

    private List<LanguageDetection> languages = null;
    private List<List<Detection>> detections = null;
    private List<Translation> translations = null;

    public List<LanguageDetection> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageDetection> languages) {
        this.languages = languages;
    }

    public List<List<Detection>> getDetections() {
        return detections;
    }

    public void setDetections(List<List<Detection>> detections) {
        this.detections = detections;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}
