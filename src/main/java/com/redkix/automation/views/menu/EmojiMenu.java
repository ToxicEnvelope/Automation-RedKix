package com.redkix.automation.views.menu;

import com.google.common.base.Objects;
import com.redkix.automation.RedkixAppView;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class EmojiMenu extends RedkixAppView {

    private static final By EMOJI_CONTAINER = By.className("emoji-positioning");
    private static final By SINGLE_EMOJI_CONTAINER = By.cssSelector(".emoji-container .single-emoji");

    public EmojiMenu(WebDriver driver) {
        super(driver);
    }

    public void selectEmoji(String emoji) {
        WebElement emojiIcon = findAll(SINGLE_EMOJI_CONTAINER).stream().
                filter(e -> Objects.equal(e.getAttribute("innerText"), emoji)).
                findFirst().
                orElseThrow(() -> new NoSuchElementException(emoji + " is not available"));

        getJavascriptExecutorFacade().executeScript("arguments[0].click()", emojiIcon);

        LOGGER.info("'{}' clicked", emoji);
    }

    @Override
    protected By getViewIdentifier() {
        return EMOJI_CONTAINER;
    }
}
