package com.redkix.automation.steps;

import com.redkix.automation.views.SettingsView;
import com.redkix.automation.views.main.NavigationView;
import net.thucydides.core.annotations.Step;

public class SettingsSteps extends RedkixScenarioSteps {

    private SettingsView settingsView;
    private NavigationView navigationView;

    @Step
    public void openSettings() {
        navigationView.openSettingsView();
    }

    @Step
    public void closeSettings() {
        settingsView.close();
    }

    @Step
    public void saveSettings() {
        settingsView.clickDone();
    }

    @Step
    public void setPriorityInbox(boolean state) {
        settingsView.setPriorityInbox(state);
    }

}
