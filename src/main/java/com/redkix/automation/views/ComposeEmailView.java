package com.redkix.automation.views;

import com.redkix.automation.RedkixAppView;
import com.redkix.automation.model.Email;
import com.redkix.automation.views.menu.EmojiMenu;
import com.redkix.automation.views.menu.TextEditorToolbar;
import com.redkix.automation.views.menu.TextEditorToolbar.EditorAction;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.redkix.automation.views.ComposeEmailView.ComposeEmailField.CC;
import static com.redkix.automation.views.ComposeEmailView.ComposeEmailField.TO;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;


public class ComposeEmailView extends RedkixAppView {


    public enum ComposeEmailField {
        TO,
        CC,
        BCC
    }

    private static final By COMPOSE_VIEW = By.className("compose-modal");
    private static final By TO_FIELD = By.cssSelector("#to-input input");
    private static final By CC_BCC_BTN = By.cssSelector("[ng-click=\"toggleCcBcc()\"]");
    private static final By CC_FIELD = By.cssSelector("#cc-input input");
    private static final By BCC_FIELD = By.cssSelector("#bcc-input input");
    private static final By SUBJECT_FIELD = By.id("composeDialogSubject");
    private static final By EMAIL_BODY_AREA = By.id("compose");
    private static final By EMAIL_BODY_LINES = By.cssSelector("#compose p");
    private static final By SEND_BTN = By.id("sendBtn");
    private static final By TO_FIELD_CURSOR = By.cssSelector("#to-input .tags.focused");
    private static final By TO_FIELD_TAGS = By.cssSelector("#to-input .tag-list > li");
    private static final By CC_FIELD_TAGS = By.cssSelector("#cc-input .tag-list > li");
    private static final By BCC_FIELD_TAGS = By.cssSelector("#bcc-input .tag-list > li");

    private static final By EMOJI_BUTTON = By.cssSelector("#bottomButtonDiv [data-content=\"emojis\"]");
    private static final By ATTACH_FILE_BUTTON = By.cssSelector("input[type='file'][aria-label='Attach Files']");

    /* Attachments */
    private static final By ATTACHMENT_CONTAINER = By.tagName("rx-nm-attachment");
    private static final By ATTACHMENT_FILENAME = By.className("nm-filename");
    private static final By ATTACHMENT_UPLOAD_PROGRESS_BAR = By.tagName("md-progress-linear");
    private static final By ATTACHMENT_SIZE = By.className("file-size");
    private static final By REMOVE_ATTACHMENT_BUTTON = By.cssSelector("[ng-click=\"ctrl.attachment.remove()\"]");

    @Override
    protected void waitTillReady() {
        new WebDriverWait(getDriver(), WAIT_TIMEOUT).until(ready());
    }

    private TextEditorToolbar editorToolbar;
    private EmojiMenu emojiMenu;

    public ComposeEmailView(WebDriver driver) {
        super(driver);
    }

    public void sendNewEmail(Email email) {
        fillEmailDetails(email);
        clickSendButton();
    }

    public void clickSendButton() {
        waiter().until(not(attributeContains(SEND_BTN, "class", "btn-send-disabled")));
        element(SEND_BTN).click();
        LOGGER.info("'Send' button clicked");
    }

    public void fillEmailDetails(Email email) {
        if (email.getRecipient() != null) {
            enterValue(TO_FIELD, email.getRecipient(), "'To'");
        }

        if (email.getCcRecipients().size() > 0 || email.getBccRecipients().size() > 0) {
            clickCcBccButton();
        }

        if (email.getCcRecipients().size() > 0) {
            String ccValue = email.getCcRecipients().stream().collect(Collectors.joining(" "));
            enterValue(CC_FIELD, ccValue, "'Cc'");
        }

        if (email.getBccRecipients().size() > 0) {
            String bccValue = email.getBccRecipients().stream().collect(Collectors.joining(" "));
            enterValue(BCC_FIELD, bccValue, "'Bcc'");
        }

        enterValue(SUBJECT_FIELD, email.getSubject(), "'Subject'");

        if (email.getBody() != null) {
            enterValue(EMAIL_BODY_AREA, email.getBody(), "'Body'");
        }
    }

    public void clickCcBccButton() {
        element(CC_BCC_BTN).click();
        LOGGER.info("'Cc/Bcc' button clicked");
    }

    public boolean isToFieldFocused() {
        try {
            return element(TO_FIELD_CURSOR).isDisplayed();
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    protected By getViewIdentifier() {
        return COMPOSE_VIEW;
    }

    public void pressEsc() {
        element(TO_FIELD).sendKeys(Keys.ESCAPE);
    }

    public boolean isHidden() {
        try {
            waiter().until(invisibilityOfElementLocated(COMPOSE_VIEW));
            return true;
        }
        catch (TimeoutException e) {
            return false;
        }
    }

    public List<String> getEmailsFromField(ComposeEmailField field) {
        By locator = field == TO ? TO_FIELD_TAGS : field == CC ? CC_FIELD_TAGS : BCC_FIELD_TAGS;
        return findAll(locator).stream().
                map(WebElementFacade::getText).
                collect(Collectors.toList());
    }

    public void enterValueInField(ComposeEmailField field, CharSequence... value) {
        By locator = field == TO ? TO_FIELD : field == CC ? CC_FIELD : BCC_FIELD;
        element(locator).sendKeys(value);
        LOGGER.info("{} entered in '{}' field", value, field);
    }

    public void clearField(ComposeEmailField field) {
        By locator = field == TO ? TO_FIELD : field == CC ? CC_FIELD : BCC_FIELD;
        element(locator).clear();
        LOGGER.info("'{}' field cleared", field);
    }

    public void addEmoji(String emoji) {
        element(EMOJI_BUTTON).click();
        LOGGER.info("'Emoji' button clicked");

        emojiMenu.selectEmoji(emoji);
    }

    public void addFile(File file) {
        List<WebElementFacade> attachFileButtons = findAll(ATTACH_FILE_BUTTON);

        if (attachFileButtons.size() != 2) {
            throw new RuntimeException("Unexpected quantity of 'Attach file' buttons");
        }

        WebElementFacade attachFileButton = attachFileButtons.get(1);

        if (!attachFileButton.isDisplayed()) {
            getJavascriptExecutorFacade().executeScript(
                    "arguments[0].style.zIndex='11000';" +
                            "arguments[0].style.visibility='visible';" +
                            "arguments[0].style.left='600px';" +
                            "arguments[0].style.top='300px';" +
                            "arguments[0].style.width='50px';" +
                            "arguments[0].style.height='50px'", attachFileButton);
        }

        upload(file.getAbsolutePath()).useRemoteDriver(isRemoteDriver()).to(attachFileButton);
    }

    public void sendKeysToBody(CharSequence... keys) {
        element(EMAIL_BODY_AREA).sendKeys(keys);
    }

    public void applyExtraToLastWord(String word, EditorAction action, String... href) {
        CharSequence[] keys = new CharSequence[word.length() + 1];
        Arrays.fill(keys, Keys.LEFT);
        keys[0] = Keys.SHIFT;
        withAction().sendKeys(Keys.chord(keys)).build().perform();
        editorToolbar.apply(action, href);
        withAction().sendKeys(Keys.CLEAR).perform();
        element(EMAIL_BODY_AREA).click();
    }

    public List<String> getAttachments() {
        return findAll(ATTACHMENT_CONTAINER).stream().
                filter(WebElement::isDisplayed).
                filter(a -> a.findElements(ATTACHMENT_UPLOAD_PROGRESS_BAR).stream().noneMatch(WebElement::isDisplayed)).
                map(a -> a.findElement(ATTACHMENT_FILENAME).getText()).
                collect(Collectors.toList());
    }

    public void removeAttachment(String fileName) {
        WebElement attachmentElement = findAll(ATTACHMENT_CONTAINER).stream().
                filter(WebElement::isDisplayed).
                filter(a -> fileName.equals(a.findElement(ATTACHMENT_FILENAME).getText())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("Attachment " + fileName + " is not found"));

        attachmentElement.findElement(REMOVE_ATTACHMENT_BUTTON).click();
        LOGGER.info("'x' (remove) button clicked for attachment");
    }

    public boolean isRemoteDriver() {
        return ((WebDriverFacade) getDriver()).getProxiedDriver() instanceof RemoteWebDriver;
    }






}
