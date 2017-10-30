package com.redkix.automation.helper;


import com.redkix.automation.model.Email;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EmailHelper {

    private static final int SUBJECT_LENGTH = 10;
    private static final int BODY_LINES_COUNT = 5;
    private static final int BODY_LINE_LENGTH = 20;

    public static Email createEmail() {
        String body = IntStream.range(0, BODY_LINES_COUNT).
                mapToObj(i -> RandomStringUtils.randomAlphabetic(BODY_LINE_LENGTH)).
                collect(Collectors.joining("\n"));

        return new Email().
                setSubject(RandomStringUtils.randomAlphabetic(SUBJECT_LENGTH)).
                setBody(body.toString());
    }

    public static Email createEmail(String recipient) {
        return createEmail().setRecipient(recipient);
    }
}
