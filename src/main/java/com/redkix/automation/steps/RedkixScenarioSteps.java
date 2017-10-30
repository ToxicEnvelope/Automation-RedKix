package com.redkix.automation.steps;

import com.google.common.base.Predicate;
import com.redkix.automation.views.messages.DesktopNotificationsMessage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.steps.ScenarioSteps;
import org.openqa.selenium.Dimension;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


public class RedkixScenarioSteps extends ScenarioSteps {

    public static final int WAIT_RESULT_TIMEOUT = 60;

    private DesktopNotificationsMessage desktopNotificationsMessage;

    protected <T> boolean waitForResult(Predicate<T> predicate, T input) {
        Instant start = Instant.now();

        while (Duration.between(start, Instant.now()).getSeconds() < WAIT_RESULT_TIMEOUT) {
            if (predicate.test(input)) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignore) {}
        }

        return false;
    }

    @Step
    public void minimizeBrowser() {
        getDriver().manage().window().setSize(new Dimension(0, 0));
    }

    @Step
    public void maximizeBrowser() {
        getDriver().manage().window().maximize();
    }

    @Step
    public void reloadApplication() {
        getDriver().navigate().refresh();
    }

    @Step
    public void clickNotNowInTheEnableDesktopNotificationsMessage() {
        desktopNotificationsMessage.clickButton(DesktopNotificationsMessage.Button.NOT_NOW);
    }
}
